package org.pike

import groovy.util.logging.Slf4j
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.IOperatingsystemProvider

/**
 * Created by OleyMa on 01.08.14.
 */
@Slf4j
class AutoinstallUtil {

    /**
     * gets installer file name
     * @param os  operatingsystem
     * @return name
     */
    public static String getInstallerFile (Operatingsystem os) {
        return "pikeinstaller-${os.name}"
    }

    public static String addCommand (final IOperatingsystemProvider provider, String command, final String unresolved, final String param) {

        if (provider == null)
            throw new IllegalStateException("No operatingsystem provider defined")

        if (unresolved == null)
            throw new IllegalStateException("No unresolved path defined")

        String resolved = unresolved
        if (param != null)
            resolved = unresolved.replace("PARAM0", param)

        resolved = resolved.replaceAll("//", provider.fileSeparator)

        if (! command.isEmpty())
            command += provider.commandSeparator

        log.debug("resolve <" + unresolved + "> with param <" + param + "> to <" + resolved + ">")

        command += resolved
        return command

    }
}
