package org.pike

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.holdertasks.InstallTask
import org.pike.test.TestUtils

/**
 * A simple integration test
 */
class SimpleIntegrationTest {


    @Test
    void simple () {

        Project project = ProjectBuilder.builder().withName("autocreateTasksTest").withProjectDir(new File ("tmp")).build()
        project.gradle.startParameter.logLevel = LogLevel.DEBUG
        project.apply plugin: 'pike'

        //Pike - Model
        project.defaults {
            fsUser = 'nightly'
            currentHost = 'localhost'
            rootpath = '${currentPath}/rootpath4Test'
        }

        project.operatingsystems {
            macosx {
                programdir = "${homedir}/swarm/tools"
                appconfigfile = '/etc/sysconfig/jenkins/global.sh'
                globalconfigfile = '/etc/profile'
            }
        }

        project.hosts {
            localhost {
                hostname = 'localhost'
                hostgroups = 'prod'
                operatingsystem = project.operatingsystems.macosx
                environment 'jdks'
                environment 'confincludes'
            }
        }

        project.environments {

            // include
            confincludes {
                userenv {
                    file operatingsystem.userconfigfile
                    include operatingsystem.appconfigfile
                }
            }


            jdks {
                matrix ([   '17':'1.7.0_07',
                        '16':'1.6.0_27'])

                download {
                    from "http://hudson.intra.vsa.de:8080/userContent/tools/jdk${paramvalue}.zip"
                    to operatingsystem.programdir
                    executable ("jdk${paramvalue}/bin/java")
                    executable ("jdk${paramvalue}/jre/bin/java")
                }

            }
        }

        TestUtils.prepareModel(project)

        Collection <String> expectedTasks = [
                "clean",
                "install",
                "installConfincludes",
                "installJdks16",
                "installJdks17"]

        for (String nextExpected : expectedTasks) {
            Assert.assertNotNull("Task $nextExpected not found", project.tasks.findByName(nextExpected))
        }


        InstallTask installTask = project.tasks.install
        installTask.execute()


    }
}
