package org.pike.worker

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.tasks.DelegatingTask
import org.pike.test.TestUtils

/**
 * Created by OleyMa on 06.05.14.
 */
@Slf4j
class SonarWorkerTest {


    @Test(expected = ProjectConfigurationException)
    public void missingUrl() {
        SonarWorker worker = TestUtils.createTask(SonarWorker)
        worker.install()

    }

    @Test(expected = ProjectConfigurationException)
    public void missingTo() {
        SonarWorker worker = TestUtils.createTask(SonarWorker)
        worker.url = "ftp://hallo"
        worker.install()
    }

    @Test
    public void execute() {


        File dummyPathTo = File.createTempDir()
        log.info("Test started in path " + dummyPathTo.absolutePath)

        File sonarZip = TestUtils.projectfile("sonar", "src/test/resources/sonarqube-4.3.zip").absoluteFile
        File pluginZip = TestUtils.projectfile("sonar", "src/test/resources/plugin1.jar").absoluteFile

        Project project = ProjectBuilder.builder().withProjectDir(dummyPathTo).build()
        project.apply plugin: 'pike'


        project.environments {
            sonar {
                sonar {
                    url "file:/" + sonarZip.absolutePath
                    to dummyPathTo.absolutePath
                    plugin "file:/" + pluginZip.absolutePath
                    property 'sonar.property', 'sonar.value'
                }
            }
        }

        TestUtils.prepareModel(project)



        DelegatingTask delegatingTask = project.tasks.findByName('installSonar')
        SonarWorker task = TestUtils.getWorker(delegatingTask)
        task.downloadUrlPlugins = '' //to download from filesystem

        List<PikeWorker> allWorkers = new ArrayList<PikeWorker>()
        TestUtils.getAllWorkers(task, allWorkers)

        for (PikeWorker next: allWorkers) {
            println (next.detailInfo)
            Assert.assertFalse("Task $next is uptodate before installation", next.uptodate())
            next.install()
        }

        File originalPath = new File(dummyPathTo, 'sonarqube-4.3')
        Assert.assertTrue(originalPath.absolutePath + " does not exist(Original)", originalPath.exists())

        File linkedPath = new File(dummyPathTo, 'sonar')
        Assert.assertTrue(linkedPath.absolutePath + " does not exist (Linked path)", linkedPath.exists())
        File confPath = new File(originalPath, 'conf')
        File propertiesFile = new File(confPath, 'sonar.properties')
        Assert.assertTrue(propertiesFile.absolutePath + " does not contain config value", propertiesFile.text.contains('sonar.value'))

    }
}
