package org.pike.tasks.git

import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PullCommand
import org.eclipse.jgit.api.PullResult
import org.eclipse.jgit.lib.ProgressMonitor
import org.gradle.api.tasks.TaskAction
import org.pike.configuration.Configuration
import org.pike.configuration.Module
import org.pike.exceptions.MissingConfigurationException
import org.pike.tasks.PikeTask
import org.pike.utils.ProgressLoggerWrapper


class CloneGitTask extends PikeTask {

    Module module

    File clonePath

    CloneCommand cloneCommand = Git.cloneRepository()

    PullCommand pullCommand = null

    @TaskAction
    public void cloneGitModule () {
        Configuration mergedConfiguration = pikeExtension.getMergedConfiguration(module.configuration)

        if (mergedConfiguration.basepath == null)
            throw new MissingConfigurationException("No basepath configured");

        if (module.name == null)
            throw new MissingConfigurationException("No modulename configured")

        if (clonePath == null)
            throw new MissingConfigurationException("No clonepath configured")

        if (clonePath.exists()) {
            if (pikeExtension.force) {
                if (pullCommand == null)
                    pullCommand = Git.open(clonePath).pull()
                pullCommand.call()
                logger.lifecycle("Merged remote data from " + module.cloneUrl + " to " + clonePath.absolutePath)
            }
            else
                logger.lifecycle("Path " + clonePath.absolutePath + " already exists. If you want to merge remote commits, add commandline parameter --force")

            //>TODO Pull
        }
        else {
            final ProgressLoggerWrapper progressLoggerWrapper = new ProgressLoggerWrapper(this, "Cloning " + module.cloneUrl)
            cloneCommand = cloneCommand.setProgressMonitor(new ProgressMonitor() {

                private int totalWork
                private int currentWork

                private String currentTask

                @Override
                void start(int totalTasks) {
                }

                @Override
                void beginTask(String title, int totalWork) {
                    this.currentTask = title
                    this.currentWork = 0
                    this.totalWork = totalWork
                    progressLoggerWrapper.progress(progressLoggerWrapper.getDescription() + ":  " + currentTask + "(" + currentWork + " of " + totalWork + " finished)")
                }

                @Override
                void update(int completed) {
                    currentWork += completed
                    progressLoggerWrapper.progress(progressLoggerWrapper.getDescription() + ":  " + currentTask + "(" + currentWork + " of " + totalWork + " finished)")
                }

                @Override
                void endTask() {

                }

                @Override
                boolean isCancelled() {
                    return false
                }
            })
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
