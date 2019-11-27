package org.pike.installers

import com.google.common.io.Files
import org.junit.Assert
import org.junit.Test
import org.rauschig.jarchivelib.Archiver
import org.rauschig.jarchivelib.ArchiverFactory


class ZipInstallerTest {

    private File tmpDir = Files.createTempDir()

    @Test(expected = IllegalStateException)
    void multipleRoots () {
        Archiver archiver = ArchiverFactory.createArchiver("zip")
        File archive = archiver.create(getClass().simpleName + "_multipleRoots", Files.createTempDir(), new File ("src/test/resources/archive/multipleRoots"))
        File outDir = new File (tmpDir, "multipleRoots")
        ZipInstaller zipInstaller = new ZipInstaller()
        zipInstaller.install(outDir, archive)
    }

    @Test
    void oneRoot () {
        Archiver archiver = ArchiverFactory.createArchiver("zip")
        File archive = archiver.create(getClass().simpleName + "_oneRoot", Files.createTempDir(), new File ("src/test/resources/archive/oneRoot"))
        File outDir = new File (tmpDir, "oneRoot")
        ZipInstaller zipInstaller = new ZipInstaller()
        File rootDir = zipInstaller.install(outDir, archive)
        File textFile = new File (rootDir, 'file.txt')
        Assert.assertTrue ("Unzipped textfile does not exist", textFile.exists())
    }

    @Test(expected = IllegalArgumentException)
    public void paramOutDirNull () {
        ZipInstaller zipInstaller = new ZipInstaller()
        zipInstaller.install(null, new File("something.tgz"))
    }

    @Test(expected = IllegalArgumentException)
    public void paramArchiveNull () {
        ZipInstaller zipInstaller = new ZipInstaller()
        zipInstaller.install(new File ("output"), null)
    }

    @Test(expected = IllegalArgumentException)
    public void nonExisting ( ){
        ZipInstaller zipInstaller = new ZipInstaller()
        zipInstaller.getSingleRootPath(null, new File("nonExisting.tgz"))

    }


}
