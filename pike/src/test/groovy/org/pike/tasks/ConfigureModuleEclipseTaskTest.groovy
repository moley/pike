package org.pike.tasks


import groovy.mock.interceptor.MockFor
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.pike.PikePlugin

class ConfigureModuleEclipseTaskTest {

    private String savedUserHome = null

    @Before
    public void before () {
        savedUserHome = System.getProperty('user.home')


    }

    @After
    public void after () {
        System.setProperty('user.home', savedUserHome)

    }

    @Test
    public void getWorkspacePath () {

        Project project = ProjectBuilder.builder().build()
        File userHomePath = new File (project.projectDir, 'user-home')
        System.setProperty('user.home', userHomePath.absolutePath)
        File goomphPath = new File (userHomePath, '.goomph/ide-workspaces')
        goomphPath.mkdirs()

        File localGoomph = project.file("build/somepath")
        localGoomph.mkdirs()

        new File (goomphPath, 'bla-owner').text = localGoomph.absolutePath

        ConfigureModuleEclipseTask configureModuleEclipseTask = project.tasks.create('configureModule', ConfigureModuleEclipseTask)
        File workspace = configureModuleEclipseTask.workspacePath
        Assert.assertEquals ("Invalid directory", new File(goomphPath, 'bla'), workspace)


    }

    @Test(expected = IllegalStateException)
    public void getWorkspacePathInvalidPath () {

        Project project = ProjectBuilder.builder().build()
        File userHomePath = new File (project.projectDir, 'user-home')
        System.setProperty('user.home', userHomePath.absolutePath)
        File goomphPath = new File (userHomePath, '.goomph/ide-workspaces')
        goomphPath.mkdirs()
        new File (goomphPath, 'bla-owner').text = 'someOtherDir'
        ConfigureModuleEclipseTask configureModuleEclipseTask = project.tasks.create('configureModule', ConfigureModuleEclipseTask)
        configureModuleEclipseTask.workspacePath


    }


    @Test(expected = IllegalStateException)
    public void getWorkspacePathNoSpecialFile () {

        Project project = ProjectBuilder.builder().build()
        File userHomePath = new File (project.projectDir, 'user-home')
        System.setProperty('user.home', userHomePath.absolutePath)
        File goomphPath = new File (userHomePath, '.goomph/ide-workspaces')
        new File (goomphPath, 'directory').mkdirs()
        new File (goomphPath, 'file').createNewFile()
        ConfigureModuleEclipseTask configureModuleEclipseTask = project.tasks.create('configureModule', ConfigureModuleEclipseTask)
        configureModuleEclipseTask.workspacePath

    }
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
