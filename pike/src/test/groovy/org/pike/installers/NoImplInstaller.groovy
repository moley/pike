package org.pike.installers


class NoImplInstaller extends AbstractInstaller {

    private File installPath

    public NoImplInstaller (final File installPath) {
        this.installPath = installPath
    }

    @Override
    File install(File outputDir, File downloadedFile) {
        println "OutputDir: " + outputDir.absolutePath
        println "DownloadedFile: " + downloadedFile.absolutePath
        return installPath
    }
}
