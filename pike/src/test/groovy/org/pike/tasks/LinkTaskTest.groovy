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

    File tmpPath = Files.createTempDirectory('LinkTaskTest').toFile()
    File dummyPathTo = new File (tmpPath, "gradle1.5")
    File dummyPathToOld = new File (tmpPath, "gradle1.0")
    File dummyPathFrom = new File (tmpPath, "gradle")

    @Before
    public void before () {
        if (! dummyPathTo.exists())
            dummyPathTo.mkdirs()

        if (! dummyPathToOld.exists())
            dummyPathToOld.mkdirs()
    }


    @Test
    public void testToIsAvailable () {

        LinkWorker task = new LinkWorker()
        task.fromPath = dummyPathFrom
        task.toPath = dummyPathTo

        Operatingsystem os = new Operatingsystem("linux")
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
        task.fromPath = dummyPathFrom
        task.toPath = dummyPathTo

        Files.createSymbolicLink(dummyPathFrom.toPath(), dummyPathToOld.toPath())

        Operatingsystem os = new Operatingsystem("linux")
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

        Path dummyPathFromAsPath = dummyPathFrom.toPath()
        Path dummyPathToOldAsPath = dummyPathToOld.toPath()

        Files.createSymbolicLink(dummyPathFromAsPath, dummyPathToOldAsPath)

        LinkWorker task = new LinkWorker()
        task.fromPath = dummyPathFrom
        task.toPath = dummyPathTo

        Operatingsystem os = new Operatingsystem("linux")
        task.operatingsystem = os

        Defaults defaults = new Defaults ()
        task.defaults = defaults

        Assert.assertFalse ("Task is uptodate before installation", task.uptodate())

        task.install()

        Assert.assertTrue ("Task is not uptodate after installation", task.uptodate())

    }
}
