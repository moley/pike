package org.pike.tasks

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.test.TestUtils
import org.pike.worker.DownloadWorker

/**
 * Tests a matrix build
 */
class MatrixTaskTest {


    @Test
    public void matrixTest () {

        Project project = ProjectBuilder.builder().withName("matrixTest").build()
        project.apply plugin: 'pike'

        project.defaults {
            fsUser = 'nightly'
            currentHost = 'localhost'

        }

        project.operatingsystems {
            macosx {
                homedir = "/home/${project.defaults.fsUser}"
                programdir = "${homedir}/jenkins/tools"
            }
        }

        project.hosts {
            localhost {
                hostname = 'localhost'
                operatingsystem = project.operatingsystems.macosx
                environment 'jdks'
            }
        }

        project.environments {
            jdks {
                matrix (['17':'1.7.0_07', '16':'1.6.0_something'])

                download {
                    from "http://somewhere/jdk${paramvalue}.zip"
                    to "${operatingsystem.programdir}"
                    executable ("jdk${paramvalue}/bin/java")
                    executable ("jdk${paramvalue}/jre/bin/java")
                }
                userenv {
                    path ("JDK${paramkey}_HOME", "${operatingsystem.programdir}/jdk${paramvalue}", "bin")
                }

            }
        }

        TestUtils.prepareModel(project)

        DelegatingTask task = project.tasks.findByName("installJdks17")
        DownloadWorker worker = TestUtils.getWorker(task)
        Assert.assertEquals ("http://somewhere/jdk1.7.0_07.zip", worker.from)
        Assert.assertNotNull (task)

        DelegatingTask task2 = project.tasks.findByName("installJdks16")
        Assert.assertNotNull (task2)
        DownloadWorker worker2 = TestUtils.getWorker(task2)
        Assert.assertEquals ("http://somewhere/jdk1.6.0_something.zip", worker2.from)
        Assert.assertNotNull (task2)
    }
}
