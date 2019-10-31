package org.pike.configurators

import org.gradle.api.Project
import org.pike.configuration.OperatingSystem

class ToolConfiguratorBuilder {

    private ToolConfigurator tool = new ToolConfigurator()

    Project project

    public ToolConfiguratorBuilder(Project project, final String name) {
        this.project = project
        this.tool.project = project
        this.tool.name = name
    }

    public ToolConfiguratorBuilder platformDetails(final OperatingSystem operatingSystem, final String configurationPath) {
        ToolConfiguratorPlatformDetails platformDetails = new ToolConfiguratorPlatformDetails()
        platformDetails.globalConfigurationPath = configurationPath
        tool.platformDetailsHashMap.put(operatingSystem, platformDetails)
        return this
    }

    public ToolConfigurator get(){
        return tool
    }





}
