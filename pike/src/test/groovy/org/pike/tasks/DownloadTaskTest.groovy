package org.pike.tasks

import com.google.common.io.Files
import org.junit.Assert
import org.junit.Test
import org.pike.model.defaults.Defaults
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.test.TestUtils
import org.pike.worker.DownloadWorker

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 25.04.13
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
class DownloadTaskTest {

    @Test
    public void testReallife () {

        File dummyZip = TestUtils.projectfile("pike", "src/test/resources/testzip.zip")
        File dummyPathTo = Files.createTempDir()

        DownloadWorker task = TestUtils.createTask(DownloadWorker)
        task.toPath = dummyPathTo
        task.from = "file:/" + dummyZip.absolutePath



        Operatingsystem os = new Operatingsystem("linux")
        task.operatingsystem = os

        Defaults defaults = new Defaults ()
        task.defaults = defaults

        String executable = "rootpath3/somefile"

        task.executable(executable)

        Assert.assertFalse ("Task is uptodate before installation", task.uptodate())

        task.install()

        File executableFile = new File (dummyPathTo, executable)
        Assert.assertTrue ("Executable ${executableFile.absolutePath} was not made executable", executableFile.canExecute())

        Assert.assertTrue ("Task is not uptodate after installation", task.uptodate())

    }
}
