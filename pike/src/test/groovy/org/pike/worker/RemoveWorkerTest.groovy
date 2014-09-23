package org.pike.worker

import org.junit.Assert
import org.junit.Test
import org.pike.test.TestUtils

import java.nio.file.Files

/**
 * Tests remove worker
 */
class RemoveWorkerTest {

    File file = Files.createTempFile('removeworker', System.currentTimeMillis().toString()).toFile()

    @Test
    public void test() {
        Assert.assertTrue (file.exists())

        RemoveWorker worker = TestUtils.createTask(RemoveWorker)
        worker.file(file.absolutePath)
        worker.install()

        Assert.assertFalse (file.exists())
    }

}
