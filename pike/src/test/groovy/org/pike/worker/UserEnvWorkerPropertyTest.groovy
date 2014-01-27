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
 * Time: 23:09
 * To change this template use File | Settings | File Templates.
 */
class UserEnvWorkerPropertyTest {

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
        worker.property("http_proxy", "proxy.mycompany.de", ":")
        worker.property("https_proxy", "proxy.mycompany.de")
        worker.install()

        File file = worker.toFile(worker.file)
        String text = file.text
        println (text)

        String content = "# pike    BEGIN (PROPERTY http_proxy)\n" +
                "http_proxy:proxy.mycompany.de\n" +
                "# pike    END (PROPERTY http_proxy)\n" +
                "# pike    BEGIN (PROPERTY https_proxy)\n" +
                "https_proxy=proxy.mycompany.de\n" +
                "# pike    END (PROPERTY https_proxy)"
        Assert.assertEquals(content, text)

    }

    @Test@Ignore
    public void testWindows () {
        //TODO
    }
}
