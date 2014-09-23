package org.pike.tasks

import com.google.common.io.Files
import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.pike.test.TestUtils
import org.pike.worker.UserenvWorker

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 * tests userenv task
 */
@Slf4j
class UserenvTaskTest {

    private String user = System.getProperty("user.name")
    private String propFilePath = "/home/${user}/.bashrc"
    private File propFile = new File ("tmp", propFilePath).absoluteFile

    private Project configureProject () {
        Project project = ProjectBuilder.builder().withName("autocreateTasksTest").withProjectDir(Files.createTempDir())build()
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
    public void testUpdateAlreadyPikedEntry () {

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

        println (">Before: " + propFile.text+"<")


        Project project = configureProject()

        TestUtils.prepareModel(project)

        DelegatingTask setPropertyHalloTask = project.tasks.findByName ("installTestenv")
        UserenvWorker worker = TestUtils.getWorker(setPropertyHalloTask)
        worker.file (propFilePath)
        worker.install()

        List<String> text = propFile.text.split(UserenvWorker.NEWLINE)

        println (propFile.text)

        checkFile(text, "java")

    }
    @Test
    public void testNew () {

        Project project = configureProject()


        TestUtils.prepareModel(project)

        DelegatingTask setPropertyHalloTask = project.tasks.findByName ("installTestenv")
        UserenvWorker worker = TestUtils.getWorker(setPropertyHalloTask)
        assertTrue (propFile.absoluteFile.parentFile.mkdirs())
        worker.file (propFilePath)
        worker.install()

        List<String> text = propFile.text.split(UserenvWorker.NEWLINE)

        log.info (">After: " + propFile.text+"<")

        checkFile(text, "java")

    }

    private void checkFile (final List<String> text, String pathend) {

        assertEquals ("Pikeentry PATH GRADLE_HOME not found in line 0 (${text})", 0, text.indexOf("# pike    BEGIN (PATH GRADLE_HOME)"))
        assertEquals ("Pikeentry PATH GRADLE_HOME not found in line 4 (${text})", 4, text.indexOf('# pike    BEGIN (PATH JAVA_HOME)'))

        String javahomeString = "export JAVA_HOME=/home/${user}/swarm/tools/" + pathend
        assertEquals ("line $javahomeString not found in $text", 5, text.indexOf(javahomeString))

        assertEquals (6, text.indexOf('export PATH=$JAVA_HOME/bin:$PATH'))
        assertEquals (7, text.indexOf('# pike    END (PATH JAVA_HOME)'))
    }
}
