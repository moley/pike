package org.pike.org.pike

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.CreateLinuxScript

/**
 * Test to check CreateLinuxScript task
 */
class CreateLinuxScriptTest {

    @Test
    public void createScript () {


        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'autoinstall'

        project.defaults {
            pikegradle = 'some pikegradle'
        }

        project.autoinstall {
            os (project.operatingsystems.linux)
        }

        project.hosts {
            host1 {
                operatingsystem = project.operatingsystems.linux
            }
        }

        File buildDir = new File (project.projectDir, 'build/install/linux')
        File gradleFile = new File (buildDir, 'gradle/bin/gradle')
        gradleFile.parentFile.mkdirs()
        Assert.assertTrue (gradleFile.createNewFile())

        File jreFile = new File (buildDir, 'jre/bin/java')
        jreFile.parentFile.mkdirs()
        Assert.assertTrue (jreFile.createNewFile())

        project.evaluate()

        CreateLinuxScript linuxScriptTask = project.tasks.prepareInstallerlinuxStartscript
        linuxScriptTask.create()

        String scriptText = linuxScriptTask.scriptFileCreated.text

        Assert.assertTrue (scriptText.contains('export GRADLE_HOME=gradle'))
        Assert.assertTrue (scriptText.contains('export JAVA_HOME=jre'))
    }
}
