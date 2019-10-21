package org.pike.tasks


import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.pike.configuration.PikeExtension

class InstallTask extends PikeTask {

    {description = 'Does all the configuration defined in the pike-Closure in the current project'}

    @Option(option = "force", description = "Makes a clean configuration (e.g. pull on existing git clones)")
    public void setForce(boolean enabled) {
        PikeExtension pikeExtension = project.extensions.pike
        pikeExtension.force = enabled
    }

    @TaskAction
    public void execute () {

    }
}
