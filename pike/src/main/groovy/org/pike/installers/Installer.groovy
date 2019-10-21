package org.pike.installers

import org.gradle.api.Project


interface Installer {

    void setProject(Project project)

    public void install (final File outputDir, final File downloadedFile)
}
