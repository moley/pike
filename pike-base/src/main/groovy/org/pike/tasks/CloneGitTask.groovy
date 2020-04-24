package org.pike.tasks

import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PullCommand
import org.eclipse.jgit.lib.ProgressMonitor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.pike.PikePlugin
import org.pike.configuration.Module
import org.pike.configuration.PikeExtension
import org.pike.exceptions.MissingConfigurationException
import org.pike.utils.ConfigureUtils
import org.pike.utils.PikeProgressMonitor
import org.pike.utils.ProgressLoggerWrapper

class CloneGitTask extends ForcableTask {

    {
        group = PikePlugin.PIKE_GROUP
    }

    ConfigureUtils configureUtils = new ConfigureUtils()


    Module module

    CloneCommand cloneCommand = Git.cloneRepository()

    PullCommand pullCommand = null



    @TaskAction
    public void cloneGitModule () {
        if (module.name == null)
            throw new MissingConfigurationException("No modulename configured")

        File basePath = configureUtils.getBasePath(project, module.configuration)
        File clonePath = new File(basePath, module.name)

        if (clonePath.exists()) {
            if (force) {
                if (pullCommand == null)
                    pullCommand = Git.open(clonePath).pull()
                pullCommand.call()
                logger.lifecycle("Merged remote data from " + module.cloneUrl + " to " + clonePath.absolutePath)
            }
            else
                logger.lifecycle("Path " + clonePath.absolutePath + " already exists. If you want to merge remote commits, add commandline parameter --force")
        }
        else {
            final ProgressLoggerWrapper progressLoggerWrapper = new ProgressLoggerWrapper(project, "Cloning " + module.cloneUrl)
            cloneCommand = cloneCommand.setProgressMonitor(new PikeProgressMonitor(progressLoggerWrapper))
            cloneCommand = cloneCommand.setURI(module.cloneUrl)
            if (module.branch != null)
                cloneCommand = cloneCommand.setBranch(module.branch)
            cloneCommand = cloneCommand.setDirectory(clonePath)
            cloneCommand.call()

            progressLoggerWrapper.end()
            logger.lifecycle("Cloned " + module.cloneUrl + " to " + clonePath.absolutePath)
        }
    }

}
