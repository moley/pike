package org.pike.installers

import org.rauschig.jarchivelib.Archiver
import org.rauschig.jarchivelib.ArchiverFactory

class TarGzInstaller extends AbstractInstaller{

    @Override
    void install(File outputDir, File downloadedFile) {
        if (outputDir == null)
            throw new IllegalStateException("Parameter 'outputDir' must be set")

        if (downloadedFile == null)
            throw new IllegalStateException("Parameter 'downloadedFile' must be set")

        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz")
        archiver.extract(downloadedFile, outputDir)
    }
}
