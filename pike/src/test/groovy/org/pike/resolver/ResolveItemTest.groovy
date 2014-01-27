package org.pike.resolver

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.pike.model.A

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 25.09.13
 * Time: 07:42
 * To change this template use File | Settings | File Templates.
 */
class ResolveItemTest {

    A a
    MetaMethod getterMethod
    MetaMethod setterMethod


    @Before
    public void before () {
        a = new A()
        getterMethod = a.metaClass.methods.find {it->it.name.equals("getVariable1")}
        setterMethod = a.metaClass.methods.find {it->it.name.equals("setVariable1")}
    }


    @Test
    public void notResolvableDueToNull () {

        ResolveItem item = new ResolveItem(a, getterMethod, setterMethod, null)
        item.isNotResolvable()
    }

    @Test(expected=IllegalStateException)
    public void notResolvableDueToNotYetResolved () {

        a.variable1 = "Hello"
        ResolveItem item = new ResolveItem(a, getterMethod, setterMethod, null)
        item.isNotResolvable()
    }

    @Test
    public void notResolvableDueToInvalidVariable () {

        Project project = ProjectBuilder.builder().withName("resolveRecursively").build()

        a.variable1 = 'Hallo ${invalidParamter}'
        ResolveItem item = new ResolveItem(a, getterMethod, setterMethod, project)
        item.resolve(project)
        Assert.assertTrue (item.isResolved)
        Assert.assertTrue (item.isNotResolvable())
    }
}
