package org.pike

import groovy.util.logging.Slf4j
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.logging.ProgressLoggerFactory
import org.pike.autoinstall.PropertyChangeProgressLogging
import org.pike.model.Autoinstall
import org.pike.model.host.Host
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.IOperatingsystemProvider
import org.pike.os.LinuxProvider
import org.pike.os.WindowsProvider
import org.pike.remoting.CommandBuilder
import org.pike.remoting.IRemoting
import org.pike.remoting.RemoteTask
import org.pike.remoting.SshRemoting

import java.nio.file.Files
import java.nio.file.Path

/**
 * Created by OleyMa on 01.08.14.
 */
@Slf4j
class InstallPikeTask extends RemoteTask {

    File installPathRoot


    protected IRemoting getRemoting () {
        return new SshRemoting()
    }

    @TaskAction
    public void install () {

        Autoinstall autoinstall = project.autoinstall


        installPathRoot = AutoinstallUtil.getInstallPath(project)

        String installerFileNotFound = ""

        for (Host host: getHostsToBuild()) {
            File installerFile = getInstallerFile(host, autoinstall)
            if (! installerFile.exists())
                installerFileNotFound += "- Installerfile ${installerFile.absolutePath} for host $host.name (operatingsystem $host.operatingsystem.name) not available\n"
        }

        if (! installerFileNotFound.trim().isEmpty())
            throw new GradleException(installerFileNotFound)

        Collection<Host> hosts = getHostsToBuild()
        for (Host host: hosts) {
            IRemoting remoting = getRemoting()

            File installerFile = getInstallerFile(host, autoinstall)

            Operatingsystem os = host.operatingsystem
            IOperatingsystemProvider osprovider = os.provider
            PropertyChangeProgressLogging progressLogging = new PropertyChangeProgressLogging(services.get(ProgressLoggerFactory), InstallPikeTask)

            println ("Start installing pike on host " + host.name + "- Operatingsystem " + os.name + "- Group " + group)

            progressLogging.progressLogger.setDescription("Start configuring host $host.hostname")
            progressLogging.start("Start configuring host $host.hostname ")

            remoting.configure(project, host)

            String pikeDirRemote = AutoinstallUtil.getPikeDirRemote(host)

            //initialize paths
            CommandBuilder builderInitializePaths = remoting.createCommandBuild(host)
            builderInitializePaths = builderInitializePaths.addCommand(osprovider.bootstrapCommandRemovePath, pikeDirRemote)
            builderInitializePaths = builderInitializePaths.addCommand(osprovider.bootstrapCommandMakePath, pikeDirRemote)
            builderInitializePaths = builderInitializePaths.addCommand(osprovider.bootstrapCommandMakeWritablePath, pikeDirRemote)
            remoting.execCmd(builderInitializePaths.get())

            //Copy unzip.exe if windows
            if (host.operatingsystem.provider instanceof WindowsProvider) {
                URL url = getClass().getResource('/installation/unzip.exe')
                if (url != null) {
                    InputStream is = url.openStream()

                    File unzipFile = project.file('build/unzip.exe')
                    if (! unzipFile.exists()) {
                        log.info("Copy $url.file to ${unzipFile.absolutePath}")
                        Files.copy(is, unzipFile.toPath())
                    }
                    remoting.upload(pikeDirRemote, unzipFile, progressLogging)
                } else
                    throw new IllegalStateException('Could not find unzip.exe')
            }

            //upload installer
            remoting.upload(pikeDirRemote, installerFile, progressLogging)

            //Unzip the installer on remote host
            String remoteZipFile = osprovider.addPath(pikeDirRemote, installerFile.name)
            CommandBuilder builderUnzipInstaller = remoting.createCommandBuild(host)
            builderUnzipInstaller = builderUnzipInstaller.addCommand(false, osprovider.bootstrapCommandChangePath, pikeDirRemote)
            builderUnzipInstaller = builderUnzipInstaller.addCommand(osprovider.bootstrapCommandInstall, remoteZipFile)
            remoting.execCmd(builderUnzipInstaller.get())

            //Adapt the user
            if (host.operatingsystem.provider instanceof LinuxProvider) {
                CommandBuilder builderAdaptUser = remoting.createCommandBuild(host)
                builderAdaptUser = builderAdaptUser.addCommand(osprovider.bootstrapCommandAdaptUser, remoting.user, remoting.group, pikeDirRemote)
                remoting.execCmd(builderAdaptUser.get())
            }

            remoting.disconnect()
        }




    }



    /**
     * gets installer file for host
     * @param host  host
     * @return installer file
     */
    public File getInstallerFile (final Host host, Autoinstall autoinstall) {
        Operatingsystem os = AutoinstallUtil.getInstallerOs(autoinstall, host)
        return new File (installPathRoot, AutoinstallUtil.getInstallerFile(os) + ".installer")
    }


}
