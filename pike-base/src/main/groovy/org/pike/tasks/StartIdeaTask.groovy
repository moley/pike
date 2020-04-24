package org.pike.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.PikePlugin
import org.pike.configuration.Idea
import org.pike.configuration.OperatingSystem
import org.pike.installers.ToolInstaller
import org.pike.utils.FileUtils
import org.pike.utils.PikeProperties
import org.pike.utils.ProcessResult
import org.pike.utils.ProcessWrapper

class StartIdeaTask extends DefaultTask{

    {
      group = PikePlugin.PIKE_GROUP
      description = 'Starts an IntelliJ instance for the given project'
    }

    ProcessWrapper processWrapper = new ProcessWrapper()


    PikeProperties pikeProperties = new PikeProperties(project)

    OperatingSystem operatingSystem = OperatingSystem.current

    @TaskAction
    public void startIdea () {

        File installationDir = pikeProperties.getFileProperty(InstallIdeaTask.IDEA_INSTALLPATH)
        if (installationDir == null)
            throw new IllegalStateException("No installation dir for idea set, please call task installIdea before")

        File startFile = null
        if (operatingSystem.equals(OperatingSystem.MACOS)) {
            startFile = new File(installationDir, "Contents/MacOS/idea")
        }
        else if (operatingSystem.equals(OperatingSystem.LINUX)) {
            startFile = new File (installationDir, 'bin/idea.sh')
        }

        println ("Starting binary  " + startFile.absolutePath)

        String [] commands = [startFile.absolutePath, project.projectDir.absolutePath]
            ProcessResult result = processWrapper.execute(commands)
            if (result.resultCode != 0)
                throw new IllegalStateException("Could not start idea (" + result.error + ")")

    }
}
