package org.pike.installers

import com.google.common.io.Files
import org.junit.Assert
import org.junit.Test
import org.rauschig.jarchivelib.Archiver
import org.rauschig.jarchivelib.ArchiverFactory


class TarGzInstallerTest {

    private File tmpDir = Files.createTempDir()

    @Test(expected = IllegalStateException)
    void multipleRoots () {
        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz")
        File archive = archiver.create(getClass().simpleName + "_multipleRoots", Files.createTempDir(), new File ("src/test/resources/archive/multipleRoots"))
        File outDir = new File (tmpDir, "multipleRoots")
        TarGzInstaller tarGzInstaller = new TarGzInstaller()
        tarGzInstaller.install(outDir, archive)
    }

    @Test
    void oneRoot () {
        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz")
        File archive = archiver.create(getClass().simpleName + "_oneRoot", Files.createTempDir(), new File ("src/test/resources/archive/oneRoot"))
        File outDir = new File (tmpDir, "oneRoot")
        TarGzInstaller tarGzInstaller = new TarGzInstaller()
        File rootDir = tarGzInstaller.install(outDir, archive)
        File textFile = new File (rootDir, 'file.txt')
        Assert.assertTrue ("Unzipped textfile does not exist", textFile.exists())
    }
}
