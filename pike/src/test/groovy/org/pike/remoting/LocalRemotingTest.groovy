package org.pike.remoting

import org.junit.After
import org.junit.Assert
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 25.09.13
 * Time: 08:13
 * To change this template use File | Settings | File Templates.
 */
class LocalRemotingTest {
    File file = new File ("").getAbsoluteFile()
    File tmpPath = new File (file, "tmpLocalRemotingTest")
    File tmpFile = new File (file, "tmpFile")

    @Test
    public void execCmd () {
        LocalRemoting localRemoting = new LocalRemoting()
        Assert.assertTrue (localRemoting.execCmd("ls").ok)

    }

    @Test
    public void connected () {
        LocalRemoting localRemoting = new LocalRemoting()
        Assert.assertTrue (localRemoting.connectedToHost(null))
    }

    @After
    public void after () {
        if (tmpPath.exists()) {
            org.apache.commons.io.FileUtils.cleanDirectory(tmpPath)
            org.apache.commons.io.FileUtils.deleteDirectory(tmpPath)
        }
        if (tmpFile.exists())
          Assert.assertTrue (tmpFile.delete())
    }

    @Test
    public void upload () {
        Assert.assertTrue (tmpPath.mkdirs())
        Assert.assertTrue (tmpFile.createNewFile())

        File toFile = new File (tmpPath, "hallo")

        LocalRemoting localRemoting = new LocalRemoting()
        localRemoting.upload(toFile.absolutePath, tmpFile, null)

    }
}
