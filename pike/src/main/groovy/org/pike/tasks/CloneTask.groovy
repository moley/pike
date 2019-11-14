package org.pike.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.options.Option
import org.pike.PikePlugin
import org.pike.configuration.PikeExtension


class CloneTask extends DefaultTask{

    {
        group = PikePlugin.PIKE_GROUP
        description = 'Clones all defined modules'
    }


    @Option(option = "force", description = "Makes a clean configuration (e.g. pull on existing git clones)")
    public void setForce(boolean enabled) {
        PikeExtension pikeExtension = project.extensions.findByName(PikeExtension.NAME)
        pikeExtension.force = enabled
    }
}
