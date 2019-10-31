package org.pike.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.pike.PikePlugin
import org.pike.configuration.PikeExtension

class InstallTask extends DefaultTask {

    {
        group = PikePlugin.PIKE_GROUP
        description = 'Does all the installation of configured tools and checkout of all modules  defined in the pike-Closure in the current project'
    }

    @Option(option = "force", description = "Makes a clean configuration (e.g. pull on existing git clones)")
    public void setForce(boolean enabled) {
        PikeExtension pikeExtension = project.extensions.findByName(PikeExtension.NAME)
        pikeExtension.force = enabled
    }

    @TaskAction
    public void execute() {

    }
}
