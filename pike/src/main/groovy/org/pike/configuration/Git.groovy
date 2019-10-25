package org.pike.configuration

import org.gradle.api.Project
import org.pike.tasks.BuildModuleTask
import org.pike.tasks.CloneGitTask
import org.pike.utils.StringUtils
import org.pike.utils.TaskUtils

class Git {

    PikeExtension pikeExtension

    StringUtils stringUtils = new StringUtils()

    TaskUtils taskUtils = new TaskUtils()

    List<Module> modules = new ArrayList<>()

    void gitmodule (String name, String cloneurl, Closure closure = null) {
        Project project = pikeExtension.project

        Module module = new Module()
        module.cloneUrl = cloneurl
        module.name = name
        module.project = project
        project.configure(module, closure)

        CloneGitTask cloneGitTask = project.tasks.create("clone" + stringUtils.getFirstUpper(name), CloneGitTask)
        cloneGitTask.description = "Clones the repository $name from $cloneurl"
        cloneGitTask.module = module
        taskUtils.registerInstallTask(project, cloneGitTask)


        BuildModuleTask buildModuleTask = project.tasks.create("build" + stringUtils.getFirstUpper(name), BuildModuleTask)
        buildModuleTask.module = module
        buildModuleTask.description = "Starts the initial build on module $name"
        buildModuleTask.mustRunAfter cloneGitTask
        taskUtils.registerInstallTask(project, buildModuleTask)


        this.modules.add(module)
    }
}
