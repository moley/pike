package org.pike.resolver.components

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 24.04.13
 * Time: 21:54
 * To change this template use File | Settings | File Templates.
 */
class CurrentPathResolverTest {

    @Test
    public void resolveCurrentUser () {

        Project project = ProjectBuilder.builder().withName("resolveRecursively").build()
        CurrentPathResolver resolver = new CurrentPathResolver()
        String resolved = resolver.resolve(project, '${currentPath}HALLO')
        String filename = new File ("").absolutePath
        Assert.assertEquals ("Name not resolved", filename + "HALLO", resolved)

    }


}
