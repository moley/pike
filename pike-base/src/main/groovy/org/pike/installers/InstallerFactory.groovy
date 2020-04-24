package org.pike.installers


class InstallerFactory {

    public Installer getInstaller(final File downloadedFile) {

        if (downloadedFile.name.endsWith("dmg"))
            return new DmgInstaller()
        else if (downloadedFile.name.endsWith("tar.gz"))
            return new TarGzInstaller()
        else if (downloadedFile.name.endsWith(".zip"))
            return new ZipInstaller()
        else
            throw new IllegalStateException("No installer for file " + downloadedFile.name + " found")

    }
}
