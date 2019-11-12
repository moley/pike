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



    File getClonePath () {
        File basePath = configureUtils.getBasePath(project, module.configuration)
        return new File(basePath, module.name)
    }

    @TaskAction
    public void deleteGitModule () {

        if (clonePath.exists())
          FileUtils.deleteDirectory(clonePath)

    }

}
