package org.pike

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.PikePlugin
import org.pike.tasks.ConfigureEclipseTask
import org.pike.tasks.ConfigureIdeaTask
import org.pike.tasks.ConfigureModuleEclipseTask
import org.pike.tasks.ConfigureModuleIdeaTask


class PikePluginTest {

    @Test
    void apply () {

        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            git {
                gitmodule ('pike', 'https://github.com/moley/pike.git') {
                    configuration {
                        encoding 'UTF-8'
                    }
                }
                gitmodule ('leguan', 'https://github.com/moley/leguan')
            }

            configuration {
                encoding 'ISO-8859-15'
                basepath 'build/basepath'
            }

        }

        Assert.assertNotNull ("Install task not created", project.tasks.install)
        Assert.assertNull ("Configure Eclipse task created", project.tasks.findByName('configureEclipse'))
        Assert.assertNull ("Configure Eclipse task created", project.tasks.findByName('configureIdea'))

    }

    @Test
    public void eclipse () {

        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            git {
                gitmodule ('pike', 'https://github.com/moley/pike.git') {
                    configuration {
                        encoding 'UTF-8'
                    }
                }
                gitmodule ('leguan', 'https://github.com/moley/leguan')
            }

            eclipse {
                repo 'https://download.eclipse.org/releases/2019-09/'
                feature 'org.eclipse.egit'
            }
        }

        project.evaluate()

        Assert.assertNotNull ("Install task not created", project.tasks.install)
        ConfigureEclipseTask configureEclipseTask = project.tasks.configureEclipse
        ConfigureModuleEclipseTask configureEclipseTaskLeguan = project.tasks.configureEclipseLeguan
        ConfigureModuleEclipseTask configureEclipseTaskPike = project.tasks.configureEclipsePike
        Assert.assertTrue ("Dependency is missing configure module leguan", configureEclipseTask.dependsOn.contains(configureEclipseTaskLeguan))
        Assert.assertTrue ("Dependency is missing configure module pike", configureEclipseTask.dependsOn.contains(configureEclipseTaskPike))

    }

    @Test
    public void idea () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            git {
                gitmodule ('pike', 'https://github.com/moley/pike.git') {
                    configuration {
                        encoding 'UTF-8'
                    }
                }
                gitmodule ('leguan', 'https://github.com/moley/leguan')
            }

            idea {


            }
        }

        project.evaluate()

        Assert.assertNotNull ("Install task not created", project.tasks.install)
        ConfigureIdeaTask configureIdeaTask = project.tasks.configureIdea

        ConfigureModuleIdeaTask configureIdeaTaskLeguan = project.tasks.configureIdeaLeguan
        ConfigureModuleIdeaTask configureIdeaTaskPike = project.tasks.configureIdeaPike
        Assert.assertTrue ("Dependency is missing configure module leguan", configureIdeaTask.dependsOn.contains(configureIdeaTaskLeguan))
        Assert.assertTrue ("Dependency is missing configure module pike", configureIdeaTask.dependsOn.contains(configureIdeaTaskPike))

    }
}
