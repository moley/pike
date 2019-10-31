package org.pike.tasks

import org.gradle.api.tasks.GradleBuild
import org.gradle.api.tasks.TaskAction
import org.pike.PikePlugin
import org.pike.configuration.Module
import org.pike.configuration.PikeExtension
import org.pike.utils.ConfigureUtils

class BuildModuleTask extends GradleBuild{

    {
        group = PikePlugin.PIKE_GROUP
    }


    Module module

    ConfigureUtils configureUtils = new ConfigureUtils()


    @TaskAction
    public void buildModule () {

        PikeExtension pikeExtension = project.extensions.findByName(PikeExtension.NAME)
        File basePath = configureUtils.getBasePath(project, module.configuration)
        File clonePath = new File(basePath, module.name)
        buildFile = new File (clonePath, 'build.gradle')
        dir = clonePath
        tasks = pikeExtension.initialBuildTasks
        logger.lifecycle("Starting build in " + clonePath.absolutePath)
        build()
    }
}
