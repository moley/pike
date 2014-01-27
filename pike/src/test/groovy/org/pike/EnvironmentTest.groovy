package org.pike

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.environment.EnvCollector

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 19.09.13
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
class EnvironmentTest {

    @Test
    public void collectEnvironmentsHostAndGroupBased () {
        Project project = ProjectBuilder.builder().withName("matrixTest").build()
        project.apply plugin: 'pike'

        project.defaults {
            currentHost = 'myHost'
        }

        project.hostgroups {
            hostgroup1 {
                environment 'envFromHostgroup1'
            }

            hostgroup2 {
                environment 'envFromHostgroup2'
            }

            hostgroup3 {
                environment 'envFromHostgroup3'
            }
        }

        project.hosts {
            host1 {
                hostname = 'myHost'
                hostgroups = 'hostgroup1, hostgroup2'
                environment 'envFromHost1'
            }
        }

        EnvCollector envCollector = new EnvCollector()
        Assert.assertTrue (envCollector.isEnvironmentActive(project, "envFromHost1")) // configured at host
        Assert.assertTrue (envCollector.isEnvironmentActive(project, "envFromHostgroup1")) // configured at hostgroup1
        Assert.assertTrue (envCollector.isEnvironmentActive(project, "envFromHostgroup2")) // configured at hostgroup2
        Assert.assertFalse (envCollector.isEnvironmentActive(project, "envFromHostgroup3")) // configured at hostgroup3
        Assert.assertFalse (envCollector.isEnvironmentActive(project, "unknownHostgroup")) // configured at hostgroup3
    }

    @Test
    public void collectEnvironmentsGroupBased () {
        Project project = ProjectBuilder.builder().withName("matrixTest").build()
        project.apply plugin: 'pike'

        project.defaults {
            currentHost = 'myHost'
        }

        project.hostgroups {
            hostgroup1 {
                environment 'envFromHostgroup1'
            }

            hostgroup2 {
                environment 'envFromHostgroup2'
            }

            hostgroup3 {
                environment 'envFromHostgroup3'
            }
        }

        project.hosts {
            host1 {
                hostname = 'myHost'
                hostgroups = 'hostgroup1, hostgroup2'
            }
        }

        EnvCollector envCollector = new EnvCollector()
        Assert.assertFalse (envCollector.isEnvironmentActive(project, "envFromHost1")) // configured at host
        Assert.assertTrue (envCollector.isEnvironmentActive(project, "envFromHostgroup1")) // configured at hostgroup1
        Assert.assertTrue (envCollector.isEnvironmentActive(project, "envFromHostgroup2")) // configured at hostgroup2
        Assert.assertFalse (envCollector.isEnvironmentActive(project, "envFromHostgroup3")) // configured at hostgroup3
        Assert.assertFalse (envCollector.isEnvironmentActive(project, "unknownHostgroup")) // configured at hostgroup3
    }

    @Test
    public void collectEnvironmentsHostBased () {
        Project project = ProjectBuilder.builder().withName("matrixTest").build()
        project.apply plugin: 'pike'

        project.defaults {
            currentHost = 'myHost'
        }

        project.hosts {
            host1 {
                hostname = 'myHost'
                environment 'envFromHost1'
            }
        }

        EnvCollector envCollector = new EnvCollector()
        Assert.assertTrue (envCollector.isEnvironmentActive(project, "envFromHost1")) // configured at host
        Assert.assertFalse (envCollector.isEnvironmentActive(project, "envFromHostgroup1")) // configured at hostgroup1
        Assert.assertFalse (envCollector.isEnvironmentActive(project, "envFromHostgroup2")) // configured at hostgroup2
        Assert.assertFalse (envCollector.isEnvironmentActive(project, "envFromHostgroup3")) // configured at hostgroup3
        Assert.assertFalse (envCollector.isEnvironmentActive(project, "unknownHostgroup")) // configured at hostgroup3
    }
}
