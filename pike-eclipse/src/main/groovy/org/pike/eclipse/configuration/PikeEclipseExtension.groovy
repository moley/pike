package org.pike.eclipse.configuration

import org.gradle.api.Project
import org.pike.configuration.OperatingSystem

class PikeEclipseExtension {

    public final static String NAME = "pikeeclipse"

    List<OsToken> osTokens = []

    File installationDir

    String version

    String distributionType = 'java'

    List<UpdatesiteItem> updateSites = []

    Project project

    PikeEclipseExtension (final Project project) {
        this.project = project
    }

    public List<OsToken> getOsTokens () {
        return osTokens.isEmpty() ?
        [new OsToken(OperatingSystem.WINDOWS, "win32-x86_64.zip"),
         new OsToken(OperatingSystem.MACOS, "macosx-cocoa-x86_64.dmg"),
         new OsToken(OperatingSystem.LINUX, "linux-gtk-x86_64.tar.gz")] : osTokens
    }

    public void osToken (String operatingsystem, final String token) {
        osTokens.add(new OsToken(OperatingSystem.valueOf(operatingsystem.toUpperCase()), token))
    }

    public void installationDir (File installationDir) {
        this.installationDir = installationDir
    }

    public void updatesite (final String url, final String name) {
        this.updateSites.add(new UpdatesiteItem(url, name))
    }

    public void version (final String version) {
        this.version = version
    }

    public void distributionType (final String distributionType) {
        this.distributionType = distributionType
    }
}
