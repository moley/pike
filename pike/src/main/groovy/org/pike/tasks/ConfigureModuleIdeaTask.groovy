package org.pike.tasks

import groovy.io.FileType
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

    @TaskAction
    public void configureModule() {

        File basepath = configureUtils.getBasePath(project, module.configuration)
        File clonePath = new File(basepath, module.name)
        logger.lifecycle("Configure module in " + clonePath.absolutePath)

        ToolConfiguratorBuilder toolConfiguratorBuilder = new ToolConfiguratorBuilder(project, InstallIdeaTask.TOOLNAME)
        toolConfiguratorBuilder = toolConfiguratorBuilder.platformDetails(OperatingSystem.MACOS, "${System.getProperty('user.home')}/Library/Preferences/${idea.globalConfFolder}")
        ToolConfigurator toolConfigurator = toolConfiguratorBuilder.get()

        PikeExtension pikeExtension = project.extensions.findByName(PikeExtension.NAME)
        Configuration mergedConfiguration = pikeExtension.getMergedConfiguration(module.configuration)

        ToolConfiguratorPlatformDetails platformDetails = toolConfigurator.getPlatformDetails(OperatingSystem.getCurrent())
        File globalConfigPath = new File (platformDetails.globalConfigurationPath)

        IdeaConfiguration ideaConfiguration = new IdeaConfiguration(project.logger, globalConfigPath, projectConfigPaths)
        ideaConfiguration.apply(mergedConfiguration, false)


    }

    public List<File> getProjectConfigPaths() {
        List<File> foundSettingsPaths = new ArrayList<File>()
        project.projectDir.eachFileRecurse(FileType.DIRECTORIES) {
            if (it.name.equals('.idea')) {
                project.logger.info("Found project config path " + it.absolutePath)
                foundSettingsPaths.add(it)
            }
        }

        project.logger.info("Found " + foundSettingsPaths.size() + " project config paths in project dir " + project.projectDir.absolutePath)
        return foundSettingsPaths

    }
}
