package org.pike.worker

import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.pike.model.defaults.Defaults
import org.pike.model.operatingsystem.Operatingsystem

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 20.09.13
 * Time: 23:02
 * To change this template use File | Settings | File Templates.
 */
class UserEnvWorkerIncludeTest {

    File file = new File ("tmp/hallo")

    @Test
    public void testLinux () {

        UserenvWorker worker = new UserenvWorker()
        worker.operatingsystem = new Operatingsystem("linux")
        worker.defaults = new Defaults()
        worker.file = file.absolutePath
        worker.include("/etc/hallo", true)
        worker.install()

        File file = worker.toFile(worker.file)
        String text = file.text
        println (text)

        String content = "# pike    BEGIN (INCLUDE/etc/hallo)\n" +
                "source /etc/hallo >/dev/null\n" +
                "# pike    END (INCLUDE/etc/hallo)\n"
        Assert.assertEquals(content, text)

    }

    @After
    public void after () {
        if (file.exists())
            Assert.assertTrue (file.delete())
    }
}
