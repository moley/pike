package org.pike.tasks

import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.PikePlugin
import org.pike.configuration.Module
import org.pike.utils.ConfigureUtils

class DeleteModuleTask extends DefaultTask {

    {
        group = PikePlugin.PIKE_GROUP
    }

    ConfigureUtils configureUtils = new ConfigureUtils()

    Module module


    @TaskAction
    public void cloneGitModule () {

        File basePath = configureUtils.getBasePath(project, module.configuration)
        File clonePath = new File(basePath, module.name)

        if (clonePath.exists())
          FileUtils.deleteDirectory(clonePath)

    }

}
