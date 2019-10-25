package org.pike.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.PikePlugin
import org.pike.configuration.FileType
import org.pike.configuration.OperatingSystem
import org.pike.configuration.PikeExtension
import org.pike.installers.ToolInstaller
import org.pike.installers.ToolInstallerBuilder

class InstallVsCodeTask extends DefaultTask{

    {
        group = PikePlugin.PIKE_GROUP
        description = 'Installs and configures an Visual studio code instance for the given project'
    }

    public final static String TOOLNAME = 'VisualStudioCode'

    String version

    ToolInstaller tool

    @TaskAction
    public void prepareVsCode () {

        PikeExtension pikeExtension = project.extensions.findByName(PikeExtension.NAME)
        if (version == null) {
            version = pikeExtension.idea?.version
        }

        logger.info("prepare vscode ide")
        ToolInstallerBuilder toolBuilder = new ToolInstallerBuilder(project, TOOLNAME, version)
        //TODO toolBuilder.tool(OperatingSystem.LINUX, "https://download.jetbrains.com/idea/ideaIC-${version}.tar.gz?_ga=2.213794582.307856837.1571235091-1189283095.1568013896")
        toolBuilder.platformDetails(OperatingSystem.MACOS, "https://update.code.visualstudio.com/1.39.2/darwin/stable", FileType.ZIP)
        //TODO toolBuilder.tool(OperatingSystem.WINDOWS, "https://download.jetbrains.com/idea/ideaIC-${version}.win.zip?_ga=2.239423617.307856837.1571235091-1189283095.1568013896")

        tool = toolBuilder.get()



        tool.install(OperatingSystem.current)





    }
}
