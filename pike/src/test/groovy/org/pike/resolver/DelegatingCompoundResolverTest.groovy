package org.pike.resolver

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.test.TestUtils
import org.pike.model.A
import org.pike.model.B
import org.pike.model.C

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 19.04.13
 * Time: 00:49
 * To change this template use File | Settings | File Templates.
 */
class DelegatingCompoundResolverTest {



    @Test
    public void resolveTheRealLife () {
        String currentUser = System.getProperty("user.name")
        Project project = ProjectBuilder.builder().withName("autocreateTasksTest").build()
        project.apply plugin: 'pike'

        project.defaults {
            defaultuser = 'nightly'
            currentHost = 'testhost'
        }
        project.operatingsystems {
            linux {
                homedir = '/home/${user}'
                programdir = "${homedir}/swarm/tools"
            }
        }

        project.hosts {
            testhost {
                hostname = 'testhost.intra.domain'
                operatingsystem = project.operatingsystems.linux
                environment 'buildnode'
                //environment 'buildnode2'
            }
        }

        project.environments {
            gradle {
                download {
                    from "http://somwhere.zip"
                    to "${operatingsystem.programdir}"
                }
            }

            buildnode {
                download {
                    from "http://somwhere2.zip"
                    to "${operatingsystem.programdir}"
                }
            }
        }

        TestUtils.prepareModel(project)

        //Check if everythings was resolved
        Assert.assertEquals ("Homedir was not resolved", "/home/" + currentUser, project.operatingsystems.linux.homedir)
    }

    @Test
    public void resolveRecursively() {

        Project project = ProjectBuilder.builder().withName("resolveRecursively").build()

        A a = new A ()
        a.variable1 = '${user}Foo'
        a.variable2 = '${user}OnceMore'
        a.variable3 = null
        B b = new B ()
        a.nullreferenceToB = null
        a.referenceToB = b
        b.propertymore = '${user}AndB'

        C c1 = new C ()
        c1.name = '${user}c1'

        C c2 = new C ()
        c2.name = 'c2${user}'
        b.cs.addAll(c1, c2)

        List<ResolveItem> items = new ArrayList<ResolveItem>()
        HashSet<Object> collectedObjects = new HashSet<Object>()
        DelegatingCompoundResolver resolver = new DelegatingCompoundResolver()
        resolver.collectResolveItems(collectedObjects, items, project, a)
        resolver.resolveAll(items)

        String user = System.getProperty("user.name")

        Assert.assertEquals (user + "Foo", a.variable1)
        Assert.assertEquals (user + "OnceMore", a.variable2)
        Assert.assertEquals (user + "AndB", b.propertymore)
        Assert.assertEquals (user + "c1", c1.name)
        Assert.assertEquals ("c2" + user, c2.name)

    }

    @Test(expected = ResolveException)
    public void resolveRecursivelyItemNotResolvable() {

        Project project = ProjectBuilder.builder().withName("resolveRecursively").build()

        A a = new A ()
        a.variable1 = '${user}Foo'
        a.variable2 = '${wrongUser}OnceMore'
        a.variable3 = null

        List<ResolveItem> items = new ArrayList<ResolveItem>()
        HashSet<Object> collectedObjects = new HashSet<Object>()
        DelegatingCompoundResolver resolver = new DelegatingCompoundResolver()
        resolver.collectResolveItems(collectedObjects, items, project, a)
        resolver.resolveAll(items)

    }



}
