package org.pike

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.holdertasks.InstallTask
import org.pike.test.TestUtils

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 25.09.13
 * Time: 19:46
 * To change this template use File | Settings | File Templates.
 */
class SimpleIntegrationTest {


    @Test
    void simple () {

        Project project = ProjectBuilder.builder().withName("autocreateTasksTest").withProjectDir(new File ("tmp")).build()
        project.gradle.startParameter.logLevel = LogLevel.DEBUG
        project.apply plugin: 'pike'

        //Pike - Model
        project.defaults {
            defaultuser = 'nightly'
            currentHost = 'localhost'
            rootpath = '${currentPath}/rootpath4Test'
            pikeuser = '${user}'
            pikepassword = 'Momopomo351977'
            pikegradle = 'http://services.gradle.org/distributions/gradle-1.6-all.zip'
        }

        project.operatingsystems {
            macosx {
                homedir = "/home/${project.defaults.defaultuser}"
                programdir = "${homedir}/swarm/tools"
                cachedir = "${homedir}/.pike/cache"
                pikedir = '/opt/pike'
                tmpdir = '/tmp'
                pikejre = 'http://installbuilder.bitrock.com/java/jre1.7.0_21-osx.zip'
                appconfigfile = '/etc/sysconfig/jenkins/global.sh'
                userconfigfile =  "${homedir}/.bashrc"
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
                    file = operatingsystem.userconfigfile
                    include (operatingsystem.appconfigfile)
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

        Collection <String> expectedTasks = ["checkenv",
                "checkenvConfincludes",
                "checkenvJdks16",
                "checkenvJdks17",
                "clean",
                "deinstall",
                "deinstallConfincludes",
                "deinstallJdks16",
                "deinstallJdks17",
                "deriveTasks",
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
