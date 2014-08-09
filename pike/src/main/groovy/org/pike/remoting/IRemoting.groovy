package org.pike.remoting

import org.gradle.api.Project
import org.pike.autoinstall.PropertyChangeProgressLogging
import org.pike.model.host.Host

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 02.05.13
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */
public interface IRemoting {

    /**
     * upload a file
     * @param toDir   to remote dir
     * @param from    from file
     * @param logging  progress logging
     */
    void upload (String toDir, File from, PropertyChangeProgressLogging logging)

    /**
     * execute a command
     * @param cmd
     * @return
     */
    RemoteResult execCmd(String cmd)

    /**
     * disconnect
     */
    void disconnect ()

    /**
     * checks if the connection is connected to the host
     * @param host host
     * @return true: connected, false: not connected
     */
    boolean connectedToHost (Host host)

    /**
     * configures the connection
     * @param project  project
     * @param host host to connect to
     */
    void configure (Project project, Host host)

    /**
     * creates command builder
     * @param host  host
     * @return commandbuilder
     * @return commandbuilder
     */
    CommandBuilder createCommandBuild (Host host)

    /**
     * getter
     * @return user
     */
    String getUser ()
}