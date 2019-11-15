package org.pike.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.configuration.Eclipse
import org.pike.configuration.Idea
import org.pike.configuration.OperatingSystem
import org.pike.configuration.PikeExtension
import org.pike.configurators.file.ReplaceLineConfigurator
import org.pike.installers.ToolInstaller
import org.pike.installers.ToolInstallerBuilder
import org.pike.PikePlugin
import org.pike.utils.FileUtils
import org.pike.utils.PikeProperties


class InstallIdeaTask extends DefaultTask {

    {
        group = PikePlugin.PIKE_GROUP
        description = 'Installs and configures an IntelliJ instance for the given project'
    }

    public final static String TOOLNAME = 'IntelliJ'

    public final static String IDEA_INSTALLPATH = 'pike.idea.installpath'

    String version

    ToolInstaller tool

    PikeProperties pikeProperties = new PikeProperties(project)

    FileUtils fileUtils = new FileUtils()

    @TaskAction
    public void prepareIdea() {

        PikeExtension pikeExtension = project.extensions.findByName(PikeExtension.NAME)
        Idea idea = pikeExtension.idea


        if (version == null) {
            version = pikeExtension.idea?.version
        }

        if (version == null)
            throw new IllegalStateException("You did not specify a version for the tool 'IntelliJ'")

        getLogger().info("Install " + TOOLNAME + " version " + version)

        ToolInstallerBuilder toolBuilder = new ToolInstallerBuilder(project, TOOLNAME, version)
        toolBuilder.platform(OperatingSystem.LINUX).url("https://download.jetbrains.com/idea/ideaIC-${version}.tar.gz?_ga=2.213794582.307856837.1571235091-1189283095.1568013896")
        toolBuilder.platform(OperatingSystem.MACOS).url("https://download.jetbrains.com/idea/ideaIC-${version}.dmg?_ga=2.209614864.307856837.1571235091-1189283095.1568013896")
        toolBuilder.platform(OperatingSystem.WINDOWS).url("https://download.jetbrains.com/idea/ideaIC-${version}.win.zip?_ga=2.239423617.307856837.1571235091-1189283095.1568013896")

        tool = toolBuilder.get(OperatingSystem.current)
        File installationPath = tool.install()
        pikeProperties.setProperty(IDEA_INSTALLPATH, installationPath.absolutePath)

        if (idea.xmx) {
            File vmoptionsFile = fileUtils.findFile(installationPath, 'idea.vmoptions')
            logger.lifecycle("Set xmx " + idea.xmx + " in file " + vmoptionsFile.absolutePath)
            ReplaceLineConfigurator configurator = new ReplaceLineConfigurator()
            configurator.configure(logger, vmoptionsFile, "-Xmx", idea.xmx, false)
        }

        for (String nextPlugin: idea.plugins) {
            logger.lifecycle("Install plugin " + nextPlugin)
            
            ToolInstallerBuilder toolBuilderPlugin = new ToolInstallerBuilder(project, TOOLNAME, version).installationPathMustExist()
            toolBuilderPlugin.all().url(nextPlugin)
            toolBuilderPlugin.platform(OperatingSystem.MACOS).installationPath(new File (installationPath, 'Contents/plugins'))
            toolBuilderPlugin.platform(OperatingSystem.WINDOWS).installationPath(new File (installationPath, 'plugins'))
            toolBuilderPlugin.platform(OperatingSystem.LINUX).installationPath(new File (installationPath, 'plugins'))
            ToolInstaller toolPlugin = toolBuilderPlugin.get(OperatingSystem.current)
            toolPlugin.install()
        }


    }
}
