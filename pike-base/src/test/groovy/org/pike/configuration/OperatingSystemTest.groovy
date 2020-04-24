package org.pike.configuration

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

class OperatingSystemTest {

    private static String osNamedSaved

    @BeforeClass
    public static void beforeClass() {
        osNamedSaved = System.getProperty('os.name')
    }

    @AfterClass
    public static void afterClass() {
        if (osNamedSaved != null)
            System.setProperty('os.name', osNamedSaved)
        osNamedSaved = null
    }

    @Test
    public void type() {
        System.setProperty("os.name", "Windows 2019")
        OperatingSystem operatingSystemWindows = OperatingSystem.current
        Assert.assertEquals(OperatingSystem.WINDOWS, operatingSystemWindows)

        System.setProperty("os.name", "Mac OSX")
        OperatingSystem operatingSystemMac = OperatingSystem.current
        Assert.assertEquals(OperatingSystem.MACOS, operatingSystemMac)

        System.setProperty("os.name", "bla")
        OperatingSystem operatingSystemLinux = OperatingSystem.current
        Assert.assertEquals(OperatingSystem.LINUX, operatingSystemLinux)
    }
}
