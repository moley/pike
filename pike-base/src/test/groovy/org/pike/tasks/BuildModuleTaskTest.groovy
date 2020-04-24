package org.pike.tasks

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import org.pike.PikePlugin

class BuildModuleTaskTest {

    @Test(expected = UnsupportedOperationException)
    public void buildModule () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            git {
                gitmodule('pike', 'https://github.com/moley/pike.git') {
                    configuration {
                        encoding 'UTF-8'
                    }
                }
            }
        }

        File buildFile = new File (project.projectDir, 'pike/build.gradle')
        buildFile.parentFile.mkdirs()
        buildFile.text = """apply {plugin 'java'} """

        BuildModuleTask buildModuleTask = project.tasks.buildPike
        buildModuleTask.buildModule()


    }
}
