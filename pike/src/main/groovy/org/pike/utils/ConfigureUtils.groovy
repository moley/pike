package org.pike.utils

import org.gradle.api.Project
import org.pike.configuration.Configuration
import org.pike.configuration.PikeExtension
import org.pike.exceptions.MissingConfigurationException

class ConfigureUtils {

    public File getBasePath (final Project project, final Configuration configuration ) {
        PikeExtension pikeExtension = project.extensions.findByName(PikeExtension.NAME)
        Configuration mergedConfiguration = pikeExtension.getMergedConfiguration(configuration)
        if (mergedConfiguration == null)
            throw new MissingConfigurationException("No configuration dsl provided. Add a closure pike.configuration() to define a basepath")

        if (mergedConfiguration.basepath == null)
            return project.projectDir
        else
          return project.file(mergedConfiguration.basepath)
    }
}
