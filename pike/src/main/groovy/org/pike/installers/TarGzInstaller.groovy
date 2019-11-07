package org.pike.installers

import org.rauschig.jarchivelib.Archiver

class TarGzInstaller extends AbstractInstaller{

    @Override
    void install(File outputDir, File downloadedFile) {
        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz")
        archiver.extract(downloadedFile, outputDir)
    }
}
