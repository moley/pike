package org.pike.vagrant

import org.pike.InstallPikeTask
import org.pike.remoting.IRemoting

/**
 * Created by OleyMa on 07.08.14.
 */
class InstallPikeInVmTask extends InstallPikeTask{

    @Override
    protected IRemoting getRemoting () {
        return new VagrantRemoting()
    }

}
