package org.pike

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import org.pike.cache.CacheManager
import org.pike.holdertasks.ResolveModelTask
import org.pike.test.TestUtils

/**
 * Tests creating installer tasks when os is defined in multiple hosts
 */
@Slf4j
class AutoinstallPluginMultipleTest {


    private Project project


    @Test
    public void checkTasks() {

        CacheManager.cacheDir = new File('build/cache')

        String pikeJre = 'http://installbuilder.bitrock.com/java/jre1.7.0_65-osx.zip'
        String pikeGradle = "http://services.gradle.org/distributions/${TestUtils.CURRENT_DIST}"
        File projectDir = new File('build/testcase/autoinstallPluginTest')
        project = ProjectBuilder.builder().withName("autoinstallPluginTest").withProjectDir(projectDir).build()
        project.apply plugin: 'autoinstall'

        project.defaults {
            defaultuser = 'nightly'
            currentHost = 'vtbuild11-x'
            pikegradle = pikeGradle
        }

        project.operatingsystems {

            linux {
                homedir = "home/${project.defaults.defaultuser}"
                programdir = "tools"
                pikedir = "pike"
                pikejre32 = pikeJre
            }

            windows { //No autoinstall
            }
        }

        project.autoinstall {
            os(project.operatingsystems.linux)
        }

        project.hosts {
            testhostLinux1 {
                hostname = 'testhost'
                operatingsystem = project.operatingsystems.linux
            }
            testhostLinux2 {
                hostname = 'testhost'
                operatingsystem = project.operatingsystems.linux
            }
            testhostWindows {
                operatingsystem = project.operatingsystems.windows
            }
        }

        project.evaluate()

        ResolveModelTask resolveModelTask = project.tasks.resolveModel
        resolveModelTask.resolveModel()

        project.tasks.each { log.info(it.name) }


    }

}
