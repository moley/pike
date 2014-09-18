package org.pike.vagrant.org.pike.vagrant

import org.junit.Assert
import org.junit.Test
import org.pike.vagrant.VagrantUtil

/**
 * Created by OleyMa on 18.09.14.
 */
class VagrantUtilTest {

    @Test
    public void checkExisting () {
        Assert.assertFalse (VagrantUtil.doesVmExist("xyz"))
    }
}
