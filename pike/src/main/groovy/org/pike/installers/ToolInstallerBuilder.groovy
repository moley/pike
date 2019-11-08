package org.pike.installers

import org.gradle.api.Project
import org.pike.configuration.OperatingSystem

public class ToolInstallerBuilder {

    Project project

    String name

    String version

    boolean installationPathMustExist

    private ToolInstallerPlatformBuilder allPlatformInstallerBuilder = new ToolInstallerPlatformBuilder()

    private HashMap<OperatingSystem, ToolInstallerPlatformBuilder> platformInstallerBuilders = new HashMap<OperatingSystem, ToolInstallerPlatformBuilder>()

    public ToolInstallerBuilder(Project project, final String name, final String version) {
        this.project = project
        this.name = name
        this.version = version
    }

    public ToolInstallerPlatformBuilder platform (final OperatingSystem operatingSystem) {
        ToolInstallerPlatformBuilder platformBuilder = platformInstallerBuilders.get(operatingSystem)
        if (platformBuilder == null) {
            platformBuilder = new ToolInstallerPlatformBuilder()
            platformInstallerBuilders.put(operatingSystem, platformBuilder)
        }

        return platformBuilder
    }

    public ToolInstallerPlatformBuilder all () {
        return allPlatformInstallerBuilder
    }

    public ToolInstallerBuilder installationPathMustExist () {
        this.installationPathMustExist = true
        return this
    }

    public ToolInstaller get(OperatingSystem operatingSystem){
        return new ToolInstaller(this, operatingSystem)
    }





}
