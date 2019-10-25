package org.pike.tasks

import org.gradle.api.tasks.GradleBuild
import org.gradle.api.tasks.TaskAction
import org.pike.PikePlugin
import org.pike.configuration.Configuration
import org.pike.configuration.Module
import org.pike.configuration.PikeExtension
import org.pike.exceptions.MissingConfigurationException


class BuildModuleTask extends GradleBuild{

    {
        group = PikePlugin.PIKE_GROUP
        description = 'builds a module as preparation for importing it in the IDE'
    }


    Module module


    @TaskAction
    public void buildModule () {
        ArrayList<String> buildTasks = new ArrayList<String>()
        buildTasks.add('testClasses')

        PikeExtension pikeExtension = project.extensions.findByName(PikeExtension.NAME)
        Configuration mergedConfiguration = pikeExtension.getMergedConfiguration(module.configuration)
        if (mergedConfiguration.basepath == null)
            throw new MissingConfigurationException("A basepath must be configured globally or at gitmodule")
        File basepath = project.file(mergedConfiguration.basepath)
        File clonePath = new File(basepath, module.name)
        buildFile = new File (clonePath, 'build.gradle')
        dir = clonePath
        tasks = buildTasks
        logger.lifecycle("Starting build in " + clonePath.absolutePath)
        build()
    }
}
