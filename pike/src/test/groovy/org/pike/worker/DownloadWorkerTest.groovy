package org.pike.worker

import com.google.common.io.Files
import groovy.util.logging.Slf4j
import org.gradle.api.GradleException
import org.junit.Test
import org.pike.model.defaults.Defaults
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.test.TestUtils

import static org.junit.Assert.*

/**
 * Tests for downloading something
 */
@Slf4j
class DownloadWorkerTest {

    @Test
    public void testReallife () {

        File dummyPathTo = Files.createTempDir()
        File dummyZip = TestUtils.projectfile("pike", "src/test/resources/testzip.zip")

        DownloadWorker downloadworker = TestUtils.createTask(DownloadWorker)
        downloadworker.toPath = dummyPathTo
        downloadworker.from = "file:/" + dummyZip.absolutePath

        Operatingsystem os = new Operatingsystem("linux")
        downloadworker.operatingsystem = os

        Defaults defaults = new Defaults ()
        downloadworker.defaults = defaults

        String executable = "rootpath3/somefile"

        downloadworker.executable(executable)

        assertFalse ("Task is uptodate before installation", downloadworker.uptodate())

        downloadworker.install()

        File executableFile = new File (dummyPathTo, executable)
        assertTrue ("Executable toFile was not made executable", executableFile.canExecute())

        assertTrue ("Task is not uptodate after installation", downloadworker.uptodate())

        assertEquals (3, downloadworker.getDownloadedFiles().size())
        try {
            downloadworker.getDownloadedFile()
            fail ("No exception thrown - we downloaded 3 files, but getDownloadedFile() expects 1")
        } catch (IllegalStateException e) {

        }

    }

    @Test
    public void testNotExistingWithoutBin () {
        File dummyPathTo = new File ("/tmp/xyz" + System.currentTimeMillis())
        assertFalse ("DummyPath $dummyPathTo.absolutePath already exists", dummyPathTo.exists())
        File dummyZip = TestUtils.projectfile("pike", "src/test/resources/testzip.zip")

        DownloadWorker downloadworker = TestUtils.createTask(DownloadWorker)
        downloadworker.toPath = dummyPathTo
        downloadworker.from = "file:/" + dummyZip.absolutePath

        Operatingsystem os = new Operatingsystem("linux")
        downloadworker.operatingsystem = os

        Defaults defaults = new Defaults ()
        downloadworker.defaults = defaults

        String executable = "rootpath3/somefile"

        downloadworker.executable(executable)

        assertFalse ("Task is uptodate before installation", downloadworker.uptodate())

        downloadworker.install()

        File executableFile = new File (dummyPathTo, executable)
        assertTrue ("Executable toFile $executableFile.absolutePath was not made executable", executableFile.canExecute())

        assertTrue ("Task is not uptodate after installation", downloadworker.uptodate())

    }

    @Test(expected = GradleException.class)
    public void testNotExistingWithBin () {
        File dummyPathTo = new File ("/tmp/xyz" + System.currentTimeMillis())
        assertFalse ("DummyPath $dummyPathTo.absolutePath already exists", dummyPathTo.exists())
        File dummyZip = TestUtils.projectfile("pike", "src/test/resources/testzipWithBin.zip")

        DownloadWorker downloadworker = TestUtils.createTask(DownloadWorker)
        downloadworker.toPath = dummyPathTo
        downloadworker.from = "file:/" + dummyZip.absolutePath

        Operatingsystem os = new Operatingsystem("linux")
        downloadworker.operatingsystem = os

        Defaults defaults = new Defaults ()
        downloadworker.defaults = defaults

        assertFalse ("Task is uptodate before installation", downloadworker.uptodate())

        downloadworker.install()

        File executableExpected = new File (dummyPathTo, "testzipWithbin/bin/execute.sh")

        log.info(executableExpected.absolutePath)

        assertTrue ("Executable toFile $executableExpected.absolutePath was not made executable", executableExpected.canExecute())

        assertTrue ("Task is not uptodate after installation", downloadworker.uptodate())

    }


}
