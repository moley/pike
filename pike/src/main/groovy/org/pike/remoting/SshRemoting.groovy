package org.pike.remoting

import com.sshtools.j2ssh.ScpClient
import com.sshtools.j2ssh.SftpClient
import com.sshtools.j2ssh.SshClient
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient
import com.sshtools.j2ssh.authentication.PasswordChangePrompt
import com.sshtools.j2ssh.configuration.SshConnectionProperties
import com.sshtools.j2ssh.connection.Channel
import com.sshtools.j2ssh.connection.ChannelEventAdapter
import com.sshtools.j2ssh.connection.ChannelState
import com.sshtools.j2ssh.session.SessionChannelClient
import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.pike.autoinstall.AlwaysTrueHostKeyVerification
import org.pike.autoinstall.PropertyChangeProgressLogging
import org.pike.model.host.Host
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.LinuxProvider

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 30.04.13
 * Time: 10:51
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class SshRemoting implements IRemoting{

    private SshClient client
    private Operatingsystem os
    private String hostname

    private boolean isConfigured



    public void setSshClient (final SshClient client) {
        this.client = client
    }
    /**
     * constructor
     * @param project  project
     * @param host     current host
     */
    public void configure (Project project, Host host) {
        if (isConfigured)
            return
        isConfigured = true

        log.info("Configure remoting for host ${host.name}")

        hostname = host.hostname
        if (! hostname.contains(".") && project.defaults.defaultdomain != null)
            hostname = host.hostname + "." + project.defaults.defaultdomain

        os = host.operatingsystem

        String realPikeUser = host.pikeuser != null ? host.pikeuser : project.defaults.pikeuser
        String realPikePassword = host.pikepassword != null ? host.pikepassword : project.defaults.pikepassword

        SshConnectionProperties props = new SshConnectionProperties()
        props.username = realPikeUser
        props.host = host.ip//hostname
        if (host.pikeport != null)
		  props.port = host.pikePortInt

        println ("Connecting to host <${hostname}>, ip <${host.ip}>, port <${host.pikeport}> with credentials " + realPikeUser + "-" + realPikePassword)

        client = new SshClient()
        client.connect(props, new AlwaysTrueHostKeyVerification())

        PasswordAuthenticationClient pwd = new PasswordAuthenticationClient()

        pwd.setUsername(realPikeUser)
        pwd.setPassword(realPikePassword)
        pwd.passwordChangePrompt = new PasswordChangePrompt() {
            @Override
            String changePassword(String s) {
                println ("Change password " + s)
            }
        }


        int result = client.authenticate(pwd);
        println ("Authentication result " + result + " on host $hostname (user = " + pwd.username + ", pwd = " + pwd.password + ")")

        if (client.connectionState.lastError != null)
            client.connectionState.lastError.printStackTrace()

        if (client.connectionState.disconnectReason != null)
          println ("Reason: " + client.connectionState.disconnectReason)

        if (result != AuthenticationProtocolState.COMPLETE)
            throw new IllegalStateException("Could not establish the connection to host " + hostname + " (connectionState = " + result + ")")


    }

    /**
     * upload toFile
     * @param toDir
     * @param from
     * @param logging
     */
    public void upload (String toDir, File from, PropertyChangeProgressLogging logging) {

        int lastPercentageShown = 0
        int outgoingByteCountOffset = client.outgoingByteCount

        //TODO make configurable
        if (os.provider instanceof LinuxProvider) {
            ScpClient scpclient = new ScpClient(client, false, new ChannelEventAdapter() {
                @Override
                void onDataSent(Channel channel, byte[] bytes) {
                    super.onDataSent(channel, bytes)
                    long realOutgoing = Math.min(((long) client.outgoingByteCount - outgoingByteCountOffset), from.length())
                    long percentage = Math.min((long) realOutgoing * 100 / from.length(), (long) 100)

                    if (lastPercentageShown < percentage) {
                        logging.progressLogger.progress("Copy " + realOutgoing + " of " + from.length() + "bytes (" + percentage + "%)")
                        lastPercentageShown = percentage
                    }
                }
            })

            println ("Copy " + from.absolutePath + " to " + toDir)
            scpclient.put(from.absolutePath, toDir, false)

        } else {

            SftpClient sftpClient = new SftpClient(client, new ChannelEventAdapter() {
                @Override
                void onDataSent(Channel channel, byte[] bytes) {
                    super.onDataSent(channel, bytes)
                    long realOutgoing = Math.min(((long) client.outgoingByteCount - outgoingByteCountOffset), from.length())
                    long percentage = Math.min((long) realOutgoing * 100 / from.length(), (long) 100)

                    if (lastPercentageShown < percentage) {
                        logging.progressLogger.progress("Copy " + realOutgoing + " of " + from.length() + "bytes (" + percentage + "%)")
                        lastPercentageShown = percentage
                    }
                }
            })

            toDir = toDir.replace("C:", "")      //sftp-rootdir
            toDir = toDir.replace("\\", "/")
            println ("Upload " + from.absolutePath + " to "+ hostname + "-" + toDir)
            sftpClient.put(from.absolutePath, toDir)
        }



    }


    public RemoteResult execCmd(String cmd) {
       RemoteResult result

        // The connection is authenticated we can now do some real work!
        SessionChannelClient session = client.openSessionChannel()

        String prefix = client.connectionProperties.host +  "      | "
        String prefixErr = client.connectionProperties.host +  "  ERR | "

        Thread errThread
        Thread inThread

        String output = ""
        try
        {
            if ( session.executeCommand(cmd) ) {


                inThread = Thread.start {
                    session.inputStream.eachLine {
                        output << it
                        println(prefix + it)
                    }
                }

                errThread = Thread.start {
                    session.stderrInputStream.eachLine {
                        output << it
                        println(prefixErr + it)
                    }
                }

                session.state.waitForState(ChannelState.CHANNEL_CLOSED)
                result = new RemoteResult(client.connectionProperties.host, "Command: <$cmd>: $output")
            }
            else
                result = new RemoteResult(client.connectionProperties.host, "Command: <$cmd>: returned with error")

        }
        catch(Exception e) {
            throw new RuntimeException(e)
        } finally  {
            session.close()
            if (inThread != null)
                inThread.interrupt()

            if (errThread != null)
                errThread.interrupt()
        }

        if (! result.ok)
            throw new IllegalStateException("Command " + cmd + " could not be executed on host " + client.connectionProperties.host)

        return result
    }

    public void disconnect () {
        if (client != null)
          client.disconnect()
    }

    @Override
    public boolean connectedToHost (Host host) {
        return client.getConnectionProperties().getHost().equals(host.hostname)
    }
}
