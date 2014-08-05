package org.pike.common

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.remotetasks.StartRemoteBuildTask

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 20.09.13
 * Time: 10:27
 * To change this template use File | Settings | File Templates.
 */
class RemoteTaskTest {

    private String TASKCONFIGUREREMOTE = "configureRemotes"

    /**
     * creates a valid project
     * @param param param to be called as additional task
     * @return project
     */
    private Project prepareProject (final String taskname, final String paramHost, final String paramEnv = null) {
        Project project = ProjectBuilder.builder().withName("autocreateTasksTest").build()
        project.gradle.startParameter.taskNames = [taskname]
        project.apply plugin: 'pike'

        project.hosts {
            myhost {
                hostgroups = 'test'
            }

            myhost2 {
                hostgroups = 'test'
            }

            secondhost {
                hostgroups = 'prod'
            }

            secondhost2 {
                hostgroups = 'prod, test'
            }
        }

        project.environments {
            env1 {

            }

            env2 {

            }
        }

        def task = project.tasks.findByName(taskname)
        if (paramHost != null)
          task.setHost(paramHost)
        if (paramEnv != null)
          task.setEnv (paramEnv)

        return project
    }


    private StartRemoteBuildTask getRemoteBuildTask (final Project project) {
        return project.tasks.findByName(TASKCONFIGUREREMOTE)
    }

    /**
     * creates a invalid project
     * Defines a host and a hostgroup with the same name
     * @param param param to be called as additional task
     * @return project
     */
    private StartRemoteBuildTask prepareInvalidProject (final String param) {
        Project project = ProjectBuilder.builder().withName("autocreateTasksTest").build()
        project.gradle.startParameter.taskNames.add("autoinstall")
        project.apply plugin: 'pike'

        project.hosts {
            test {
                hostgroups = 'test'
            }
        }

        StartRemoteBuildTask task = project.tasks.findByName(TASKCONFIGUREREMOTE)
        task.setHost(param)

        return task
    }

    @Test
    public void buildGroup () {
        //building test remains in building 3 hosts
        StartRemoteBuildTask task = getRemoteBuildTask(prepareProject(TASKCONFIGUREREMOTE, "test"))
        Assert.assertEquals(3, task.hostsToBuild.size())
        Assert.assertEquals(TASKCONFIGUREREMOTE, task.project.gradle.startParameter.taskNames.get(0))
    }

    @Test
    public void buildHost () {
        //building test remains in building 3 hosts
        StartRemoteBuildTask task = getRemoteBuildTask(prepareProject(TASKCONFIGUREREMOTE, "myhost"))
        Assert.assertEquals(1, task.hostsToBuild.size())
        Assert.assertEquals(TASKCONFIGUREREMOTE, task.project.gradle.startParameter.taskNames.get(0))
    }

    @Test(expected = IllegalStateException.class)
    public void buildEverythingIsForbidden () {
        //building test remains in building 3 hosts
        StartRemoteBuildTask task = getRemoteBuildTask(prepareProject(TASKCONFIGUREREMOTE, ""))
        task.hostsToBuild
    }

    @Test(expected = IllegalStateException.class)
    public void buildNothing () {
        //building test remains in building 3 hosts
        StartRemoteBuildTask task = getRemoteBuildTask(prepareProject(TASKCONFIGUREREMOTE, "rotz"))
        task.hostsToBuild
    }

    @Test(expected = IllegalStateException.class)
    public void buildProjectWithNodeNameAndGroupEquals () {
        StartRemoteBuildTask task = prepareInvalidProject("test") //name and group named equal
        task.hostsToBuild
    }

    @Test(expected = IllegalStateException.class)
    public void buildProjectWithNodeHostnameAndGroupEquals () {
        StartRemoteBuildTask task = prepareInvalidProject("test")   //hostname and group named equal
        task.hostsToBuild
    }

    @Test
    public void validEnv () {
        StartRemoteBuildTask task = getRemoteBuildTask(prepareProject(TASKCONFIGUREREMOTE, "myhost", "env1"))
        Assert.assertEquals ("installEnv1", task.getEnv())

    }

    @Test(expected = IllegalStateException.class)
    public void invalidEnv () {
        StartRemoteBuildTask task = getRemoteBuildTask(prepareProject(TASKCONFIGUREREMOTE, "myhost", "envInvalid"))
        task.getEnv()
    }


}
