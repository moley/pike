package org.pike.configuration

import org.gradle.api.Project
import org.pike.tasks.*
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
        taskUtils.registerCloneTask(project, cloneGitTask)

        DeleteModuleTask deleteModuleTask = project.tasks.create("delete" + stringUtils.getFirstUpper(name), DeleteModuleTask)
        deleteModuleTask.description = "Deletes the module $name from workspace"
        deleteModuleTask.module = module
        taskUtils.registerDeleteTask(project, deleteModuleTask)

        BuildModuleTask buildModuleTask = project.tasks.create("build" + stringUtils.getFirstUpper(name), BuildModuleTask)
        buildModuleTask.module = module
        buildModuleTask.description = "Starts the initial build on module $name"
        buildModuleTask.mustRunAfter cloneGitTask
        taskUtils.registerInstallTask(project, buildModuleTask)

        project.afterEvaluate {

            if (pikeExtension.eclipse) {
                ConfigureModuleEclipseTask configureModuleEclipseTask = project.tasks.create("configureEclipse" + stringUtils.getFirstUpper(name), ConfigureModuleEclipseTask)
                configureModuleEclipseTask.eclipse = pikeExtension.eclipse
                configureModuleEclipseTask.module = module
                configureModuleEclipseTask.description = "Applies all configurations for eclipse on module $name"
                configureModuleEclipseTask.mustRunAfter buildModuleTask
                configureModuleEclipseTask.mustRunAfter project.tasks.ideSetupWorkspace // as long as we are overwriting configurations ourselfs
                taskUtils.registerConfigureTask(project, configureModuleEclipseTask, IDEType.ECLIPSE)
            }

            if (pikeExtension.idea) {
                ConfigureModuleIdeaTask configureModuleIdeaTask = project.tasks.create("configureIdea" + stringUtils.getFirstUpper(name), ConfigureModuleIdeaTask)
                configureModuleIdeaTask.idea = pikeExtension.idea
                configureModuleIdeaTask.module = module
                configureModuleIdeaTask.description = "Applies all configurations for idea on module $name"
                configureModuleIdeaTask.mustRunAfter buildModuleTask
                taskUtils.registerConfigureTask(project, configureModuleIdeaTask, IDEType.IDEA)
            }
        }


        this.modules.add(module)
    }

    public Module findModuleByName (final String name) {
        for (Module next: modules) {
            if (next.name.equals(name))
                return next
        }

        return null
    }
}
