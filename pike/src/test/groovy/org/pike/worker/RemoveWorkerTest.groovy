package org.pike.worker

import org.junit.Assert
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 20.09.13
 * Time: 22:45
 * To change this template use File | Settings | File Templates.
 */
class RemoveWorkerTest {

    File file = new File ("hallo")

    @Test
    public void test() {
        Assert.assertTrue (file.createNewFile())

        RemoveWorker worker = new RemoveWorker()
        worker.file("hallo")
        worker.install()

        Assert.assertFalse (file.exists())
    }

    public void after () {
        if (file.exists())
           Assert.assertTrue (file.delete())
    }
}
