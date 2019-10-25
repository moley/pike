package org.pike.installers

import org.gradle.api.Project
import org.pike.configuration.FileType
import org.pike.configuration.OperatingSystem


class ToolInstallerBuilder {

    private ToolInstaller tool = new ToolInstaller()

    Project project

    File installationPath

    public ToolInstallerBuilder(Project project, final String name, final String version) {
        this.project = project
        this.tool.project = project
        this.tool.name = name
        this.tool.version = version
    }

    public ToolInstallerBuilder installationPath (final File installationpath) {
        this.tool.installationPath = installationpath
        return this
    }


    public ToolInstallerBuilder platformDetails(final OperatingSystem operatingSystem, final String url, final FileType suffix = null) {
        ToolInstallerPlatformDetails platformDetails = new ToolInstallerPlatformDetails()
        platformDetails.url = url
        platformDetails.suffix = suffix
        tool.platformDetailsHashMap.put(operatingSystem, platformDetails)
    }

    public ToolInstaller get(){
        return tool
    }





}
