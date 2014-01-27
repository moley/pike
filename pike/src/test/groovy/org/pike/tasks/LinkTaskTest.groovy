package org.pike.tasks

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.pike.model.defaults.Defaults
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.worker.LinkWorker

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 02.05.13
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */
class LinkTaskTest {

    File dummyPathTo = new File ("tmp/gradle1.5")
    File dummyPathToOld = new File ("tmp/gradle1.0")
    File dummyPathFrom = new File ("tmp/gradle")

    @Before
    public void before () {

        File tmp = new File ("tmp")
        if (tmp.exists())
          FileUtils.deleteDirectory(tmp)

        if (! dummyPathTo.exists())
            dummyPathTo.mkdirs()

        if (! dummyPathToOld.exists())
            dummyPathToOld.mkdirs()
    }

    @After
    public void after () {
        if (dummyPathToOld.exists())
          FileUtils.forceDelete(dummyPathToOld)

        if (dummyPathFrom.exists())
          FileUtils.forceDelete(dummyPathFrom)

        if (dummyPathTo.exists())
          FileUtils.forceDelete(dummyPathTo)
    }

    @Test
    public void testToIsAvailable () {

        LinkWorker task = new LinkWorker()
        task.from = dummyPathFrom.absolutePath
        task.to = dummyPathTo.absolutePath

        Operatingsystem os = new Operatingsystem("linux")
        os.tmpdir = "/tmp"
        task.operatingsystem = os

        Defaults defaults = new Defaults ()
        task.defaults = defaults

        Assert.assertFalse ("Task is uptodate before installation", task.uptodate())

        task.install()

        Assert.assertTrue ("Task is not uptodate after installation", task.uptodate())

    }

    @Test
    public void testChangeLink () {

        LinkWorker task = new LinkWorker()
        task.from = dummyPathFrom.absolutePath
        task.to = dummyPathTo.absolutePath

        Files.createSymbolicLink(dummyPathFrom.toPath(), dummyPathToOld.toPath())

        Operatingsystem os = new Operatingsystem("linux")
        os.tmpdir = "/tmp"
        task.operatingsystem = os

        Defaults defaults = new Defaults ()
        task.defaults = defaults

        Assert.assertFalse ("Task is uptodate before installation", task.uptodate())

        task.install()

        Assert.assertTrue ("Task is not uptodate after installation", task.uptodate())

    }


    @Test
    public void testFromAndToExistButToChanged () {

    }

    @Test
    public void testLinkChanged () {

        Path dummyPathFromAsPath = Paths.get(dummyPathFrom.absolutePath)
        Path dummyPathToOldAsPath = Paths.get(dummyPathToOld.absolutePath)

        Files.createSymbolicLink(dummyPathFromAsPath, dummyPathToOldAsPath)

        LinkWorker task = new LinkWorker()
        task.from = dummyPathFrom.absolutePath
        task.to = dummyPathTo.absolutePath

        Operatingsystem os = new Operatingsystem("linux")
        os.tmpdir = "/tmp"
        task.operatingsystem = os

        Defaults defaults = new Defaults ()
        task.defaults = defaults

        Assert.assertFalse ("Task is uptodate before installation", task.uptodate())

        task.install()

        Assert.assertTrue ("Task is not uptodate after installation", task.uptodate())

    }
}
