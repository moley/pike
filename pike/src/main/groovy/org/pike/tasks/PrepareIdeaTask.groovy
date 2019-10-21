package org.pike.tasks

import org.gradle.api.tasks.TaskAction
import org.pike.configuration.OperatingSystem
import org.pike.installers.Tool
import org.pike.installers.ToolBuilder

class PrepareIdeaTask extends PikeTask{

    {
        description = 'Installs and configures an IntelliJ instance for the given project'
    }

    public final static String TOOLNAME = 'IntelliJ'

    String version

    Tool tool

    @TaskAction
    public void prepareIdea () {

        if (version == null) {
            version = pikeExtension.idea?.version
        }

        if (version == null)
            throw new IllegalStateException("You did not specify a version for the tool 'IntelliJ'")

        getLogger().info("prepare idea ide")

        ToolBuilder toolBuilder = new ToolBuilder(project, TOOLNAME, version)
        toolBuilder = toolBuilder.withInstallationPath(new File ("/Application/IntelliJ IDEA CE.app"))
        toolBuilder.tool(OperatingSystem.LINUX, "https://download.jetbrains.com/idea/ideaIC-${version}.tar.gz?_ga=2.213794582.307856837.1571235091-1189283095.1568013896")
        toolBuilder.tool(OperatingSystem.MACOS, "https://download.jetbrains.com/idea/ideaIC-${version}.dmg?_ga=2.209614864.307856837.1571235091-1189283095.1568013896")
        toolBuilder.tool(OperatingSystem.WINDOWS, "https://download.jetbrains.com/idea/ideaIC-${version}.win.zip?_ga=2.239423617.307856837.1571235091-1189283095.1568013896")

        tool = toolBuilder.get()



        tool.install(OperatingSystem.current)





    }
}
