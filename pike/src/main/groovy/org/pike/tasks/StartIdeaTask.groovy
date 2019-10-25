package org.pike.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.PikePlugin
import org.pike.configuration.OperatingSystem
import org.pike.installers.ToolInstaller
import org.pike.utils.ProcessResult
import org.pike.utils.ProcessWrapper

class StartIdeaTask extends DefaultTask{

    {
      group = PikePlugin.PIKE_GROUP
      description = 'Starts an IntelliJ instance for the given project'
    }

    ProcessWrapper processWrapper = new ProcessWrapper()

    ToolInstaller tool


    @TaskAction
    public void startIdea () {

        if (OperatingSystem.current.equals(OperatingSystem.MACOS)) {
            File startFile = project.file("/Applications/IntelliJ IDEA CE.app/Contents/MacOS/idea") //TODO nicer

            String [] commands = [startFile.absolutePath, project.projectDir.absolutePath]
            ProcessResult result = processWrapper.execute(commands)
            if (result.resultCode != 0)
                throw new IllegalStateException("Could not start idea " + result.error)
        }

    }
}
