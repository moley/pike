package org.pike

import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.tasks.DelegatingTask
import org.pike.test.TestUtils

/**
 * Created by OleyMa on 02.09.14.
 */
class PreAndPostTest {

    @Test
    public void pre () {
        ProjectInternal project = ProjectBuilder.builder().withName("pre").build()
        project.apply plugin: 'pike'

        project.defaults {
            currentHost = 'local'
        }

        project.hosts {
            local {
                operatingsystem = project.operatingsystems.redhat
                environment 'anything1'
                environment 'anything2'
                pre 'anythingPre'
            }
        }

        project.environments {
            anything1 {
                download {}
            }
            anything2 {
                download {}
            }
            anythingPre {
                download {}
            }
        }

        TestUtils.prepareModel(project)


        DelegatingTask install1task = project.tasks.installAnything1
        DelegatingTask install2task = project.tasks.installAnything2
        DelegatingTask installPretask = project.tasks.installAnythingPre

        Assert.assertNotNull(install1task)
        Assert.assertNotNull(install2task)

        Assert.assertNotNull(installPretask)




    }

    @Test
    public void post () {
        ProjectInternal project = ProjectBuilder.builder().withName("pre").build()
        project.apply plugin: 'pike'

        project.defaults {
            currentHost = 'local'
        }

        project.hosts {
            local {
                operatingsystem = project.operatingsystems.redhat
                environment 'anything1'
                environment 'anything2'
                post 'anythingPost'
            }
        }

        project.environments {
            anything1 {
                download {}
            }
            anything2 {
                download {}
            }
            anythingPost {
                download {}
            }
        }

        TestUtils.prepareModel(project)


        DelegatingTask install1task = project.tasks.installAnything1
        DelegatingTask install2task = project.tasks.installAnything2
        DelegatingTask installPosttask = project.tasks.installAnythingPost

        Assert.assertNotNull(install1task)
        Assert.assertNotNull(install2task)

        Assert.assertNotNull(installPosttask)


    }
}
