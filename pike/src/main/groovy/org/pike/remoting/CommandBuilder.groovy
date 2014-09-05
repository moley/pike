package org.pike.remoting

import groovy.util.logging.Slf4j
import org.pike.os.IOperatingsystemProvider
import org.pike.os.LinuxProvider

/**
 * Created by OleyMa on 07.08.14.
 */
@Slf4j
class CommandBuilder {

    IOperatingsystemProvider osProvider = null
    String user = null

    String commandLine = ''

    public CommandBuilder onOperatingSystem (final IOperatingsystemProvider osProvider) {
        this.osProvider = osProvider
        return this
    }

    public CommandBuilder asUser (final String user) {
        this.user = user
        return this
    }

    /**
     * creates a command that is executed as sudo
     * @param unresolved  unresolved command
     * @param param   list of parameters
     * @return itself
     */
    public CommandBuilder addCommand (final String unresolved, final String... param) {
        return addCommand(true, unresolved, param)
    }

    /**
     * creates a command
     * @param sudo          true: executed as sudo
     * @param unresolved    unresolved command
     * @param param         list of parameters
     * @return itself
     */
    public CommandBuilder addCommand (final boolean sudo, final String unresolved, final String... param) {
        if (osProvider == null)
            throw new IllegalStateException("No operatingsystem provider defined")

        if (unresolved == null)
            throw new IllegalStateException("No unresolved path defined")

        String resolved = unresolved

        for (int i = 0; i < param.length; i++) {
            String key = "PARAM" + i
            resolved = resolved.replace(key, param [i])
        }

        resolved = resolved.replaceAll("//", osProvider.fileSeparator)

        if (! commandLine.isEmpty())
            commandLine += osProvider.commandSeparator

        log.debug("resolve <" + unresolved + "> with param <" + param + "> to <" + resolved + ">")

        if (sudo && user != 'root' && osProvider instanceof LinuxProvider)
            commandLine += 'sudo '


        commandLine += resolved
        return this

    }

    public String get () {
        return commandLine
    }
}
