package org.pike.remoting

import org.pike.autoinstall.PropertyChangeProgressLogging
import org.pike.model.host.Host

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 02.05.13
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */
public interface IRemoting {

    void upload (String toDir, File from, PropertyChangeProgressLogging logging)

    RemoteResult execCmd(String cmd)

    void disconnect ()

    boolean connectedToHost (Host host)
}