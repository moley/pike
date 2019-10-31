package org.pike.configuration

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.PikePlugin


class WithGradlePropertiesEnricherTest {

    @Test(expected = IllegalStateException)
    public void enrichWithProjectPropertiesInvalidProperty () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.ext.'pike.invalidProperty' = 'specialEncoding2'
        PikeExtension pikeExtension = project.extensions.pike

        WithGradlePropertiesEnricher withGradlePropertiesEnricher = new WithGradlePropertiesEnricher()
        withGradlePropertiesEnricher.enrich(project, pikeExtension)
    }

    @Test
    public void enrichWithProjectPropertiesValidModule () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.ext.'pike.module1.encoding' = 'specialEncoding1'
        project.ext.'pike.encoding' = 'specialEncoding2'
        PikeExtension pikeExtension = project.extensions.pike
        pikeExtension.git {
            gitmodule ('module1', 'https://module1.com') {
                configuration {
                    encoding = 'defaultEncodingProject'
                }
            }
        }

        WithGradlePropertiesEnricher withGradlePropertiesEnricher = new WithGradlePropertiesEnricher()
        withGradlePropertiesEnricher.enrich(project, pikeExtension)
        Assert.assertEquals ("Invalid encoding", 'specialEncoding1', pikeExtension.git.findModuleByName('module1').configuration.encoding)
        Assert.assertEquals ("Invalid encoding", 'specialEncoding2', pikeExtension.configuration.encoding)
    }

    @Test(expected = IllegalStateException)
    public void enrichWithProjectPropertiesNoValidModule () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.ext.'pike.module1.encoding' = 'specialEncoding1'
        project.ext.'pike.encoding' = 'specialEncoding2'
        PikeExtension pikeExtension = project.extensions.pike
        pikeExtension.git {
            gitmodule ('invalidmodule', 'https://module1.com') {
                configuration {
                    encoding = 'defaultEncodingProject'
                }
            }


        }

        WithGradlePropertiesEnricher withGradlePropertiesEnricher = new WithGradlePropertiesEnricher()
        withGradlePropertiesEnricher.enrich(project, pikeExtension)
    }

    @Test(expected = IllegalStateException)
    public void enrichWithProjectPropertiesNoGit () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.ext.'pike.module1.encoding' = 'specialEncoding1'
        PikeExtension pikeExtension = project.extensions.pike

        WithGradlePropertiesEnricher withGradlePropertiesEnricher = new WithGradlePropertiesEnricher()
        withGradlePropertiesEnricher.enrich(project, pikeExtension)
    }
}
