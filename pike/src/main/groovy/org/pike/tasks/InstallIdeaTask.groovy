package org.pike.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.configuration.OperatingSystem
import org.pike.configuration.PikeExtension
import org.pike.installers.ToolInstaller
import org.pike.installers.ToolInstallerBuilder
import org.pike.PikePlugin


class InstallIdeaTask extends DefaultTask {

    {
        group = PikePlugin.PIKE_GROUP
        description = 'Installs and configures an IntelliJ instance for the given project'
    }

    public final static String TOOLNAME = 'IntelliJ'

    String version

    ToolInstaller tool

    @TaskAction
    public void prepareIdea() {

        PikeExtension pikeExtension = project.extensions.findByName(PikeExtension.NAME)


        if (version == null) {
            version = pikeExtension.idea?.version
        }

        if (version == null)
            throw new IllegalStateException("You did not specify a version for the tool 'IntelliJ'")

        getLogger().info("prepare idea ide")

        ToolInstallerBuilder toolBuilder = new ToolInstallerBuilder(project, TOOLNAME, version)
        toolBuilder.platformDetails(OperatingSystem.LINUX, "https://download.jetbrains.com/idea/ideaIC-${version}.tar.gz?_ga=2.213794582.307856837.1571235091-1189283095.1568013896")
        toolBuilder.platformDetails(OperatingSystem.MACOS, "https://download.jetbrains.com/idea/ideaIC-${version}.dmg?_ga=2.209614864.307856837.1571235091-1189283095.1568013896")
        toolBuilder.platformDetails(OperatingSystem.WINDOWS, "https://download.jetbrains.com/idea/ideaIC-${version}.win.zip?_ga=2.239423617.307856837.1571235091-1189283095.1568013896")

        toolBuilder.installationPath()

        tool = toolBuilder.get()


        tool.install(OperatingSystem.current)


    }
}
