package org.pike.autoinstall

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.TestUtils
import org.pike.holdertasks.ResolveModelTask

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 30.04.13
 * Time: 12:27
 * To change this template use File | Settings | File Templates.
 */
class AutoinstallTaskTest {

    @Test
    public void autoinstallWindows () throws Exception {


        URL jre = TestUtils.projectfile("pike", "src/test/resources/dummyjre.zip").toURI().toURL()
        URL gradle = TestUtils.projectfile("pike", "src/test/resources/dummygradle.zip").toURI().toURL()

        Project project = ProjectBuilder.builder().withName("autoinstallTest").build()
        project.gradle.startParameter.taskNames.add("autoinstall")

        project.apply plugin: 'pike'

        project.defaults {
            defaultuser = 'nightly'
            defaultdomain = 'intra.vsa.de'
            pikeuser = 'root'
            pikepassword = 'j&sAi4a'
            pikegradle = "${gradle}"

        }

        project.operatingsystems {
            windows {
                homedir = "C:\\Users/OleyMa"
                //servicedir = "/etc/init.d"
                appdir = "${homedir}\\jenkins"
                programdir = "${homedir}\\jenkins\\tools"
                cachedir = "${homedir}\\.pike\\cache"
                pikedir = 'C:\\opt\\pike'
                tmpdir = 'C:\\tmp'
                pikejre = "${jre}"
                //appconfigfile = '/etc/profile.d/jenkins_global.sh'
            }
        }

        project.hosts {
            localhost {
                hostname = 'localhost'
                operatingsystem = project.operatingsystems.windows
                environment 'buildnode'
            }


        }

        AutoinstallTask autoinstallTask = project.tasks.findByName("autoinstall")
        autoinstallTask.setHost("localhost")

        println ("Test started in " + project.getProjectDir().absolutePath)

        ResolveModelTask resolveModelTask = project.tasks.resolveModel
        resolveModelTask.resolveModel()


        DummySshRemoting sshremoting = new DummySshRemoting()

        AutoinstallTask task = project.tasks.autoinstall
        AutoinstallWorker.injectedRemoting = sshremoting
        task.installPike()
        File installationPath = task.file(project.operatingsystems.windows.pikedir)

        for (String next: sshremoting.commands)
            println ("<$next>")

        Assert.assertTrue (sshremoting.commands.size() > 0)



    }

    @Test
    public void autoinstallLinux () throws Exception {



        URL jre = TestUtils.projectfile("pike", "src/test/resources/dummyjre.zip").toURI().toURL()
        URL gradle = TestUtils.projectfile("pike", "src/test/resources/dummygradle.zip").toURI().toURL()

        Project project = ProjectBuilder.builder().withName("autoinstallTest").build()
        project.gradle.startParameter.taskNames.add("autoinstall")
        project.apply plugin: 'pike'

        project.defaults {
            defaultuser = 'nightly'
            defaultdomain = 'intra.vsa.de'
            pikeuser = 'root'
            pikepassword = 'j&sAi4a'
            pikegradle = "${gradle}"

        }

        project.operatingsystems {
            linux {
                homedir = '/home/${user}'
                programdir = "${homedir}/swarm/tools"
                cachedir = "${homedir}/.pike/cache"
                pikedir = "/opt/pike"
                pikejre = "${jre}"
                tmpdir = '/tmp'
            }
        }

        project.hosts {
            localhost {
                hostname = 'localhost'
                operatingsystem = project.operatingsystems.linux
                environment 'buildnode'
            }


        }

        println ("Test started in " + project.getProjectDir().absolutePath)

        ResolveModelTask resolveModelTask = project.tasks.resolveModel
        resolveModelTask.resolveModel()


        DummySshRemoting sshremoting = new DummySshRemoting()

        AutoinstallTask task = project.tasks.autoinstall
        AutoinstallWorker.injectedRemoting = sshremoting
        task.setHost("localhost")
        task.installPike()
        File installationPath = task.file(project.operatingsystems.linux.pikedir)

        for (String next: sshremoting.commands)
          println ("<$next>")

        Assert.assertTrue (sshremoting.commands.size() > 0)



    }
}
