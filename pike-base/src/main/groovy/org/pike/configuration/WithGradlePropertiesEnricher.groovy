package org.pike.configuration

import org.gradle.api.Project


/**
 * configures gradle properties into pike extension
 */
class WithGradlePropertiesEnricher {

    public void enrich (Project project, PikeExtension pikeExtension) {
        Properties pikeProperties = filterPikeProperties(project.properties)

        for (String nextKey: pikeProperties.keySet()) {
            String nextValue = pikeProperties.get(nextKey)

            Configuration configuration = pikeExtension.configuration
            if (configuration == null) {
                configuration = new Configuration()
                pikeExtension.configuration = configuration
            }

            String [] tokens = nextKey.split('\\.')
            String property = null
            if (tokens.length == 3) {

                Git git = pikeExtension.git
                if (git == null)
                    throw new IllegalStateException("Module specific property found (" + nextKey + ") but no subclosure git defined. Remove this property or add git modules to your configuration")

                String modulename = tokens[1]
                property = tokens[2]
                Module foundModule = git.findModuleByName(modulename)
                if (foundModule == null)
                    throw new IllegalStateException("Error evaluating property " + nextKey + ": Module " + modulename + " not found")
                else
                    configuration = foundModule.configuration
            }
            else {
                property = tokens[1]
            }

            try {
                configuration.setProperty(property, nextValue)
            } catch (MissingPropertyException e) {
                throw new IllegalStateException("Property " + nextKey + " did not match a valid extension property")
            }

            println "Property : " + nextKey
            println "Set to   : " + nextValue


        }

    }

    Properties filterPikeProperties (final HashMap<String, ?> properties) {
        Properties filteredProperties = new Properties()
        for (String nextKey: properties.keySet()) {
            if (nextKey.startsWith('pike.')) {
                String nextValue = properties.get(nextKey)
                filteredProperties.put(nextKey, nextValue)
            }
        }

        return filteredProperties

    }
}
