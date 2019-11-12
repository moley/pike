package org.pike.utils

import com.google.common.io.Files
import org.junit.Assert
import org.junit.Test

class FileUtilsTest {

    FileUtils fileUtils = new FileUtils()

    @Test
    public void singleChildValid () {
        File tmpDir = Files.createTempDir()
        tmpDir.mkdirs()
        File subPath = new File (tmpDir, "path1")
        subPath.mkdirs()

        Assert.assertEquals (subPath, fileUtils.getSingleChild(tmpDir))


    }

    @Test(expected = IllegalStateException)
    public void singleChildInvalid () {
        File tmpDir = Files.createTempDir()
        tmpDir.mkdirs()
        new File (tmpDir, "path1").mkdirs()
        new File (tmpDir, "path2").mkdirs()

        fileUtils.getSingleChild(tmpDir)

    }
}
