package org.pike.utils

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import org.pike.PikePlugin
import org.pike.exceptions.MissingConfigurationException

class ConfigureUtilsTest {

    @Test(expected = MissingConfigurationException)
    public void getBasePath () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        ConfigureUtils configureUtils = new ConfigureUtils();
        configureUtils.getBasePath(project, null )

    }
}
