package org.pike.worker

import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.pike.TestUtils
import org.pike.tasks.DelegatingTask
import org.pike.worker.PropertyWorker
import org.pike.worker.UserenvWorker

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 02.05.13
 * Time: 22:58
 * To change this template use File | Settings | File Templates.
 */
class UserEnvWorkerGeneralTest {

    private String user = System.getProperty("user.name")
    private String propFilePath = "/home/${user}/.bashrc"
    private File propFile = new File ("tmp", propFilePath).absoluteFile

    private Project configureProject () {
        Project project = ProjectBuilder.builder().withName("autocreateTasksTest").build()
        project.apply plugin: 'pike'

        project.defaults {
            rootpath = '${currentPath}/tmp'
            currentHost = 'myhost'
        }
        project.operatingsystems {
            linux {
                homedir = '/home/${user}'
                programdir = "${homedir}/swarm/tools"
            }
        }

        project.hosts {
            myhost {
                operatingsystem = project.operatingsystems.linux
                environment 'testenv'
            }
        }

        project.environments {
            testenv {
                userenv {
                    path ("GRADLE_HOME", "${operatingsystem.programdir}/gradle", "bin")
                    path ("JAVA_HOME", "${operatingsystem.programdir}/java", "bin")
                }
            }
        }
        return project
    }

    @Before@After
    public void init () {

        File tmp = new File ("tmp")
        if (tmp.exists())
            FileUtils.forceDelete(tmp)

    }

    @Test
    public void testUpdate () {

        String content =    "# pike    BEGIN (PATH GRADLE_HOME)\n" +
                            "export GRADLE_HOME=/home/${user}/swarm/tools/oldgradle\n" +
                            "export PATH=\$GRADLE_HOME:\$PATH\n" +
                            "# pike    END (PATH GRADLE_HOME)\n" +
                            "# pike    BEGIN (PATH JAVA_HOME)\n" +
                            "export JAVA_HOME=/home/${user}/swarm/tools/java\n" +
                            "export PATH=\$JAVA_HOME:\$PATH\n" +
                            "# pike    END (PATH JAVA_HOME)"

        propFile.parentFile.mkdirs()
        propFile << content

        Project project = configureProject()

        TestUtils.prepareModel(project)

        DelegatingTask setPropertyHalloTask = project.tasks.findByName ("installTestenv")
        UserenvWorker worker = setPropertyHalloTask.workers.get(0)
        worker.file = propFilePath
        println (setPropertyHalloTask.getDetailInfo())
        setPropertyHalloTask.install()

        List<String> text = propFile.text.split(UserenvWorker.NEWLINE)

        println (propFile.text)

        checkFile(text, "java")

    }
    @Test
    public void testNew () {

        Project project = configureProject()

        TestUtils.prepareModel(project)

        DelegatingTask setPropertyHalloTask = project.tasks.findByName ("installTestenv")
        UserenvWorker worker = setPropertyHalloTask.workers.get(0)
        Assert.assertTrue (propFile.absoluteFile.parentFile.mkdirs())
        worker.file = propFilePath
        println (setPropertyHalloTask.getDetailInfo())
        setPropertyHalloTask.install()

        List<String> text = propFile.text.split(UserenvWorker.NEWLINE)

        println (">" + propFile.text+"<")

        checkFile(text, "java")

    }

    private void checkFile (final List<String> text, String pathend) {
        Assert.assertEquals (0, text.indexOf("# pike    BEGIN (PATH GRADLE_HOME)"))

        Assert.assertEquals (4, text.indexOf('# pike    BEGIN (PATH JAVA_HOME)'))

        String javahomeString = "export JAVA_HOME=/home/${user}/swarm/tools/" + pathend
        Assert.assertEquals ("line $javahomeString not found", 5, text.indexOf(javahomeString))

        Assert.assertEquals (6, text.indexOf('export PATH=$JAVA_HOME/bin:$PATH'))
        Assert.assertEquals (7, text.indexOf('# pike    END (PATH JAVA_HOME)'))
    }
}
