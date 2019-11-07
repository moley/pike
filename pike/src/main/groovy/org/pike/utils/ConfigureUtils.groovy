package org.pike.utils

import org.gradle.api.Project
import org.pike.configuration.Configuration
import org.pike.configuration.PikeExtension

class ConfigureUtils {

    public File getBasePath (final Project project, final Configuration configuration ) {
        PikeExtension pikeExtension = project.extensions.findByName(PikeExtension.NAME)
        Configuration mergedConfiguration = pikeExtension.getMergedConfiguration(configuration)
        if (mergedConfiguration.basepath == null)
            return project.projectDir
        else
          return project.file(mergedConfiguration.basepath)
    }
}