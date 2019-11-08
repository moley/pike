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

    ToolInstallerPlatformBuilder operatingSystemPlatformBuilder

    ObjectMergeUtil<ToolInstallerPlatformBuilder> toolInstallerPlatformBuilderObjectMergeUtil = new ObjectMergeUtil<ToolInstallerPlatformBuilder>()

    boolean installationPathMustExist


    public ToolInstaller (final ToolInstallerBuilder toolInstallerBuilder, OperatingSystem operatingSystem) {
        if (toolInstallerBuilder == null)
            throw new IllegalArgumentException("Parameter 'toolInstallerBuilder' must not be null")
        if (operatingSystem == null)
            throw new IllegalArgumentException("Parameter 'operatingSystem' must not be null")

        this.project = toolInstallerBuilder.project
        this.name = toolInstallerBuilder.name
        this.version = toolInstallerBuilder.version
        this.operatingSystem = operatingSystem
        this.operatingSystemPlatformBuilder = toolInstallerPlatformBuilderObjectMergeUtil.merge(toolInstallerBuilder.all(), toolInstallerBuilder.platform(operatingSystem))
        this.installationPathMustExist = toolInstallerBuilder.installationPathMustExist
    }

    public File getInstallationPathOrDefault() {
        File installationPath = operatingSystemPlatformBuilder.installationPath
        if (installationPath == null) {
            return project.file('build/pike/tools/' + name)
        }

        return installationPath
    }

    File install() {

        project.logger.info("Install " + name + " on operatingsystem " + operatingSystem.name())

        PikeExtension pikeExtension = project.extensions.pike

        Download download = new Download()
        download.project = project
        download.source = operatingSystemPlatformBuilder.url
        download.fileType = operatingSystemPlatformBuilder.fileType
        download.toDir = project.file('build/pike/tools/' + name + 'Downloaded')
        download.force = pikeExtension.force
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
