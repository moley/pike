package org.pike

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.cache.CacheManager
import org.pike.test.TestUtils

/**
 * Tests to check if autoinstall tasks are created
 */
@Slf4j
class AutoinstallPluginTest {

    private Project project


    public Project createProject (final boolean validJre, final boolean validGradle) {

        CacheManager.cacheDir = new File ('build/cache')

        String pikeJre = validJre ? 'http://installbuilder.bitrock.com/java/jre1.7.0_65-osx.zip' : null
        String pikeGradle = validGradle ? "http://services.gradle.org/distributions/${TestUtils.CURRENT_DIST}": null
        File projectDir = new File ('build/testcase/autoinstallPluginTest')
        project = ProjectBuilder.builder().withName("autoinstallPluginTest").withProjectDir(projectDir).build()
        project.apply plugin: 'autoinstall'

        project.defaults {
            fsUser = 'nightly'
            currentHost = 'testhost'
            pikegradle = pikeGradle
        }

        project.operatingsystems {

            linux {
                homedir = "home/${project.defaults.fsUser}"
                programdir = "tools"
                pikedir = "pike"
                pikejre32 = pikeJre
            }

            windows { //No autoinstall

            }

        }



        project.autoinstall {
            os (project.operatingsystems.linux)
        }

        project.hosts {
            testhost {
                hostname = 'testhost'
                operatingsystem = project.operatingsystems.linux
            }
            testhost2 {
                operatingsystem = project.operatingsystems.windows
            }
        }

        project.evaluate()


        project.tasks.each {log.info(it.name)}

        return project

    }

    @Test(expected = IllegalStateException)
    public void installGradleNotSet () {
        Project project = createProject(true, false)
        DownloadAndUnzipTask prepareGradleLinuxTask = project.tasks.prepareInstallerlinuxGradle

        prepareGradleLinuxTask.downloadAndUnzip()

    }

    @Test
    public void installGradle () {
        Project project = createProject(true, true)
        DownloadAndUnzipTask prepareGradleLinuxTask = project.tasks.prepareInstallerlinuxGradle
        println (prepareGradleLinuxTask.to)
        println (prepareGradleLinuxTask.from)

        Assert.assertNull (project.tasks.findByName('prepareInstallerwindowsGradle'))

        //prepareGradleLinuxTask.downloadAndUnzip() TODO asserts
    }

    @Test
    public void installJre () {
        Project project = createProject(true, true)
        DownloadAndUnzipTask prepareGradleLinuxTask = project.tasks.prepareInstallerlinuxJre
        Assert.assertNull (project.tasks.findByName('prepareInstallerwindowsJre'))
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
        Assert.assertNull (project.tasks.findByName('prepareInstallerwindowsLibs'))
        //prepareLibsTask.execute() TODO asserts
    }

    @Test
    public void installScripts () {
        Project project = createProject(true, true)
        CreateLinuxScript linuxScriptTask = project.tasks.prepareInstallerlinuxStartscript
        Assert.assertNull (project.tasks.findByName('prepareInstallerwindowsStartscript'))
        //linuxScriptTask.create() TODO asserts
    }
}
