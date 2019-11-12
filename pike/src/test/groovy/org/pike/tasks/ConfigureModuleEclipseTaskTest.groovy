package org.pike.tasks

import groovy.mock.interceptor.MockFor
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.PikePlugin

class ConfigureModuleEclipseTaskTest {

    @Test
    public void configureModule() {
        def mockedEclipseConfiguration = new MockFor(EclipseConfiguration)
        mockedEclipseConfiguration.demand.apply { a, b, File c, File d, e, Boolean f ->
            Assert.assertTrue ("WorkspaceConfPath invalid", c.absolutePath.contains("ide-workspaces"))
            Assert.assertEquals ("ProjectConfPath invalid", '.settings', d.name)
            Assert.assertFalse ("Dry-run invalid", f)
        }
        mockedEclipseConfiguration.use {

            Project project = ProjectBuilder.builder().build()
            project.plugins.apply(PikePlugin)
            project.pike {
                git {
                    gitmodule('pike', 'https://github.com/moley/pike.git') {
                        configuration {
                            encoding 'UTF-8'
                        }
                    }
                }
                eclipse {
                }
            }
            project.evaluate()


            ConfigureModuleEclipseTask configureModuleEclipseTask = project.tasks.configureEclipsePike
            configureModuleEclipseTask.configureModule()
        }


    }
}
