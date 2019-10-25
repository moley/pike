package org.pike.installers

import org.gradle.api.Project


interface Installer {

    void setProject(Project project)

    /**
     * installs the tool into outputDir
     * @param outputDir         outputDir to install tool to
     * @param downloadedFile    downloaded tool file
     */
    void install (File outputDir, File downloadedFile)
}
