package org.pike.installers

import org.gradle.api.Project
import org.pike.configuration.OperatingSystem
import org.pike.configuration.PikeExtension

class ToolInstaller {

    HashMap<OperatingSystem, ToolInstallerPlatformDetails> platformDetailsHashMap = new HashMap<OperatingSystem, ToolInstallerPlatformDetails>()

    Project project

    String name

    String version

    File installationPath

    InstallerFactory installerFactory = new InstallerFactory()

    ToolInstallerPlatformDetails getPlatformDetails (OperatingSystem operatingSystem) {
        ToolInstallerPlatformDetails platformDetails =  platformDetailsHashMap.get(operatingSystem)
        if (platformDetails == null)
            throw new IllegalStateException("No platformdetails for operatingsystem " + operatingSystem.name() + " found")
        return platformDetails
    }

    void install(OperatingSystem operatingSystem) {

        project.logger.info("Install " + name + " on operatingsystem " + operatingSystem.name())

        PikeExtension pikeExtension = project.extensions.pike

        ToolInstallerPlatformDetails platformDetails = getPlatformDetails(operatingSystem)
        Download download = new Download()
        download.project = project
        download.source = platformDetails.url
        download.fileType = platformDetails.fileType
        download.toDir = project.file('build/pike/tools/' + name + 'Downloaded')
        download.force = pikeExtension.force
        download.executeDownload()

        File downloadedFile = download.downloadedFile


        Installer installer = installerFactory.getInstaller(downloadedFile)
        installer.project = project
        installer.install(installationPath, downloadedFile)

    }
}
