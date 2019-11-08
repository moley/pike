package org.pike.installers

import org.rauschig.jarchivelib.Archiver
import org.rauschig.jarchivelib.ArchiverFactory

class ZipInstaller extends ArchiveInstaller{

    @Override
    File install(File outputDir, File downloadedFile) {
        if (outputDir == null)
            throw new IllegalStateException("Parameter 'outputDir' must be set")

        if (downloadedFile == null)
            throw new IllegalStateException("Parameter 'downloadedFile' must be set")

        Archiver archiver = ArchiverFactory.createArchiver("zip")
        archiver.extract(downloadedFile, outputDir)

        String rootPath = getSingleRootPath(archiver, downloadedFile)
        return new File (outputDir, rootPath)
    }
}
