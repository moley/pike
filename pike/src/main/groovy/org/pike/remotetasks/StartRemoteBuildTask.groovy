package org.pike.remotetasks

import groovy.util.logging.Log4j
import org.gradle.api.GradleException
import org.gradle.api.internal.tasks.options.Option
import org.gradle.api.tasks.TaskAction
import org.gradle.logging.ProgressLoggerFactory
import org.pike.autoinstall.AutoinstallWorker
import org.pike.autoinstall.PropertyChangeProgressLogging
import org.pike.model.environment.Environment
import org.pike.model.host.Host
import org.pike.os.IOperatingsystemProvider
import org.pike.remoting.IRemoting
import org.pike.remoting.RemoteResult
import org.pike.remoting.RemoteTask

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 30.04.13
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */
@Log4j
class StartRemoteBuildTask extends RemoteTask {

    private AutoinstallWorker logic = new AutoinstallWorker()

    private String env

    @Option(option='env', description='environment to be configured (default: all related envs)')
    public void setEnv(String env) {
        this.env = env
        log.info("set env to " + env)
    }

    public String getEnv () {

        if (env != null) {
            Environment environment = findEnvByName(project, env)
            String envname = environment.name
            String taskname = "install" + Character.toString(envname.charAt(0)).toUpperCase()+envname.substring(1)
            return taskname
        }
        else
          return "install"
    }



    @TaskAction
    public void startRemoteBuild () {

        Collection <RemoteResult> results = new ArrayList<RemoteResult>()

        Collection<Host> hosts = getHostsToBuild()

        for (Host nextHost: hosts) {

            println ("Start remote build on host " + nextHost.name + "- Operatingsystem " + nextHost.operatingsystem.name + "- Group " + group + " with env " + getEnv())

            PropertyChangeProgressLogging progressLogging = new PropertyChangeProgressLogging(services.get(ProgressLoggerFactory), StartRemoteBuildTask)

            try {

                progressLogging.progressLogger.setDescription("Start configuring host $nextHost.hostname")
                progressLogging.start("Start configuring host $nextHost.hostname")

                String pikeDir = file (nextHost.operatingsystem.pikedir).absolutePath
                if (pikeDir == null)
                    throw new IllegalStateException("PikeDir on host " + nextHost.hostname + " not set")

                nextHost.remotingImpl.configure(project, nextHost)

                logic.operatingsystem = nextHost.operatingsystem
                logic.uploadBootstrapScripts(project, nextHost, progressLogging)
                logic.uploadProjectDescriptions(project, nextHost, progressLogging)
                logic.uploadPlugins(project, nextHost, progressLogging)


                IOperatingsystemProvider osProvider = nextHost.operatingsystem.provider

                String command = ""
                String pikeDirRemote = logic.getPikeDirRemote(nextHost)
                command = logic.addCommand(osProvider, command, osProvider.bootstrapCommandChangePath, pikeDirRemote)
                command = logic.addCommand(osProvider, command, osProvider.bootstrapCommandStartConfigure, pikeDirRemote)
                command += " " + getEnv()
                if (project.logger.debugEnabled)
                    command += " --debug"

                if (project.logger.infoEnabled)
                    command += " --info"

                println ("Complete command to configure remotely: " + command)
                nextHost.remotingImpl.execCmd(command)



            } catch (Exception e) {
                log.error("Could not installPike host $nextHost due to error ${e.toString()}", e)
            } finally {
                progressLogging.end()
            }
        }

        logic.disconnectConnections(hosts)

        int numberOfErrors = 0
        String errorMessage = ""
        for (RemoteResult nextResult: results) {
            if (nextResult.isOk())
              errorMessage += "- ${nextResult.host}: OK\n"
            else {
              numberOfErrors++
              errorMessage += "- ${nextResult.host}: FAILURE\n"
            }
        }

        if (numberOfErrors > 0)
            throw new GradleException(numberOfErrors + " occurred while configuring modules !\n " + errorMessage)
    }
}
