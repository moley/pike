package org.pike.worker

import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.pike.model.defaults.Defaults
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.LinuxProvider

/**
 * Created by OleyMa on 31.07.14.
 */
class PikeWorkerTest {

    @Test@Ignore
    void toFile () {
        PikeWorker worker = new PikeWorker() {
            @Override
            void install() {

            }

            @Override
            boolean uptodate() {
                return false
            }
        }

        Operatingsystem os = new Operatingsystem('linux')
        os.provider = new LinuxProvider()
        worker.operatingsystem = os
        Defaults defaults = new Defaults()
        defaults.rootpath = '/hello'
        worker.defaults = defaults
        File absolutePath = worker.toFile("/Users/OleyMa/hallo")
        Assert.assertEquals ("/Users/OleyMa/hallo", absolutePath.absolutePath)

    }
}
