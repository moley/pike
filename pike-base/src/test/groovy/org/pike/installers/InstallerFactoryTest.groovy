package org.pike.installers

import org.junit.Assert
import org.junit.Test


class InstallerFactoryTest {

    InstallerFactory installerFactory = new InstallerFactory()

    @Test
    public void factory () {

        Assert.assertEquals (DmgInstaller.class, installerFactory.getInstaller(new File ("something.dmg")).class)
        Assert.assertEquals (TarGzInstaller.class, installerFactory.getInstaller(new File ("something.tar.gz")).class)
        Assert.assertEquals (ZipInstaller.class, installerFactory.getInstaller(new File ("something.zip")).class)
    }

    @Test (expected = IllegalStateException)
    public void factoryInvalid () {
        installerFactory.getInstaller(new File ("something.txt"))

    }
}
