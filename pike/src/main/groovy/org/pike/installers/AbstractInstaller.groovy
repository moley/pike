package org.pike.installers

import org.gradle.api.Project


abstract class AbstractInstaller implements Installer {

    Project getProject() {
        return project
    }

    void setProject(Project project) {
        this.project = project
    }
    private Project project
}
