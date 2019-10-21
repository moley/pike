package org.pike.installers

import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

class DmgInstallerTest {

    @Test@Ignore
    void install () {


    }

    @Test
    void getVolume () {

        DmgInstaller dmgInstaller = new DmgInstaller()
        String volume = dmgInstaller.getVolumePath ("""/dev/disk2          \tGUID_partition_scheme          \t
/dev/disk2s1        \tApple_HFS                      \t/Volumes/IntelliJ IDEA CE""")
        Assert.assertEquals ("/Volumes/IntelliJ IDEA CE", volume)
    }
}
