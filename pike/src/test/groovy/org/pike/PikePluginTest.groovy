package org.pike

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.PikePlugin


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

    }
}
