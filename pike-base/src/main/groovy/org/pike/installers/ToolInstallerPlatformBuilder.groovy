package org.pike.installers

import org.pike.configuration.FileType


class ToolInstallerPlatformBuilder {

    File installationPath

    String url

    FileType fileType

    public ToolInstallerPlatformBuilder installationPath (final File installationpath) {
        this.installationPath = installationpath
        return this
    }

    public ToolInstallerPlatformBuilder url (final String url) {
        this.url = url
        return this
    }

    public ToolInstallerPlatformBuilder filetype (final FileType fileType) {
        this.fileType = fileType
        return this
    }
}
