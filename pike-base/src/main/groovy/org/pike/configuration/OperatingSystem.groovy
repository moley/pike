package org.pike.configuration

import org.pike.utils.StringUtils

enum OperatingSystem {
    WINDOWS,
    LINUX,
    MACOS

    public static OperatingSystem getCurrent () {
        String osname = System.getProperty("os.name")
        if (osname.startsWith("Windows"))
            return WINDOWS
        else if (osname.startsWith("Mac"))
            return MACOS
        else
            return LINUX
    }

    String getDisplayName () {
        StringUtils stringUtils = new StringUtils()
        return stringUtils.getFirstUpper(name())
    }
}
