package org.pike

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.tasks.Copy
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import org.pike.holdertasks.ResolveModelTask
import org.pike.test.TestUtils

/**
 * Created by OleyMa on 31.07.14.
 */
@Slf4j
class AutoinstallPluginTest {

    private Project project


    public Project createProject (final boolean validJre, final boolean validGradle) {

        String pikeJre = validJre ? 'http://installbuilder.bitrock.com/java/jre1.7.0_65-osx.zip' : null
        String pikeGradle = validGradle ? "http://services.gradle.org/distributions/${TestUtils.CURRENT_DIST}": null
        File projectDir = new File ('build/testcase/autoinstallPluginTest')
        project = ProjectBuilder.builder().withName("autoinstallPluginTest").withProjectDir(projectDir).build()
        project.apply plugin: 'autoinstall'

        project.defaults {
            defaultuser = 'nightly'
            currentHost = 'vtbuild11-x'
            pikegradle = pikeGradle
        }

        project.operatingsystems {

            linux {
                createInstaller = true
                homedir = "home/${project.defaults.defaultuser}"
                programdir = "tools"
                cachedir = "build/cache"
                pikedir = "pike"
                tmpdir = "tmp"
                pikejre = pikeJre
            }

        }

        project.hosts {
            testhost {
                hostname = 'testhost'
                operatingsystem = project.operatingsystems.linux
            }
        }

        project.evaluate()

        ResolveModelTask resolveModelTask = project.tasks.resolveModel
        resolveModelTask.resolveModel()

        project.tasks.each {log.info(it.name)}

        return project

    }

    @Test(expected = IllegalStateException)
    public void installGradleNotSet () {
        Project project = createProject(true, false)
        DownloadAndUnzipTask prepareGradleLinuxTask = project.tasks.prepareInstallerlinuxGradle
        println (prepareGradleLinuxTask.to)
        println (prepareGradleLinuxTask.from)
        prepareGradleLinuxTask.downloadAndUnzip()

    }

    @Test
    public void installGradle () {
        Project project = createProject(true, true)
        DownloadAndUnzipTask prepareGradleLinuxTask = project.tasks.prepareInstallerlinuxGradle
        println (prepareGradleLinuxTask.to)
        println (prepareGradleLinuxTask.from)
        //prepareGradleLinuxTask.downloadAndUnzip() TODO asserts
    }

    @Test
    public void installJre () {
        Project project = createProject(true, true)
        DownloadAndUnzipTask prepareGradleLinuxTask = project.tasks.prepareInstallerlinuxJre
        //prepareGradleLinuxTask.downloadAndUnzip() TODO asserts
    }

    @Test(expected=IllegalStateException)
    public void installJreNotSet () {
        Project project = createProject(false, true)
        DownloadAndUnzipTask prepareGradleLinuxTask = project.tasks.prepareInstallerlinuxJre
        prepareGradleLinuxTask.downloadAndUnzip()
    }

    @Test
    public void installLibs () {
        Project project = createProject(true, true)
        Copy prepareLibsTask = project.tasks.prepareInstallerlinuxLibs
        //prepareLibsTask.execute() TODO asserts
    }

    @Test
    public void installScripts () {
        Project project = createProject(true, true)
        CreateLinuxScript linuxScriptTask = project.tasks.prepareInstallerlinuxStartscript
        //linuxScriptTask.create() TODO asserts
    }
}
