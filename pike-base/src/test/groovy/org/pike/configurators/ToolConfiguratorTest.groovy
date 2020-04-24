package org.pike.configurators

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import org.pike.configuration.OperatingSystem

class ToolConfiguratorTest {

    @Test(expected = IllegalStateException)
    public void platformDetails () {
        Project project = ProjectBuilder.builder().build()
        ToolConfiguratorBuilder builder = new ToolConfiguratorBuilder(project, "name")
        ToolConfigurator toolConfigurator = builder.get()
        toolConfigurator.getPlatformDetails(OperatingSystem.LINUX)

    }
}
