package org.pike.installers


class InstallerFactory {

    public Installer getInstaller (final File downloadedFile) {

        Installer selectedInstaller = null

        if (downloadedFile.name.endsWith("dmg"))
            selectedInstaller = new DmgInstaller()
        else
            throw new IllegalStateException("No installer for file " + downloadedFile.name + " found")

        return selectedInstaller
    }
}
