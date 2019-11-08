package org.pike.tasks

import groovy.mock.interceptor.MockFor
import org.eclipse.jgit.api.CloneCommand
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.PikePlugin
import org.pike.configuration.Module
import org.pike.exceptions.MissingConfigurationException

class CloneGitTaskTest {

    @Test(expected = MissingConfigurationException)
    public void modulenameMissing () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            configuration {
                basepath = project.file('build/pike')
            }
        }
        CloneGitTask cloneGitTask = project.tasks.register('modulenameMissing', CloneGitTask).get()
        cloneGitTask.module = new Module()
        cloneGitTask.cloneGitModule()


    }

    @Test
    public void taskPerGitModule () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            git {
                gitmodule ('pike', 'https://github.com/moley/pike.git') {
                    configuration {
                        encoding 'UTF-8'
                    }
                }
                gitmodule ('leguan', 'https://github.com/moley/leguan')
            }

            configuration {
                encoding 'ISO-8859-15'
                basepath 'build/basepath'
            }

        }

        CloneGitTask cloneGitTaskPike = project.tasks.findByName("clonePike")
        CloneGitTask cloneGitTaskLeguan = project.tasks.findByName("cloneLeguan")

        Assert.assertNotNull ("No task created for gitmodule pike", cloneGitTaskPike)
        Assert.assertNotNull ("No task created for gitmodule leguan", cloneGitTaskLeguan)

        Assert.assertEquals ("Name not correct", 'pike', cloneGitTaskPike.module.name)
        Assert.assertEquals ("Name not correct", 'leguan', cloneGitTaskLeguan.module.name)


    }

    @Test
    public void executeClone () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            git {
                gitmodule ('pike', 'https://github.com/moley/pike.git')
            }

            configuration {
                encoding 'ISO-8859-15'
                basepath 'build/basepath'
            }
        }
        project.evaluate()
        CloneGitTask cloneGitTaskPike = project.tasks.findByName("clonePike")
        CloneCommand theCloneCommand = new CloneCommand()
        def mockForCloneCommand = new MockFor(CloneCommand)
        mockForCloneCommand.demand.setProgressMonitor{a->theCloneCommand}
        mockForCloneCommand.demand.setURI{a-> return theCloneCommand}
        mockForCloneCommand.demand.setDirectory{a->return theCloneCommand}
        mockForCloneCommand.demand.call {}
        mockForCloneCommand.use {
            cloneGitTaskPike.cloneGitModule()
        }



    }
}
