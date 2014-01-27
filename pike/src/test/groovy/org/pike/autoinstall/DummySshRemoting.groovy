package org.pike.autoinstall

import org.gradle.api.Project
import org.pike.model.host.Host
import org.pike.remoting.IRemoting
import org.pike.remoting.RemoteResult
import org.pike.remoting.SshRemoting

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 02.05.13
 * Time: 22:30
 * To change this template use File | Settings | File Templates.
 */
class DummySshRemoting implements IRemoting {

    public Collection<String> commands = new ArrayList<String>()

    /**
     * constructor
     * @param project project
     * @param host current host
     */
    DummySshRemoting() {

    }

    public RemoteResult execCmd(String cmd) {
        commands.add(cmd)
        return new RemoteResult ("","")
    }

    @Override
    void disconnect() {
        commands.add("disconnect")
    }

    public void upload (String toDir, File from, PropertyChangeProgressLogging logging) {
        commands.add("CP " + from.absolutePath + "->" + toDir)
    }

    public Collection<String> getCommands () {
        return commands
    }

    public boolean connectedToHost (Host host) {
        commands.add("check connection to Host")
        return true
    }
}
