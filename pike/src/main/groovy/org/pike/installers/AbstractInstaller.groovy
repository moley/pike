package org.pike.installers

import org.gradle.api.Project

abstract class AbstractInstaller implements Installer {

    private Project project

    Project getProject() {
        return project
    }

    void setProject(Project project) {
        this.project = project
    }

    /**
     * returns the installation dir to be choosen
     *
     * @param installationDir  the installation dir configured from outside
     *
     * @return the real installation dir
     */
    File getDefaultInstallationDir (final File installationDir) {
        return installationDir
    }


}
