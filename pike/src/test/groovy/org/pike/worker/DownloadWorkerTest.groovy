package org.pike.worker

import com.google.common.io.Files
import org.apache.commons.io.FileUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.pike.test.TestUtils
import org.pike.model.defaults.Defaults
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.cache.DummyCacheManager

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 25.04.13
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
class DownloadWorkerTest {

    @Test
    public void testReallife () {

        File dummyPathTo = Files.createTempDir()
        File dummyZip = TestUtils.projectfile("pike", "src/test/resources/testzip.zip")

        DownloadWorker downloadworker = new DownloadWorker()
        downloadworker.cacheManager = new DummyCacheManager()
        downloadworker.toPath = dummyPathTo
        downloadworker.from = dummyZip.absolutePath
        downloadworker.setUser("")

        Operatingsystem os = new Operatingsystem("linux")
        downloadworker.operatingsystem = os

        Defaults defaults = new Defaults ()
        downloadworker.defaults = defaults

        String executable = "rootpath3/somefile"

        downloadworker.executable(executable)

        Assert.assertFalse ("Task is uptodate before installation", downloadworker.uptodate())

        downloadworker.install()

        File executableFile = new File (dummyPathTo, executable)
        Assert.assertTrue ("Executable toFile was not made executable", executableFile.canExecute())

        Assert.assertTrue ("Task is not uptodate after installation", downloadworker.uptodate())

        Assert.assertEquals (3, downloadworker.getDownloadedFiles().size())
        try {
            downloadworker.getDownloadedFile()
            Assert.fail ("No exception thrown - we downloaded 3 files, but getDownloadedFile() expects 1")
        } catch (IllegalStateException e) {

        }

    }

    @Test
    public void testNotExistingWithoutBin () {
        File dummyPathTo = new File ("/tmp/xyz" + System.currentTimeMillis())
        Assert.assertFalse ("DummyPath $dummyPathTo.absolutePath already exists", dummyPathTo.exists())
        File dummyZip = TestUtils.projectfile("pike", "src/test/resources/testzip.zip")

        DownloadWorker downloadworker = new DownloadWorker()
        downloadworker.cacheManager = new DummyCacheManager()
        downloadworker.toPath = dummyPathTo
        downloadworker.from = dummyZip.absolutePath
        downloadworker.setUser("")

        Operatingsystem os = new Operatingsystem("linux")
        downloadworker.operatingsystem = os

        Defaults defaults = new Defaults ()
        downloadworker.defaults = defaults

        String executable = "rootpath3/somefile"

        downloadworker.executable(executable)

        Assert.assertFalse ("Task is uptodate before installation", downloadworker.uptodate())

        downloadworker.install()

        File executableFile = new File (dummyPathTo, executable)
        Assert.assertTrue ("Executable toFile $executableFile.absolutePath was not made executable", executableFile.canExecute())

        Assert.assertTrue ("Task is not uptodate after installation", downloadworker.uptodate())

    }

    @Test
    public void testNotExistingWithBin () {
        File dummyPathTo = new File ("/tmp/xyz" + System.currentTimeMillis())
        Assert.assertFalse ("DummyPath $dummyPathTo.absolutePath already exists", dummyPathTo.exists())
        File dummyZip = TestUtils.projectfile("pike", "src/test/resources/testzipWithBin.zip")

        DownloadWorker downloadworker = new DownloadWorker()
        downloadworker.cacheManager = new DummyCacheManager()
        downloadworker.toPath = dummyPathTo
        downloadworker.from = dummyZip.absolutePath
        downloadworker.setUser("")

        Operatingsystem os = new Operatingsystem("linux")
        downloadworker.operatingsystem = os

        Defaults defaults = new Defaults ()
        downloadworker.defaults = defaults


        Assert.assertFalse ("Task is uptodate before installation", downloadworker.uptodate())

        downloadworker.install()

        File executableExpected = new File (dummyPathTo, "testzipWithbin/bin/execute.sh")

        Assert.assertTrue ("Executable toFile $executableExpected.absolutePath was not made executable", executableExpected.canExecute())

        Assert.assertTrue ("Task is not uptodate after installation", downloadworker.uptodate())

    }
}
