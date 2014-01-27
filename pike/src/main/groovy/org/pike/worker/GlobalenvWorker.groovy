package org.pike.worker

import org.pike.env.IEnvEntry

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 17.09.13
 * Time: 12:19
 * To change this template use File | Settings | File Templates.
 */
class GlobalenvWorker extends AbstractEnvironmentWorker {

    @Override
    void install() {
        global = true

        if (file != null)
            throw new IllegalStateException("You cannot configure a file in the globalenv worker")

        super.install()
    }
}
