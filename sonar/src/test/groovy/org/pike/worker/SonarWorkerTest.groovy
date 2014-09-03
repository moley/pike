package org.pike.worker

import com.google.common.io.Files
import groovy.util.logging.Slf4j
import org.junit.Assert
import org.junit.Test
import org.pike.cache.CacheManager
import org.pike.test.TestUtils
import org.pike.model.defaults.Defaults
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.cache.DummyCacheManager

/**
 * Created by OleyMa on 06.05.14.
 */
@Slf4j
class SonarWorkerTest {


    @Test(expected = IllegalStateException)
    public void missingUrl() {
        SonarWorker worker = new SonarWorker()
        worker.install()

    }

    @Test(expected = IllegalStateException)
    public void missingTo() {
        SonarWorker worker = new SonarWorker()
        worker.url = "ftp://hallo"
        worker.install()

    }

    @Test
    public void execute() {

        File dummyPathTo = Files.createTempDir()
        log.info("Test started in path " + dummyPathTo.absolutePath)

        File sonarZip = TestUtils.projectfile("sonar", "src/test/resources/sonarqube-4.3.zip")
        File pluginZip = TestUtils.projectfile("sonar", "src/test/resources/plugin1.jar")

        SonarWorker worker = new SonarWorker()
        worker.cacheManager = new DummyCacheManager()

        //functional parameters
        worker.url = sonarZip.absolutePath
        worker.toPath = dummyPathTo
        worker.user = ''
        worker.property("sonar.property", 'sonar.value')

        Operatingsystem os = new Operatingsystem("linux")
        worker.operatingsystem = os

        Defaults defaults = new Defaults()
        worker.defaults = defaults



        worker.install()


        Assert.assertFalse("Task is uptodate before installation", worker.uptodate())

        File originalPath = new File(dummyPathTo, 'sonarqube-4.3')
        Assert.assertTrue(originalPath.absolutePath + " does not exist(Original)", originalPath.exists())

        File linkedPath = new File(dummyPathTo, 'sonar')
        Assert.assertTrue(linkedPath.absolutePath + " does not exist (Linked path)", linkedPath.exists())
        File confPath = new File(originalPath, 'conf')
        File propertiesFile = new File(confPath, 'sonar.properties')
        Assert.assertTrue(propertiesFile.absolutePath + " does not contain config value", propertiesFile.text.contains('sonar.value'))


    }
}
