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

    @Test
    public void alias () {

        File tmpFile = File.createTempFile(getClass().getName(), 'alias')

        UserenvWorker worker = new UserenvWorker()
        worker.operatingsystem = new Operatingsystem("linux")
        worker.defaults = new Defaults()
        worker.file (tmpFile.absolutePath)
        worker.alias("l", "ls -l")
        worker.install()

        File file = worker.file
        String text = file.text
        println (text)

        String content = "# pike    BEGIN (ALIASl)\n" +
                "alias l='ls -l'\n" +
                "# pike    END (ALIASl)\n"
        Assert.assertEquals(content, text)

    }


}
