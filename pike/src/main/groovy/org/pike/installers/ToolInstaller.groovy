package org.pike.installers

import org.gradle.api.Project
import org.pike.configuration.OperatingSystem
import org.pike.configuration.PikeExtension
import org.pike.utils.ObjectMergeUtil

class ToolInstaller {

    Project project

    String name

    String version

    InstallerFactory installerFactory = new InstallerFactory()

    OperatingSystem operatingSystem

    boolean force

    Download download = new Download()

    ToolInstallerPlatformBuilder operatingSystemPlatformBuilder

    ObjectMergeUtil<ToolInstallerPlatformBuilder> toolInstallerPlatformBuilderObjectMergeUtil = new ObjectMergeUtil<ToolInstallerPlatformBuilder>()

    boolean installationPathMustExist

    public void setToolInstallerBuilder (final ToolInstallerBuilder toolInstallerBuilder) {
        if (toolInstallerBuilder == null)
            throw new IllegalArgumentException("Parameter 'toolInstallerBuilder' must not be null")

        this.project = toolInstallerBuilder.project
        this.name = toolInstallerBuilder.name
        this.version = toolInstallerBuilder.version
        this.operatingSystemPlatformBuilder = toolInstallerPlatformBuilderObjectMergeUtil.merge(toolInstallerBuilder.all(), toolInstallerBuilder.platform(operatingSystem))
        this.installationPathMustExist = toolInstallerBuilder.installationPathMustExist
    }


    public void setOperatingSystem (final OperatingSystem operatingSystem) {
        if (operatingSystem == null)
            throw new IllegalArgumentException("Parameter 'operatingSystem' must not be null")

        this.operatingSystem = operatingSystem
    }


    public File getInstallationPathOrDefault() {
        File installationPath = operatingSystemPlatformBuilder?.installationPath
        if (installationPath == null) {
            return project.file('build/pike/tools/' + name)
        }

        return installationPath
    }

    File install() {

        project.logger.info("Install " + name + " on operatingsystem " + operatingSystem.name())

        download.name = name
        download.project = project
        download.source = operatingSystemPlatformBuilder.url
        download.fileType = operatingSystemPlatformBuilder.fileType
        download.toDir = project.file('build/pike/tools/' + name + 'Downloaded')
        download.force = force
        download.executeDownload()

        File downloadedFile = download.downloadedFile

        Installer installer = installerFactory.getInstaller(downloadedFile)
        installer.project = project

        File installationPath = installationPathOrDefault
        installationPath = installer.getDefaultInstallationDir(installationPath)


        if (installationPathMustExist && ! installationPath.exists())
            throw new IllegalStateException("Installation path " + installationPath.absolutePath + " is expected to exist, check configuration")

        return installer.install(installationPath, downloadedFile)
    }
}
