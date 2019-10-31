package org.pike.tasks

import org.gradle.api.DefaultTask
import org.pike.PikePlugin


class CloneTask extends DefaultTask{

    {
        group = PikePlugin.PIKE_GROUP
        description = 'Clones all defined modules'
    }
}
