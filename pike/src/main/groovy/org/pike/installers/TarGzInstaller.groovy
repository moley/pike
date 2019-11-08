package org.pike.installers


import org.rauschig.jarchivelib.Archiver
import org.rauschig.jarchivelib.ArchiverFactory

class TarGzInstaller extends ArchiveInstaller{

    @Override
    File install(File outputDir, File downloadedFile) {
        if (outputDir == null)
            throw new IllegalArgumentException("Parameter 'outputDir' must be set")

        if (downloadedFile == null)
            throw new IllegalArgumentException("Parameter 'downloadedFile' must be set")

        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz")
        archiver.extract(downloadedFile, outputDir)

        String rootPath = getSingleRootPath(archiver, downloadedFile)
        return new File (outputDir, rootPath)




    }
}
