package org.pike.tasks

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.pike.TestUtils
import org.pike.common.ProjectInfo
import org.pike.model.defaults.Defaults
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.worker.DownloadWorker

import javax.annotation.security.RolesAllowed

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 25.04.13
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
class DownloadTaskTest {


    File dummyPathTo = new File ("tmp/downloadtasktest")

    @Before
    public void before () {
        FileUtils.deleteDirectory(dummyPathTo)

    }

    @After
    public void after () {
        FileUtils.deleteQuietly(dummyPathTo)
    }

    @Test
    public void testReallife () {

        File dummyZip = TestUtils.projectfile("pike", "src/test/resources/testzip.zip")

        DownloadWorker task = new DownloadWorker()
        task.cacheManager = new DummyCacheManager(dummyZip)
        task.to = dummyPathTo.absolutePath

        Operatingsystem os = new Operatingsystem("linux")
        os.tmpdir = "/tmp"
        task.operatingsystem = os

        Defaults defaults = new Defaults ()
        task.defaults = defaults

        String executable = "rootpath3/somefile"

        task.executable(executable)

        Assert.assertFalse ("Task is uptodate before installation", task.uptodate())

        task.install()

        File executableFile = new File (dummyPathTo, executable)
        Assert.assertTrue ("Executable toFile was not made executable", executableFile.canExecute())

        Assert.assertTrue ("Task is not uptodate after installation", task.uptodate())

    }
}
