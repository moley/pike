package org.pike.utils

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.pike.configuration.IDEType


class TaskUtils {

    public void  registerInstallTask (Project project, DefaultTask task) {
        project.tasks.install.dependsOn task
    }

    public void  registerCloneTask (Project project, DefaultTask task) {
        project.tasks.clone.dependsOn task
    }

    public void registerDeleteTask (Project project, DefaultTask task) {
        project.tasks.delete.dependsOn task
    }

    public void  registerConfigureTask (Project project, DefaultTask task, final IDEType ide) {
        DefaultTask configureIde = project.tasks.findByName("configure${ide.name}")
        configureIde.dependsOn task
    }
}
