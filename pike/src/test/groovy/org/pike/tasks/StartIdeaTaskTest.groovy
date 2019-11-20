package org.pike.tasks

import groovy.mock.interceptor.MockFor
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.PikePlugin
import org.pike.configuration.OperatingSystem
import org.pike.utils.PikeProperties
import org.pike.utils.ProcessResult
import org.pike.utils.ProcessWrapper


class StartIdeaTaskTest {

    @Test(expected = IllegalStateException)
    public void noInstallationDir () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            idea {
            }
        }

        StartIdeaTask startIdeaTask = project.tasks.startIdea
        startIdeaTask.operatingSystem = OperatingSystem.MACOS
        startIdeaTask.startIdea()
    }

    @Test(expected = IllegalStateException)
    public void processWrapperReturncode () {
        Project project = ProjectBuilder.builder().build()
        PikeProperties pikeProperties = new PikeProperties(project)
        pikeProperties.setProperty(InstallIdeaTask.IDEA_INSTALLPATH, project.file('build/installpath').absolutePath)
        project.plugins.apply(PikePlugin)
        project.pike {
            idea {
            }
        }

        def mockedProcessWrapper = new MockFor(ProcessWrapper)
        mockedProcessWrapper.demand.execute {a-> println a
            Assert.assertTrue (a.toString().contains('idea,'))
            ProcessResult processResult = new ProcessResult()
            processResult.error = "Some fancy error"
            processResult.resultCode = 1
            return processResult
        }
        mockedProcessWrapper.use {
            StartIdeaTask startIdeaTask = project.tasks.startIdea
            startIdeaTask.operatingSystem = OperatingSystem.MACOS
            startIdeaTask.processWrapper = new ProcessWrapper()
            startIdeaTask.startIdea()
        }

    }

    @Test
    public void startIdeaMac () {
        Project project = ProjectBuilder.builder().build()
        PikeProperties pikeProperties = new PikeProperties(project)
        pikeProperties.setProperty(InstallIdeaTask.IDEA_INSTALLPATH, project.file('build/installpath').absolutePath)
        project.plugins.apply(PikePlugin)
        project.pike {
            idea {
            }
        }

        def mockedProcessWrapper = new MockFor(ProcessWrapper)
        mockedProcessWrapper.demand.execute {a-> println a
            Assert.assertTrue (a.toString().contains('idea,'))
            new ProcessResult()
        }
        mockedProcessWrapper.use {
            StartIdeaTask startIdeaTask = project.tasks.startIdea
            startIdeaTask.operatingSystem = OperatingSystem.MACOS
            startIdeaTask.processWrapper = new ProcessWrapper()
            startIdeaTask.startIdea()
        }

    }

    @Test
    public void startIdeaLinux () {
        Project project = ProjectBuilder.builder().build()
        PikeProperties pikeProperties = new PikeProperties(project)
        pikeProperties.setProperty(InstallIdeaTask.IDEA_INSTALLPATH, project.file('build/installpath').absolutePath)
        project.plugins.apply(PikePlugin)
        project.pike {
            idea {
            }
        }

        def mockedProcessWrapper = new MockFor(ProcessWrapper)
        mockedProcessWrapper.demand.execute {a-> println a
            Assert.assertTrue (a.toString().contains('idea.sh,'))
            new ProcessResult()
        }
        mockedProcessWrapper.use {
            StartIdeaTask startIdeaTask = project.tasks.startIdea
            startIdeaTask.operatingSystem = OperatingSystem.LINUX
            startIdeaTask.processWrapper = new ProcessWrapper()
            startIdeaTask.startIdea()
        }

    }
}
