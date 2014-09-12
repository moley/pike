package org.pike.vagrant

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.holdertasks.ResolveModelTask

/**
 * Created by OleyMa on 11.08.14.
 */
@Slf4j
class VagrantPluginTest {


    public Project createProject () {

        Project project = ProjectBuilder.builder().withName("vagrantPluginTest").build()
        project.apply plugin: 'vagrant'

        project.defaults {
            currentHost = 'vtbuild11-x'
        }

        project.operatingsystems {
            linux {
            }

            windows { //No vagrant
            }
        }

        project.vagrant {
            linux {
                box 'https://cloud-images.ubuntu.com/vagrant/trusty/current/trusty-server-cloudimg-i386-vagrant-disk1.box'
            }
        }

        project.hosts {
            testhost {
                operatingsystem = project.operatingsystems.linux
            }
            testhost2 {
                operatingsystem = project.operatingsystems.windows
            }
        }

        project.evaluate()

        ResolveModelTask resolveModelTask = project.tasks.resolveModel
        resolveModelTask.resolveModel()

        project.tasks.each {log.info(it.name)}

        return project

    }

    @Test
    public void testCreateTasks () {
        Project project = createProject()

        InstallPikeInVmTask installPikeInVmTask = project.tasks.getByName('installPikeVm')
        Assert.assertNotNull (installPikeInVmTask)

        StartRemoteBuildInVmTask configurePikeInVmTask = project.tasks.getByName('provisionVm')
        Assert.assertNotNull(configurePikeInVmTask)

    }
}
