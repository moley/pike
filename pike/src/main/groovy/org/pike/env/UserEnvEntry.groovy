package org.pike.env

import org.pike.model.operatingsystem.Operatingsystem

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 03.05.13
 * Time: 10:14
 * To change this template use File | Settings | File Templates.
 */
public abstract class UserEnvEntry implements IEnvEntry {


    protected String buildCommand (final boolean isExported, final Operatingsystem os, String key, final String value, String addon, String divider = "=") {
        String command = ""
        if (addon == null)
            addon = ""
        else
           addon = os.provider.pathSeparator + addon

        command = key + divider + os.provider.getOsDependendPath(value) + os.provider.getOsDependendPath(addon)

        if (isExported)
          command = os.setEnvPrefix + command

        println (command)

        return command
    }


}
