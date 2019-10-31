package org.pike.tasks

import org.gradle.api.DefaultTask
import org.pike.PikePlugin

class DeleteTask extends DefaultTask {

    {
        group = PikePlugin.PIKE_GROUP
        description = 'Delete all defined modules'
    }
}
