package org.pike.configuration

import org.gradle.api.Project
import org.pike.tasks.git.CloneGitTask
import org.pike.utils.StringUtils
import org.pike.utils.TaskUtils

class Git {

    PikeExtension pikeExtension

    StringUtils stringUtils = new StringUtils()

    TaskUtils taskUtils = new TaskUtils()

    List<Module> modules = new ArrayList<>()

    void gitmodule (String name, String cloneurl, Closure closure = null) {
        Project project = pikeExtension.project
        CloneGitTask cloneGitTask = project.tasks.create("clone" + stringUtils.getFirstUpper(name), CloneGitTask)
        cloneGitTask.description = "Clones the repository $name from $cloneurl"
        taskUtils.registerInstallTask(project, cloneGitTask)
        Module module = new Module()
        module.cloneUrl = cloneurl
        module.name = name
        module.pikeExtension = pikeExtension
        module.project = project
        project.configure(module, closure)
        cloneGitTask.module = module
        cloneGitTask.pikeExtension = pikeExtension

        this.modules.add(module)
    }
}
