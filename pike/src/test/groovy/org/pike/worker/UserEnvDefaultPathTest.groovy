package org.pike.worker

import org.junit.After
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.pike.model.defaults.Defaults
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.test.TestUtils

import java.nio.file.Files

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 20.09.13
 * Time: 23:07
 * To change this template use File | Settings | File Templates.
 */
class UserEnvDefaultPathTest {

    @Test
    public void path () {

        UserenvWorker worker = TestUtils.createTask(UserenvWorker)
        worker.operatingsystem = new Operatingsystem("linux")
        worker.defaults = new Defaults()
        worker.file (TestUtils.tmpFile.absolutePath)
        worker.defaultpath("GROOVY_HOME")
        worker.install()

        File file = worker.file
        String text = file.text
        println (text)

        String content = "# pike    BEGIN (DEFAULTPATH GROOVY_HOME)\n" +
                "export PATH=GROOVY_HOME:\$PATH\n" +
                "# pike    END (DEFAULTPATH GROOVY_HOME)\n"
        Assert.assertEquals(content, text)

    }


}
