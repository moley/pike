package org.pike.vagrant

import org.pike.StartRemoteBuildTask
import org.pike.remoting.IRemoting

/**
 * Created by OleyMa on 09.08.14.
 */
class StartRemoteBuildInVmTask extends StartRemoteBuildTask {

    @Override
    protected IRemoting getRemoting () {
        return new VagrantRemoting()
    }
}
