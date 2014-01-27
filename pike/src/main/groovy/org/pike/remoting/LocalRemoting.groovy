package org.pike.remoting

import org.apache.commons.io.FileUtils
import org.pike.autoinstall.PropertyChangeProgressLogging
import org.pike.model.host.Host

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 14.05.13
 * Time: 01:12
 * To change this template use File | Settings | File Templates.
 */
class LocalRemoting implements IRemoting {
    @Override
    void upload(String toDir, File from, PropertyChangeProgressLogging logging) {
        if (from.isDirectory())
          FileUtils.copyDirectory(from, new File (toDir))
        else
          FileUtils.copyFile(from, new File (toDir))
    }

    @Override
    RemoteResult execCmd(String cmd) {
        Process process = Runtime.runtime.exec(cmd)

        int failed = process.waitFor()
        RemoteResult result = new RemoteResult("localhost", "Returned with " + failed)

        return result
    }

    @Override
    void disconnect() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    boolean connectedToHost(Host host) {
        return true
    }
}
