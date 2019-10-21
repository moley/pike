package org.pike.tasks

import org.gradle.api.tasks.TaskAction
import org.pike.configuration.OperatingSystem
import org.pike.installers.Tool
import org.pike.utils.ProcessResult
import org.pike.utils.ProcessWrapper

class StartIdeaTask extends PikeTask{

    {
        description = 'Starts an IntelliJ instance for the given project'
    }

    ProcessWrapper processWrapper = new ProcessWrapper()

    Tool tool


    @TaskAction
    public void prepareIdea () {

        if (OperatingSystem.current.equals(OperatingSystem.MACOS)) {
            File startFile = project.file("/Applications/IntelliJ IDEA CE.app/Contents/MacOS/idea")

            String [] commands = [startFile.absolutePath]
            ProcessResult result = processWrapper.execute(commands)
            if (result.resultCode != 0)
                throw new IllegalStateException("Could not start idea " + result.error)
        }

    }
}