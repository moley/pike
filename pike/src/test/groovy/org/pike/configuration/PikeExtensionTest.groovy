package org.pike.configuration

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.PikePlugin

public class PikeExtensionTest {

    @Test
    void mergeConfigurationBoth () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            configuration {
                encoding 'encodingGlobal'
                basepath 'basepathGlobal'
            }
        }

        Configuration specificConfiguration = new Configuration()
        specificConfiguration.basepath = "basepathCustom"
        specificConfiguration.encoding = "encodingCustom"


        PikeExtension pikeExtension = project.extensions.pike
        Configuration mergedConfiguration = pikeExtension.getMergedConfiguration(specificConfiguration)
        Assert.assertEquals ("Basepath incorrect", specificConfiguration.basepath, mergedConfiguration.basepath)
        Assert.assertEquals ("Encoding incorrect", specificConfiguration.encoding, mergedConfiguration.encoding)
    }

    @Test
    void mergeConfigurationOnlySpecific () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)

        Configuration specificConfiguration = new Configuration()
        specificConfiguration.basepath = "basepathCustom"
        specificConfiguration.encoding = "encodingCustom"

        PikeExtension pikeExtension = project.extensions.pike
        Configuration mergedConfiguration = pikeExtension.getMergedConfiguration(specificConfiguration)
        Assert.assertEquals ("Basepath incorrect", specificConfiguration.basepath, mergedConfiguration.basepath)
        Assert.assertEquals ("Encoding incorrect", specificConfiguration.encoding, mergedConfiguration.encoding)
    }

    @Test
    void mergeConfigurationOnlyGlobal () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            configuration {
                encoding 'encodingGlobal'
                basepath 'basepathGlobal'
            }
        }

        PikeExtension pikeExtension = project.extensions.pike
        Configuration mergedConfiguration = pikeExtension.getMergedConfiguration(new Configuration())
        Assert.assertEquals ("Basepath incorrect", project.pike.configuration.basepath, mergedConfiguration.basepath)
        Assert.assertEquals ("Encoding incorrect", project.pike.configuration.encoding, mergedConfiguration.encoding)
    }
}
