package org.pike

import org.gradle.api.DefaultTask
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.model.environment.Environment
import org.pike.model.host.Host
import org.pike.tasks.DelegatingTask
import org.pike.test.TestUtils
import org.pike.worker.DownloadWorker

/**
 * Tests for pike plugin
 */
class PikePluginTest {

    final String CURRENT_USER = System.getProperty("user.name")

    @Test
    public void defaults () {

        ProjectInternal project = ProjectBuilder.builder().withName("autocreateTasksTest").build()
        project.apply plugin: 'pike'

        Assert.assertEquals(CURRENT_USER, project.defaults.defaultuser)

    }

    @Test(expected = ProjectConfigurationException)
    public void hostWithoutOs () {

        ProjectInternal project = ProjectBuilder.builder().withName("autocreateTasksTest").build()
        project.apply plugin: 'pike'

        project.hosts {
            host1 {
                environment 'invalidEnvironment'
            }
        }

        ModelLogger.logConfiguration("test", project, false)

        TestUtils.prepareModel(project)
    }

    @Test(expected = ProjectConfigurationException)
    public void invalidEnv () {

        ProjectInternal project = ProjectBuilder.builder().withName("autocreateTasksTest").build()
        project.apply plugin: 'pike'

        project.defaults {
            currentHost = 'host1' //to enable on local machine
        }

        project.hosts {
            host1 {
                environment 'invalidEnvironment'
                operatingsystem = project.operatingsystems.suse
            }
        }

        project.environments {
            anything {
                download {
                    from 'http://somegradleadress.zip'
                    to (operatingsystem.homedir)
                }
            }
        }

        ModelLogger.logConfiguration("test", project, false)

        TestUtils.prepareModel(project)


    }

    @Test
    public void autocreateTasksNoHostsConfigured () {

        ProjectInternal project = ProjectBuilder.builder().withName("autocreateTasksTest").build()
        project.apply plugin: 'pike'

        project.environments {
            anything {
                download {
                    from 'http://somegradleadress.zip'
                    to (operatingsystem.homedir)
                }
            }
        }

        ModelLogger.logConfiguration("test", project, false)

        TestUtils.prepareModel(project)

        DelegatingTask installtask = project.tasks.installAnything
        Assert.assertNotNull('Task installAnything was not created', installtask)
        Host host = installtask.getWorkers().get(0).host
        Assert.assertNotNull('Host is not injected', host)
        Assert.assertNotNull('IP is not set', host.ip)
        Assert.assertNotNull('Hostname is not set', host.hostname)
        Assert.assertNotNull('Operatingsystem is not set', host.operatingsystem)
    }

    @Test
    public void autocreateTasks () {

        ProjectInternal project = ProjectBuilder.builder().withName("autocreateTasksTest").build()
        project.apply plugin: 'pike'

        String DEFAULTUSER = 'user'

        project.defaults {
            defaultuser = DEFAULTUSER
            currentHost = 'build11'
        }

        project.hosts {
            jumptest {
                hostname = 'build11'
                operatingsystem = project.operatingsystems.redhat
                environment 'buildnode'
            }

            divatest {
                hostname = 'nightly11'
                operatingsystem = project.operatingsystems.suse
                environment 'buildnode'
            }
        }

        project.environments {
            buildnode {
                download {
                    from = "http://somegradleadress.zip"
                }
            }
        }

        ModelLogger.logConfiguration("test", project, false)

        TestUtils.prepareModel(project)


        println (project.environments.buildnode)

        Environment env = project.environments.buildnode
        Assert.assertEquals ("Invalid number of tasks created", 3, env.createdTaskNames.size())
        println (project.tasks)

        DelegatingTask installtask = project.tasks.installBuildnode
        DownloadWorker downloadWorker = installtask.workers.get(0)

        Assert.assertEquals ("installBuildnode", installtask.name)
        Assert.assertEquals ("buildnode", downloadWorker.environment.name)
        Assert.assertEquals ("download(0)", downloadWorker.name)
        DefaultTask containerTaskInstall = project.tasks.install
        checkTaskContainsDependency(containerTaskInstall, "installBuildnode")

        Assert.assertEquals(DEFAULTUSER, project.defaults.defaultuser)

    }

    private void checkTaskContainsDependency (final DefaultTask task, final String depTask) {
        for (Object nextDep : task.dependsOn) {
            if (nextDep instanceof DefaultTask && ((DefaultTask)nextDep).name.equals(depTask))
                return
        }

        Assert.fail ("Task $depTask was not found as dependend task of $task")
    }

}