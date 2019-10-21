package org.pike.installers

import org.gradle.api.Project
import org.pike.configuration.OperatingSystem
import org.pike.configuration.PikeExtension

class Tool {

    HashMap<OperatingSystem, String> urls = new HashMap<OperatingSystem, String>()

    Project project

    String name

    String version

    File installationPath

    InstallerFactory installerFactory = new InstallerFactory()

    private String getUrl (OperatingSystem operatingSystem) {
        String url =  urls.get(operatingSystem)
        if (url == null)
            throw new IllegalStateException("No url for operatingsystem " + operatingSystem.name())

        return url
    }

    void install(OperatingSystem operatingSystem) {

        project.logger.info("Install " + name + " on operatingsystem " + operatingSystem.name())

        PikeExtension pikeExtension = project.extensions.pike

        String remoteUrl = getUrl(operatingSystem)
        Download download = new Download()
        download.project = project
        download.source = remoteUrl
        download.toDir = project.file('build/pike/tools/' + name + 'Downloaded')
        download.force = pikeExtension.force
        download.executeDownload()

        File downloadedFile = download.downloadedFile

        File installPath = project.file('/Applications')

        Installer installer = installerFactory.getInstaller(downloadedFile)
        installer.project = project
        installer.install(installPath, downloadedFile)

    }
}
