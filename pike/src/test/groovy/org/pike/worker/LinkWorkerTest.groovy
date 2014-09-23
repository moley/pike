package org.pike.worker

import org.junit.Ignore
import org.pike.test.TestUtils

import static org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.pike.model.defaults.Defaults
import org.pike.model.operatingsystem.Operatingsystem

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Tests linking files and directories
 */
class LinkWorkerTest {

    File tmpPath = Files.createTempDirectory('LinkWorkerTest').toFile()
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
        LinkWorker task = TestUtils.createTask(LinkWorker)
        task.fromPath = dummyPathFrom
        task.toPath = dummyPathTo

        Operatingsystem os = new Operatingsystem("linux")
        task.operatingsystem = os

        Defaults defaults = new Defaults ()
        task.defaults = defaults

        assertFalse ("Task is uptodate before installation", task.uptodate())

        task.install()

        assertTrue ("Task is not uptodate after installation", task.uptodate())

    }

    @Test
    public void testChangeLink () {
        LinkWorker task = TestUtils.createTask(LinkWorker)
        task.fromPath = dummyPathFrom
        task.toPath = dummyPathTo

        Files.createSymbolicLink(dummyPathFrom.toPath(), dummyPathToOld.toPath())

        Operatingsystem os = new Operatingsystem("linux")
        task.operatingsystem = os

        Defaults defaults = new Defaults ()
        task.defaults = defaults

        assertFalse ("Task is uptodate before installation", task.uptodate())

        task.install()

        assertTrue ("Task is not uptodate after installation", task.uptodate())
    }


    @Test@Ignore
    public void testFromAndToExistButToChanged () {

    }

    @Test
    public void testLinkChanged () {
        Path dummyPathFromAsPath = Paths.get(dummyPathFrom.absolutePath)
        Path dummyPathToOldAsPath = Paths.get(dummyPathToOld.absolutePath)

        Files.createSymbolicLink(dummyPathFromAsPath, dummyPathToOldAsPath)

        LinkWorker task = TestUtils.createTask(LinkWorker)
        task.fromPath = dummyPathFrom
        task.toPath = dummyPathTo

        Operatingsystem os = new Operatingsystem("linux")
        task.operatingsystem = os

        Defaults defaults = new Defaults ()
        task.defaults = defaults

        assertFalse ("Task is uptodate before installation", task.uptodate())

        task.install()

        assertTrue ("Task is not uptodate after installation", task.uptodate())
    }
}
