package org.pike.installers

import org.gradle.api.Project
import org.pike.configuration.OperatingSystem


class ToolBuilder {

    private Tool tool = new Tool()

    Project project

    File installationPath

    public ToolBuilder(Project project, final String name, final String version) {
        this.project = project
        this.tool.project = project
        this.tool.name = name
        this.tool.version = version
    }

    public ToolBuilder withInstallationPath (final File installationpath) {
        this.tool.installationPath = installationpath
        return this
    }


    public ToolBuilder tool(final OperatingSystem operatingSystem, final String basepath) {
        tool.urls.put(operatingSystem, basepath)
    }

    public Tool get(){
        return tool
    }





}
