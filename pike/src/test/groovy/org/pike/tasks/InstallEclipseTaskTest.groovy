package org.pike.tasks


import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import org.pike.PikePlugin

class InstallEclipseTaskTest {

    @Test
    public void task () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            eclipse {
                repo 'https://download.eclipse.org/releases/2019-09/'
                feature 'org.eclipse.egit'
                feature 'org.eclipse.buildship'
            }
        }


    }
}
