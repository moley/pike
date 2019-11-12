package org.pike.tasks

import groovy.mock.interceptor.MockFor
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import org.pike.PikePlugin
import org.pike.utils.PikeProperties
import org.pike.utils.ProcessResult
import org.pike.utils.ProcessWrapper


class StartIdeaTaskTest {

    @Test
    public void startIdea () {
        Project project = ProjectBuilder.builder().build()
        PikeProperties pikeProperties = new PikeProperties(project)
        pikeProperties.setProperty(InstallIdeaTaskTest.IDEA_INSTALLPATH, project.file('build/installpath').absolutePath)
        project.plugins.apply(PikePlugin)
        project.pike {
            idea {
            }
        }

        def mockedProcessWrapper = new MockFor(ProcessWrapper)
        mockedProcessWrapper.demand.execute {new ProcessResult()}
        mockedProcessWrapper.use {
            StartIdeaTask startIdeaTask = project.tasks.startIdea
            startIdeaTask.processWrapper = new ProcessWrapper()
            startIdeaTask.startIdea()
        }

    }
}
