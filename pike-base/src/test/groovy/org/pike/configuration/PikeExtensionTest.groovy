package org.pike.configuration

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.pike.PikePlugin

public class PikeExtensionTest {


    @Test(expected = IllegalStateException)
    public void overlappingConfigurations () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            eclipse {}
            idea {}
            git {
                gitmodule ('gradle-java-apidoc-plugin', 'https://github.com/moley/gradle-java-apidoc-plugin') {
                    configuration {
                        encoding 'ISO-8859-15'
                    }

                }
                gitmodule ('leguan', 'https://github.com/moley/leguan') {
                    configuration {
                        encoding 'UTF-8'
                    }
                }
            }

        }

        PikeExtension pikeExtension = project.extensions.pike
        pikeExtension.checkOverlappingConfigurations()



    }

    @Test
    public void overlappingConfigurationsNoIde () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            git {
                gitmodule ('gradle-java-apidoc-plugin', 'https://github.com/moley/gradle-java-apidoc-plugin') {
                    configuration {
                        encoding 'ISO-8859-15'
                    }

                }
                gitmodule ('leguan', 'https://github.com/moley/leguan') {
                    configuration {
                        encoding 'UTF-8'
                    }
                }
            }

        }

        PikeExtension pikeExtension = project.extensions.pike
        pikeExtension.checkOverlappingConfigurations()



    }

    @Test
    public void overlappingConfigurationsNoGit () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)

        PikeExtension pikeExtension = project.extensions.pike
        pikeExtension.checkOverlappingConfigurations()
    }

    @Test
    public void formatter () {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            configuration {
                encoding 'encodingGlobal'
                basepath 'basepathGlobal'
                formatter {
                    name 'MyGlobalOne'
                    spacesForTabs true
                    tabWidth 8
                }
            }
        }

        PikeExtension pikeExtension = project.extensions.pike
        Assert.assertNotNull ("Formatter not found", pikeExtension.configuration.formatter)
    }

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
