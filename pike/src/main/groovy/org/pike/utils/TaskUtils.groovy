package org.pike.utils

import org.gradle.api.DefaultTask
import org.gradle.api.Project


class TaskUtils {

    public void  registerInstallTask (Project project, DefaultTask task) {
        project.tasks.install.dependsOn task
    }
}
