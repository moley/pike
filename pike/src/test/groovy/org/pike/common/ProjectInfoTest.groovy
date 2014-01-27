package org.pike.common

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 13.05.13
 * Time: 22:58
 * To change this template use File | Settings | File Templates.
 */
class ProjectInfoTest {



    @Test
    public void allgroups () {

        Project project = ProjectBuilder.builder().withName("autocreateTasksTest").build()
        project.apply plugin: 'pike'

        project.hosts {
            myhost {
                hostgroups = 'test'
            }
            secondhost {
                hostgroups = 'prod'
            }
        }

        ProjectInfo info = new ProjectInfo()
        Collection <String> groups = info.getAllGroups(project)

        Assert.assertEquals ("Number of found groups differ", 2, groups.size())
        Assert.assertTrue ("group test not available ", groups.contains("test"))
        Assert.assertTrue ("group prod not available ", groups.contains("prod"))

    }
}
