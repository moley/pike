package org.pike.tasks


import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.PikePlugin
import org.pike.configuration.Configuration
import org.pike.configuration.Eclipse
import org.pike.configuration.Module
import org.pike.configuration.PikeExtension
import org.pike.utils.ConfigureUtils

import java.nio.charset.Charset

class ConfigureModuleEclipseTask extends DefaultTask {

    {
        group = PikePlugin.PIKE_GROUP
    }

    Module module

    Eclipse eclipse

    ConfigureUtils configureUtils = new ConfigureUtils()


    private File getWorkspacePath() {
        String buildDirPath = project.buildDir.absolutePath
        File workspacesPath = new File(System.getProperty("user.home"), '.goomph/ide-workspaces')
        for (File next : workspacesPath.listFiles()) {
            if (next.isFile() && next.getName().endsWith("-owner")) {
                String home = FileUtils.readFileToString(next, Charset.defaultCharset())
                if (home.startsWith(buildDirPath)) {
                    File workspacePath = new File(next.parentFile, next.name.substring(0, next.name.length() - 6))
                    if (!workspacesPath.exists())
                        throw new IllegalStateException("Workspace configuration path " + workspacesPath.absolutePath + " does not exist")
                    if (!workspacesPath.isDirectory())
                        throw new IllegalStateException("Workspace configuration path " + workspacesPath.absolutePath + " is no directory")

                    project.logger.info("Found workspace config path " + workspacesPath.absolutePath)
                    return workspacePath
                }
            }
        }

        throw new IllegalStateException("No workspace found for project path " + project.projectDir.absolutePath)
    }

    @TaskAction
    public void configureModule() {

        File basepath = configureUtils.getBasePath(project, module.configuration)
        File clonePath = new File(basepath, module.name)
        logger.lifecycle("Configure eclipse module in " + clonePath.absolutePath)

        PikeExtension pikeExtension = project.extensions.findByName(PikeExtension.NAME)
        Configuration mergedConfiguration = pikeExtension.getMergedConfiguration(module.configuration)

        File workspaceConfigPath = workspacePath
        File projectConfigPath = new File (clonePath, '.settings')


        EclipseConfiguration eclipseConfiguration = new EclipseConfiguration()
        eclipseConfiguration.apply(project.logger,
                null,
                workspaceConfigPath,
                projectConfigPath, mergedConfiguration, false)

    }
}
