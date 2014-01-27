package org.pike.worker

import org.junit.After
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.pike.model.defaults.Defaults
import org.pike.model.operatingsystem.Operatingsystem

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 20.09.13
 * Time: 22:53
 * To change this template use File | Settings | File Templates.
 */
class UserEnvWorkerAliasTest {

    File file = new File ("tmp/hallo")

    @After
    public void after () {
        if (file.exists())
            Assert.assertTrue (file.delete())
    }

    @Test
    public void testLinux () {

        UserenvWorker worker = new UserenvWorker()
        worker.operatingsystem = new Operatingsystem("linux")
        worker.defaults = new Defaults()
        worker.file = "tmp/hallo"
        worker.alias("l", "ls -l")
        worker.install()

        File file = worker.toFile(worker.file)
        String text = file.text
        println (text)

        String content = "# pike    BEGIN (ALIASl)\n" +
                "alias l='ls -l'\n" +
                "# pike    END (ALIASl)"
        Assert.assertEquals(content, text)

    }

    @Test@Ignore
    public void testWindows () {
        //TODO
    }
}
