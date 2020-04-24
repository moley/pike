package org.pike.installers

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.configuration.OperatingSystem

class ToolInstallerBuilderTest {


    @Test
    public void globalUrl () {
        Project project = ProjectBuilder.builder().build()
        ToolInstallerBuilder toolBuilder = new ToolInstallerBuilder(project, 'name', 'version')
        toolBuilder.platform(OperatingSystem.LINUX).url(OperatingSystem.LINUX.name())
        toolBuilder.all().url("all")

        ToolInstaller toolInstallerFromGlobal = toolBuilder.get(OperatingSystem.MACOS)
        ToolInstallerPlatformBuilder operatingSystemFromGlobal = toolInstallerFromGlobal.operatingSystemPlatformBuilder
        Assert.assertEquals ("Default URL incorrect", "all", operatingSystemFromGlobal.url)

        ToolInstaller toolInstallerFromSpecific = toolBuilder.get(OperatingSystem.LINUX)
        ToolInstallerPlatformBuilder operatingSystemFromSpecific = toolInstallerFromSpecific.operatingSystemPlatformBuilder
        Assert.assertEquals ("Default URL incorrect", OperatingSystem.LINUX.name(), operatingSystemFromSpecific.url)


    }
    @Test
    public void platformDependentUrl () {
        Project project = ProjectBuilder.builder().build()
        ToolInstallerBuilder toolBuilder = new ToolInstallerBuilder(project, 'name', 'version')
        toolBuilder.platform(OperatingSystem.LINUX).url(OperatingSystem.LINUX.name())
        toolBuilder.platform(OperatingSystem.MACOS).url(OperatingSystem.MACOS.name())
        toolBuilder.platform(OperatingSystem.WINDOWS).url(OperatingSystem.WINDOWS.name())
        ToolInstaller toolInstaller = toolBuilder.get(OperatingSystem.MACOS)
        ToolInstallerPlatformBuilder operatingSystemPlatformBuilder = toolInstaller.operatingSystemPlatformBuilder
        Assert.assertEquals ("URL incorrect", OperatingSystem.MACOS.name(), operatingSystemPlatformBuilder.url)
        Assert.assertEquals ("Project invalid", project, toolInstaller.project)
        Assert.assertEquals ("Name invalid", 'name', toolInstaller.name)
        Assert.assertEquals ("Version invalid", 'version', toolInstaller.version)
    }


}
