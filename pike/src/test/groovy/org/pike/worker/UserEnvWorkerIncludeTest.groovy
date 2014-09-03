package org.pike.worker

import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.pike.model.defaults.Defaults
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.test.TestUtils

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 20.09.13
 * Time: 23:02
 * To change this template use File | Settings | File Templates.
 */
class UserEnvWorkerIncludeTest {



    @Test
    public void testLinux () {

        UserenvWorker worker = new UserenvWorker()
        worker.operatingsystem = new Operatingsystem("linux")
        worker.defaults = new Defaults()
        worker.file (TestUtils.tmpFile.absolutePath)
        worker.include("/etc/hallo", true)
        worker.install()

        File file = worker.file
        String text = file.text
        println (text)

        String content = "# pike    BEGIN (INCLUDE/etc/hallo)\n" +
                "source /etc/hallo >/dev/null\n" +
                "# pike    END (INCLUDE/etc/hallo)\n"
        Assert.assertEquals(content, text)

    }

}
