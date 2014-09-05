package org.pike.org.pike

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.AutoinstallUtil
import org.pike.InstallPikeTask

/**
 * Created by OleyMa on 04.09.14.
 */
class InstallPikeTaskTest {

    /**
     * checks if the operatingsystem defined in autoinstall closure is used instead of the concrete os defined in
     * host
     */
    @Test
    public void getAutoinstallOs () {

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
                operatingsystem = project.operatingsystems.suse
            }
        }

        project.evaluate()


        InstallPikeTask installPikeTask = project.tasks.installPike
        installPikeTask.installPathRoot = AutoinstallUtil.getInstallPath(project)
        File installerFile = installPikeTask.getInstallerFile(project.hosts.host1, project.autoinstall)
        Assert.assertEquals ('pikeinstaller-linux.installer', installerFile.name)
    }
}
