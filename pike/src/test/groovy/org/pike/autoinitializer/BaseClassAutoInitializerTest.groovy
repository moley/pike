package org.pike.autoinitializer

import org.junit.Assert
import org.junit.Test
import org.pike.model.ClassExtends

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 24.04.13
 * Time: 22:03
 * To change this template use File | Settings | File Templates.
 */
class BaseClassAutoInitializerTest {


    @Test
    public void initFromBase () {

        ClassExtends linux = new ClassExtends()
        linux.name="linux"
        linux.variable2="variable2"
        linux.variable1="variable1"

        ClassExtends ubuntu = new ClassExtends()
        ubuntu.name="Ubuntu"
        ubuntu.parent = linux

        ClassExtends ubuntu32bit = new ClassExtends()
        ubuntu32bit.name = "Ubuntu32bit"
        ubuntu32bit.parent = ubuntu

        BaseClassAutoInitializer autoInitializer = new BaseClassAutoInitializer()
        autoInitializer.initialize(ubuntu32bit, "parent")


        Assert.assertEquals ("Variable1(ubuntu32bit) not initialized", linux.variable1, ubuntu32bit.variable1)
        Assert.assertEquals ("Variable2(ubuntu32bit) not initialized", linux.variable2, ubuntu32bit.variable2)
        Assert.assertEquals ("Variable1(ubuntu) not initialized", linux.variable1, ubuntu.variable1)
        Assert.assertEquals ("Variable2(ubuntu) not initialized", linux.variable2, ubuntu.variable2)

    }
}
