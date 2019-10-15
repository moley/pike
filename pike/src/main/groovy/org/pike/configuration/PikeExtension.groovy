package org.pike.configuration

import org.gradle.api.Project


class PikeExtension extends Configurable {

    Git git

    boolean force


    public PikeExtension (final Project project) {
        this.project = project
    }

    void git (Closure closure) {
        git = new Git()
        git.pikeExtension = this
        project.configure(git, closure)
    }

    Configuration getMergedConfiguration (Configuration specificConfiguration) {
        Configuration globalConfiguration = configuration

        Configuration mergedConfiguration = new Configuration()
        mergedConfiguration.properties.each { prop, val ->
            if(prop in ["metaClass","class"]) return

            if (globalConfiguration != null) {
                Object valueFromGlobalConfiguration = globalConfiguration.getProperty(prop)
                if (valueFromGlobalConfiguration != null)
                    mergedConfiguration.setProperty(prop, valueFromGlobalConfiguration)
            }

            if (specificConfiguration != null) {
                Object valueFromSpecificConfiguration = specificConfiguration.getProperty(prop)
                if (valueFromSpecificConfiguration != null)
                    mergedConfiguration.setProperty(prop, valueFromSpecificConfiguration)
            }
        }

        return mergedConfiguration



    }


}
