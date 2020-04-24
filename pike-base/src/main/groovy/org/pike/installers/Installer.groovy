package org.pike.installers

import org.gradle.api.Project


/**
 * installer interface must be implemented by any
 * installer
 */
interface Installer {

    /**
     * set the project
     * @param project  new project
     */
    void setProject(Project project)

    /**
     * installs the tool into outputDir
     *
     * @param outputDir         outputDir to install tool to
     * @param downloadedFile    downloaded tool file
     *
     * @return root dir of installed tool
     */
    File install (File outputDir, File downloadedFile)

    /**
     * returns the installation dir to be choosen
     *
     * @param installationDir  the installation dir configured from outside
     *
     * @return the real installation dir
     */
    File getDefaultInstallationDir (final File installationDir)
}
