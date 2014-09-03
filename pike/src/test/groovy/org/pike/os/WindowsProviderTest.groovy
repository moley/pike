package org.pike.os

import org.junit.Assert
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 18.09.13
 * Time: 13:28
 * To change this template use File | Settings | File Templates.
 */
class WindowsProviderTest {

    @Test
    public void osDependedPath () {

        WindowsProvider provider = new WindowsProvider()
        println (provider.getOsDependendPath("C:/hallo"))
        Assert.assertEquals ("C:\\hallo", provider.getOsDependendPath("C:/hallo"))
        Assert.assertEquals (":ext:user@somewhere.de/irgendwas", provider.getOsDependendPath(":ext:user@somewhere.de/irgendwas"))

    }
}
