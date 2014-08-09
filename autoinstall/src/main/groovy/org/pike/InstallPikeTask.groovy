package org.pike

import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.logging.ProgressLoggerFactory
import org.pike.autoinstall.AutoinstallWorker
import org.pike.autoinstall.PropertyChangeProgressLogging
import org.pike.model.host.Host
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.IOperatingsystemProvider
import org.pike.remoting.CommandBuilder
import org.pike.remoting.IRemoting
import org.pike.remoting.RemoteTask
import org.pike.remoting.SshRemoting

/**
 * Created by OleyMa on 01.08.14.
 */
class InstallPikeTask extends RemoteTask {

    File installPathRoot

    private AutoinstallWorker logic = new AutoinstallWorker()

    protected IRemoting getRemoting () {
        return new SshRemoting()
    }

    @TaskAction
    public void install () {



        installPathRoot = AutoinstallUtil.getInstallPath(project)

        String installerFileNotFound = ""

        for (Host host: getHostsToBuild()) {
            File installerFile = getInstallerFile(host)
            if (! installerFile.exists())
                installerFileNotFound += "- Installerfile ${installerFile.absolutePath} for host $host.name (operatingsystem $host.operatingsystem.name) not available\n"
        }

        if (! installerFileNotFound.trim().isEmpty())
            throw new GradleException(installerFileNotFound)

        Collection<Host> hosts = getHostsToBuild()
        for (Host host: hosts) {
            IRemoting remoting = getRemoting()

            File installerFile = getInstallerFile(host)

            Operatingsystem os = host.operatingsystem
            IOperatingsystemProvider osprovider = os.provider
            PropertyChangeProgressLogging progressLogging = new PropertyChangeProgressLogging(services.get(ProgressLoggerFactory), InstallPikeTask)

            println ("Start installing pike on host " + host.name + "- Operatingsystem " + os.name + "- Group " + group)

            progressLogging.progressLogger.setDescription("Start configuring host $host.hostname")
            progressLogging.start("Start configuring host $host.hostname ")

            remoting.configure(project, host)

            logic.operatingsystem = host.operatingsystem

            String pikeDirRemote = logic.getPikeDirRemote(host)

            //initialize paths

            CommandBuilder builderInitializePaths = remoting.createCommandBuild(host)
            builderInitializePaths = builderInitializePaths.addCommand(osprovider.bootstrapCommandRemovePath, pikeDirRemote)
            builderInitializePaths = builderInitializePaths.addCommand(osprovider.bootstrapCommandMakePath, pikeDirRemote)
            builderInitializePaths = builderInitializePaths.addCommand(osprovider.bootstrapCommandMakeWritablePath, pikeDirRemote)
            remoting.execCmd(builderInitializePaths.get())

            //TODO upload unzip in windows


            //upload installer
            remoting.upload(pikeDirRemote, installerFile, progressLogging)


            //Unzip the installer file remote
            String remoteZipFile = osprovider.addPath(pikeDirRemote, installerFile.name)
            CommandBuilder builderUnzipInstaller = remoting.createCommandBuild(host)
            builderUnzipInstaller = builderUnzipInstaller.addCommand(osprovider.bootstrapCommandChangePath, pikeDirRemote, false)
            builderUnzipInstaller = builderUnzipInstaller.addCommand(osprovider.bootstrapCommandInstall, remoteZipFile)
            remoting.execCmd(builderUnzipInstaller.get())

            remoting.disconnect()
        }




    }



    /**
     * gets installer file for host
     * @param host  host
     * @return installer file
     */
    private File getInstallerFile (final Host host) {
        return new File (installPathRoot, AutoinstallUtil.getInstallerFile(host.operatingsystem) + ".installer")
    }


}
