package org.pike.info

import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.test.TestUtils

/**
 * Created by OleyMa on 01.09.14.
 */
class InfoHostsTaskTest {

    @Test
    public void call () {

        ProjectInternal project = ProjectBuilder.builder().withName("autocreateTasksTest").build()
        project.apply plugin: 'pike'

        project.hosts {
            host1 {
                operatingsystem = project.operatingsystems.suse
            }

            host2 {
                operatingsystem = project.operatingsystems.suse
            }
        }

        project.evaluate()

        InfoHostsTask task = project.tasks.getByName('hosts')
        task.show()

        Assert.assertTrue(task.completeString.contains('host1'))
        Assert.assertTrue(task.completeString.contains('host2'))

    }
}
