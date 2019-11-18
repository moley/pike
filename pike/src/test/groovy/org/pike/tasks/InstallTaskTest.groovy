package org.pike.tasks

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.PikePlugin
import org.pike.configuration.PikeExtension

class InstallTaskTest {

    @Test
    public void force () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        InstallTask installTask = project.tasks.install
        installTask.setForce(true)

        PikeExtension pikeExtension = project.pike
        Assert.assertTrue ("Force was not set", pikeExtension.force)

    }
}
