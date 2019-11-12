package org.pike.tasks

import com.google.common.io.Files
import groovy.mock.interceptor.MockFor
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.PikePlugin
import org.pike.configuration.OperatingSystem
import org.pike.installers.ToolInstaller
import org.pike.installers.ToolInstallerBuilder

class InstallIdeaTaskTest {

    @Test
    public void install () {

        File installPath = Files.createTempDir()
        def mockedToolInstaller = new MockFor(ToolInstaller)
        mockedToolInstaller.demand.setToolInstallerBuilder{ToolInstallerBuilder a-> println "ToolInstallerBuilder: " + a} //IDEA itself
        mockedToolInstaller.demand.setOperatingSystem{OperatingSystem a-> Assert.assertEquals (OperatingSystem.current, a)}
        mockedToolInstaller.demand.install {return installPath}

        mockedToolInstaller.demand.setToolInstallerBuilder{ToolInstallerBuilder a-> println "ToolInstallerBuilder: " + a
            Assert.assertTrue ("InstallationPath of plugins not contained in InstallationPath of Idea", a.platform(OperatingSystem.getCurrent()).installationPath.absolutePath.startsWith(installPath.absolutePath))
        } //Plugin
        mockedToolInstaller.demand.setOperatingSystem{OperatingSystem a-> "OperatingSystem: " + a}
        mockedToolInstaller.demand.install {return installPath}

        mockedToolInstaller.use {

            Project project = ProjectBuilder.builder().build()
            project.plugins.apply(PikePlugin)
            project.pike {
                idea {
                    version '2019.2'
                    plugin 'https://plugins.jetbrains.com/files/6546/71101/EclipseFormatter.zip?updateId=71101&pluginId=6546&family=INTELLIJ'
                }
            }

            InstallIdeaTask installIdeaTask = project.tasks.installIdea
            installIdeaTask.prepareIdea()
        }

    }
}
