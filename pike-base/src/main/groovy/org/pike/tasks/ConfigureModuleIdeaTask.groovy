package org.pike.tasks


import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.PikePlugin
import org.pike.configuration.*
import org.pike.configurators.ToolConfigurator
import org.pike.configurators.ToolConfiguratorBuilder
import org.pike.configurators.ToolConfiguratorPlatformDetails
import org.pike.utils.ConfigureUtils

class ConfigureModuleIdeaTask extends DefaultTask {

    {
        group = PikePlugin.PIKE_GROUP
    }

    Module module

    Idea idea

    ConfigureUtils configureUtils = new ConfigureUtils()

    IdeaConfiguration ideaConfiguration

    @TaskAction
    public void configureModule() {

        File basepath = configureUtils.getBasePath(project, module.configuration)
        File clonePath = new File(basepath, module.name)
        logger.lifecycle("Configure module in " + clonePath.absolutePath)

        if (idea.globalConfFolder == null)
            throw new IllegalStateException("No global configuration path defined in closure pike.idea{}. Define one which matches your version (e.g. globalConfFolder = 'IdeaIC2019.2')")

        ToolConfiguratorBuilder toolConfiguratorBuilder = new ToolConfiguratorBuilder(project, InstallIdeaTask.TOOLNAME)
        toolConfiguratorBuilder = toolConfiguratorBuilder.platformDetails(OperatingSystem.MACOS, "${System.getProperty('user.home')}/Library/Preferences/${idea.globalConfFolder}")
        toolConfiguratorBuilder = toolConfiguratorBuilder.platformDetails(OperatingSystem.LINUX, "${System.getProperty('user.home')}/Library/Preferences/${idea.globalConfFolder}")
        ToolConfigurator toolConfigurator = toolConfiguratorBuilder.get()

        PikeExtension pikeExtension = project.extensions.findByName(PikeExtension.NAME)
        Configuration mergedConfiguration = pikeExtension.getMergedConfiguration(module.configuration)

        ToolConfiguratorPlatformDetails platformDetails = toolConfigurator.getPlatformDetails(OperatingSystem.getCurrent())
        File globalConfigPath = new File (platformDetails.globalConfigurationPath)
        File projectConfigPath = new File (clonePath, '.idea')

        ideaConfiguration = new IdeaConfiguration()
        ideaConfiguration.apply(project.logger, globalConfigPath, null, projectConfigPath, mergedConfiguration, false)


    }
}
