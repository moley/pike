package org.pike.env

import org.junit.Assert
import org.junit.Test
import org.pike.model.operatingsystem.Operatingsystem

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 03.05.13
 * Time: 10:40
 * To change this template use File | Settings | File Templates.
 */
class PathEntryTest {

    Operatingsystem os = new Operatingsystem("linux")

    @Test
    public void simple () {

        //TODO tests for windows

        PathEntry entry = new PathEntry("GRADLE_HOME", "/opt/gradle", null)

        List <String> serialized = new ArrayList<String>()
        entry.serialize(os, serialized)

        println (serialized)

        Assert.assertEquals (1, serialized.size())
        Assert.assertEquals (serialized.get(0), "export GRADLE_HOME=/opt/gradle")
    }

    @Test
    public void addedToPath () {

        PathEntry entry = new PathEntry("GRADLE_HOME", "/opt/gradle", "bin")

        List <String> serialized = new ArrayList<String>()
        entry.serialize(os, serialized)

        println (serialized)

        Assert.assertEquals (2, serialized.size())
        Assert.assertEquals (serialized.get(0).toString(), 'export GRADLE_HOME=/opt/gradle')
        Assert.assertEquals (serialized.get(1).toString(), 'export PATH=$GRADLE_HOME/bin:$PATH')

    }
}
