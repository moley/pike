package org.pike.tasks

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import org.pike.PikePlugin
import org.pike.configuration.Idea
import org.pike.configuration.Module


class ConfigureIdeaModuleTaskTest {

    @Test
    public void task () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            configuration {
                encoding 'UTF-8'
            }
        }

        Idea idea = new Idea()
        idea.globalConfFolder 'IdeaIC2019.2'


        Module module = new Module()
        module.name = 'module'

        ConfigureModuleIdeaTask configureModuleIdeaTask = project.tasks.create('configureModuleIdea', ConfigureModuleIdeaTask)
        configureModuleIdeaTask.module = module
        configureModuleIdeaTask.idea = idea
        configureModuleIdeaTask.configureModule()

        println configureModuleIdeaTask.ideaConfiguration.globalConfigPath



    }
}
