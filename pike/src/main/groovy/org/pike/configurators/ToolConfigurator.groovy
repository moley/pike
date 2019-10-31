package org.pike.configurators

import org.gradle.api.Project
import org.pike.configuration.OperatingSystem

class ToolConfigurator {

    Project project

    String name

    HashMap<OperatingSystem, ToolConfiguratorPlatformDetails> platformDetailsHashMap = new HashMap<OperatingSystem, ToolConfiguratorPlatformDetails>()

    ToolConfiguratorPlatformDetails getPlatformDetails (OperatingSystem operatingSystem) {
        ToolConfiguratorPlatformDetails platformDetails =  platformDetailsHashMap.get(operatingSystem)
        if (platformDetails == null)
            throw new IllegalStateException("No platformdetails for operatingsystem " + operatingSystem.name() + " found")
        return platformDetails
    }

}
