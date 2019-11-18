package org.pike.tasks

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.PikePlugin
import org.pike.configuration.PikeExtension

class CloneTaskTest {

    @Test
    public void force () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        CloneTask cloneTask = project.tasks.clone
        cloneTask.setForce(true)

        PikeExtension pikeExtension = project.pike
        Assert.assertTrue ("Force was not set", pikeExtension.force)

    }
}
