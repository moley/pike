package org.pike.resolver.components

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 24.04.13
 * Time: 21:42
 * To change this template use File | Settings | File Templates.
 */
class CurrentUserResolverTest {

    @Test
    public void resolveCurrentUser () {

        Project project = ProjectBuilder.builder().withName("resolveRecursively").build()
        CurrentUserResolver resolver = new CurrentUserResolver()
        String resolved = resolver.resolve(project, '${user}HALLO')
        String user = System.getProperty("user.name")
        Assert.assertEquals ("Name not resolved", user + "HALLO", resolved)

    }
}
