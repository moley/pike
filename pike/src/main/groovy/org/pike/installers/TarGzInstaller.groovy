package org.pike.installers

import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver
import org.gradle.internal.impldep.org.codehaus.plexus.logging.console.ConsoleLoggerManager

class TarGzInstaller extends AbstractInstaller{

    @Override
    void install(File outputDir, File downloadedFile) {

        final TarGZipUnArchiver ua = new TarGZipUnArchiver()
        ConsoleLoggerManager manager = new ConsoleLoggerManager()
        manager.initialize();
        ua.enableLogging(manager.getLoggerForComponent("bla"))
        ua.setSourceFile(downloadedFile)
        destDir.mkdirs()
        ua.setDestDirectory(outputDir)
        ua.extract()

    }
}
