package org.pike.tasks


import org.gradle.api.tasks.TaskAction
import org.pike.PikePlugin

class InstallTask extends ForcableTask {

    {
        group = PikePlugin.PIKE_GROUP
        description = 'Does all the installation of configured tools and checkout of all modules  defined in the pike-Closure in the current project'
    }

    @TaskAction
    public void execute() {

    }
}
