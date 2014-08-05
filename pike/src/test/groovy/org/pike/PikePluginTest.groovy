package org.pike

import org.gradle.api.DefaultTask
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.common.TaskContext
import org.pike.model.environment.Environment
import org.pike.tasks.DelegatingTask
import org.pike.test.TestUtils
import org.pike.worker.DownloadWorker

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 17.04.13
 * Time: 21:37
 * To change this template use File | Settings | File Templates.
 */
class PikePluginTest {

    @Test
    public void autocreateTasks () {

        ProjectInternal project = ProjectBuilder.builder().withName("autocreateTasksTest").build()
        project.apply plugin: 'pike'

        project.defaults {
            defaultuser = 'nightly'
            currentHost = 'vtbuild11-x'
        }

        project.operatingsystems {

            linux {

            }

            suse {
                parent = linux
            }

            redhat {
                parent = linux
            }
        }

        project.hosts {
            jumptest {
                hostname = 'vtbuild11-x'
                operatingsystem = project.operatingsystems.redhat
                environment 'buildnode'
            }

            divatest {
                hostname = 'vtnightly11-x'
                operatingsystem = project.operatingsystems.suse
                environment 'buildnode'
            }
        }

        project.environments {
            buildnode {
                /**
                 * Download VSA Gradle
                 */
                download {
                    from = "http://hudson.intra.vsa.de:8080/view/gradle/job/createVsaGradle/lastSuccessfulBuild/artifact/build/vsagradle-1.5-all.zip"
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
        Assert.assertEquals (TaskContext.install, downloadWorker.context)
        DefaultTask containerTaskInstall = project.tasks.install
        checkTaskContainsDependency(containerTaskInstall, "installBuildnode")


        DelegatingTask deinstalltask = project.tasks.deinstallBuildnode
        DownloadWorker undownloadWorker = deinstalltask.workers.get(0)
        Assert.assertEquals ("deinstallBuildnode", deinstalltask.name)
        Assert.assertEquals ("buildnode", undownloadWorker.environment.name)
        Assert.assertEquals ("download(0)", undownloadWorker.name)
        Assert.assertEquals (TaskContext.deinstall, undownloadWorker.context)
        DefaultTask containerTaskDeInstall = project.tasks.deinstall
        checkTaskContainsDependency(containerTaskDeInstall, "deinstallBuildnode")

        DelegatingTask checktask = project.tasks.checkenvBuildnode
        DownloadWorker checkWorker = checktask.workers.get(0)
        Assert.assertEquals ("checkenvBuildnode", checktask.name)
        Assert.assertEquals ("buildnode", checkWorker.environment.name)
        Assert.assertEquals ("download(0)", checkWorker.name)
        Assert.assertEquals (TaskContext.checkenv, checkWorker.context)
        DefaultTask containerTask = project.tasks.checkenv
        checkTaskContainsDependency(containerTask, "checkenvBuildnode")
    }

    private void checkTaskContainsDependency (final DefaultTask task, final String depTask) {
        for (Object nextDep : task.dependsOn) {
            if (nextDep instanceof DefaultTask && ((DefaultTask)nextDep).name.equals(depTask))
                return
        }

        Assert.fail ("Task $depTask was not found as dependend task of $task")
    }

}