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

    @Test
    public void findFiles () {
        File tmpDir = Files.createTempDir()
        tmpDir.mkdirs()
        File path1 = new File (tmpDir, "path1")
        File path2 = new File (tmpDir, "path2")
        path1.mkdirs()
        path2.mkdirs()
        File fileInPath1 = new File (path1, "bla.txt")
        fileInPath1.createNewFile()
        File fileInPath2 = new File (path2, "bla.txt")
        fileInPath2.createNewFile()

        List<File> files = fileUtils.findFiles(tmpDir, "bla.txt")
        Assert.assertEquals ("Number of files invalid", 2, files.size())
    }

    @Test
    public void findFile () {
        File tmpDir = Files.createTempDir()
        tmpDir.mkdirs()
        File path1 = new File (tmpDir, "path1")
        File path2 = new File (tmpDir, "path2")
        path1.mkdirs()
        path2.mkdirs()
        File fileInPath1 = new File (path1, "bla.txt")
        fileInPath1.createNewFile()

        File file = fileUtils.findFile(tmpDir, "bla.txt")
        Assert.assertEquals ("Wrong file found", 'bla.txt', file.name)

    }

    @Test(expected = IllegalStateException)
    public void findFileButFoundTwo () {
        File tmpDir = Files.createTempDir()
        tmpDir.mkdirs()
        File path1 = new File (tmpDir, "path1")
        File path2 = new File (tmpDir, "path2")
        path1.mkdirs()
        path2.mkdirs()
        File fileInPath1 = new File (path1, "bla.txt")
        fileInPath1.createNewFile()
        File fileInPath2 = new File (path2, "bla.txt")
        fileInPath2.createNewFile()

        fileUtils.findFile(tmpDir, "bla.txt")
    }
}
