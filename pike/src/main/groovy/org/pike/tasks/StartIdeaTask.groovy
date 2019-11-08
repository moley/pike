package org.pike.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.PikePlugin
import org.pike.configuration.Idea
import org.pike.configuration.OperatingSystem
import org.pike.installers.ToolInstaller
import org.pike.utils.FileUtils
import org.pike.utils.ProcessResult
import org.pike.utils.ProcessWrapper

class StartIdeaTask extends DefaultTask{

    {
      group = PikePlugin.PIKE_GROUP
      description = 'Starts an IntelliJ instance for the given project'
    }

    ProcessWrapper processWrapper = new ProcessWrapper()

    FileUtils fileUtils = new FileUtils()

    Idea idea



    @TaskAction
    public void startIdea () {

        ToolInstaller toolInstaller = new ToolInstaller()
        toolInstaller.project = project
        toolInstaller.name = InstallIdeaTask.TOOLNAME

        File startFile = null
        if (OperatingSystem.current.equals(OperatingSystem.MACOS)) {
            startFile = project.file("/Applications/IntelliJ IDEA CE.app/Contents/MacOS/idea") //TODO nicer
        }
        else if (OperatingSystem.current.equals(OperatingSystem.LINUX)) {
            File installationDir = toolInstaller.getInstallationPathOrDefault()
            startFile = new File (fileUtils.getSingleChild(installationDir), 'bin/idea.sh')
        }

        println ("Starting binary  " + startFile.absolutePath)

        String [] commands = [startFile.absolutePath, project.projectDir.absolutePath]
            ProcessResult result = processWrapper.execute(commands)
            if (result.resultCode != 0)
                throw new IllegalStateException("Could not start idea " + result.error)

    }
}
