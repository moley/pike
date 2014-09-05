package org.pike.remoting

import org.pike.model.host.Host

/**
 * Created by OleyMa on 07.08.14.
 */
abstract class AbstractRemoting implements IRemoting{

    protected String user
    protected String group
    protected String password

    CommandBuilder createCommandBuild (Host host) {
        CommandBuilder commandBuilder = new CommandBuilder()
        commandBuilder.onOperatingSystem(host.operatingsystem.provider)
        commandBuilder.asUser(getUser())
        return commandBuilder
    }

    /**
     * getter
     * @return user
     */
    String getUser () {
        if (user == null)
            throw new IllegalStateException("User not set, please call configure () and implement this method to set the property user")

        return user
    }

    String getPassword () {
        if (password == null)
            throw new IllegalStateException("Password not set, please call configure() and implement this method to set the property password")

        return password
    }

    String getGroup () {
        if (group == null)
            return getUser()
        else
            return group
    }
}
