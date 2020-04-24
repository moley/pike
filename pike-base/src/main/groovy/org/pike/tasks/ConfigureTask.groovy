package org.pike.tasks

import org.gradle.api.DefaultTask
import org.pike.PikePlugin


class ConfigureTask extends DefaultTask{

    {
        group = PikePlugin.PIKE_GROUP
        description = 'Applies all configurations of all modules defined in the pike-Closure in the current project'
    }
}
