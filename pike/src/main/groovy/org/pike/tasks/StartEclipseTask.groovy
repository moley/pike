package org.pike.tasks

import org.gradle.api.DefaultTask
import org.pike.PikePlugin

class StartEclipseTask extends DefaultTask{

    {
        group = PikePlugin.PIKE_GROUP
        description = 'Starts an IntelliJ instance for the given project'
    }




}
