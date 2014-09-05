package org.pike

import groovy.util.logging.Log4j
import org.gradle.api.GradleException
import org.gradle.api.file.FileTree
import org.gradle.api.internal.tasks.options.Option
import org.gradle.api.tasks.TaskAction
import org.gradle.logging.ProgressLoggerFactory
import org.pike.autoinstall.PropertyChangeProgressLogging
import org.pike.model.environment.Environment
import org.pike.model.host.Host
import org.pike.os.IOperatingsystemProvider
import org.pike.remoting.*

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 30.04.13
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */
@Log4j
class StartRemoteBuildTask extends RemoteTask {

    private String env

    protected File minimalInstallPackage = new File (project.buildDir, 'minimalinstall')

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

    protected IRemoting getRemoting () {
        return new SshRemoting()
    }

    /**
     * copies provision scripts
     **/
    private void copyProvisioningDefinitions() {
        project.copy {
            from(project.projectDir)
            include '**/*.gradle'
            exclude 'build.gradle'
            exclude 'build/**'
            into(minimalInstallPackage)
            includeEmptyDirs false
        }
    }




    @TaskAction
    public void startRemoteBuild () {

        Collection <RemoteResult> results = new ArrayList<RemoteResult>()

        Collection<Host> hosts = getHostsToBuild()

        copyProvisioningDefinitions()

        for (Host nextHost: hosts) {

            String hostname = nextHost.name


            println ("Start remote build on host $hostname - Operatingsystem $nextHost.operatingsystem.name - Group " + group + " with env " + getEnv())

            PropertyChangeProgressLogging progressLogging = new PropertyChangeProgressLogging(services.get(ProgressLoggerFactory), StartRemoteBuildTask)

            try {
                progressLogging.progressLogger.setDescription("Provisioning of host $hostname")
                progressLogging.start("Starting provisioning of host $hostname")

                String pikeDir = file (nextHost.operatingsystem.pikedir).absolutePath
                if (pikeDir == null)
                    throw new IllegalStateException("PikeDir on host $hostname not set")

                IRemoting remoting = getRemoting()
                remoting.configure(project, nextHost)

                String pikeDirRemote = AutoinstallUtil.getPikeDirRemote(nextHost)

                progressLogging.progressLogger.progress("Upload provision descriptions to host $hostname")

                FileTree buildscripts = project.fileTree(minimalInstallPackage)
                for (File next: buildscripts.files) {
                    String relativ = (next.toString() - minimalInstallPackage.toString())
                    String to = pikeDirRemote + relativ
                    log.info("Upload file $next.absolutePath to $to on host $hostname")
                    remoting.upload(to, next, progressLogging)
                }


                IOperatingsystemProvider osProvider = nextHost.operatingsystem.provider
                CommandBuilder builder = remoting.createCommandBuild(nextHost)
                builder = builder.addCommand(false, osProvider.bootstrapCommandChangePath, pikeDirRemote)
                builder = builder.addCommand(osProvider.bootstrapCommandStartConfigure, pikeDirRemote)
                String command = builder.get()
                command += " " + getEnv()
                if (project.logger.debugEnabled)
                    command += " --debug"

                if (project.logger.infoEnabled)
                    command += " --info"

                log.info ("Complete command to configure remotely: $command")
                progressLogging.progressLogger.progress("Start provisioning of host $hostname")
                remoting.execCmd(command)

                remoting.disconnect()

                progressLogging.progressLogger.progress("Provisioning of host $hostname is done")

            } catch (Exception e) {
                log.error("Could not provision $hostname due to error ${e.toString()}", e)
            } finally {
                progressLogging.end()
            }
        }

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
