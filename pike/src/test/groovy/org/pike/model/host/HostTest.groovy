package org.pike.model.host

import org.junit.Assert
import org.junit.Test
import org.pike.BitEnvironment
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.SuseProvider
import org.pike.os.WindowsProvider

/**
 * Tests for host
 */
class HostTest {

    @Test
    public void appdescLin32 () {
        Operatingsystem os = new Operatingsystem("linux", null)
        os.provider = new SuseProvider()
        Host host = new Host("hostname", null)
        host.operatingsystem = os
        Assert.assertEquals ('lin32', host.appdesc)
    }

    @Test
    public void appdescWin64 () {
        Operatingsystem os = new Operatingsystem("linux", null)
        os.provider = new WindowsProvider()
        Host host = new Host("hostname", null)
        host.operatingsystem = os
        host.bitEnvironment = BitEnvironment._64
        Assert.assertEquals ('win64', host.appdesc)
    }
}
