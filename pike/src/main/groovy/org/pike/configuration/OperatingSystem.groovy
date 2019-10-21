package org.pike.configuration

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
}
