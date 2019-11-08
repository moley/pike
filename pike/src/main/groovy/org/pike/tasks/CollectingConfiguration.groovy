package org.pike.tasks

import org.gradle.api.logging.Logger
import org.pike.configuration.Configuration
import org.pike.configurators.file.FileConfigurator


abstract class CollectingConfiguration {

    protected HashMap<String, String> alreadyConfigured = new HashMap<String, String>()

    File workspaceConfigPath
    File globalConfigPath
    Collection<File> projectConfigPaths

    Logger logger

    HashMap<Class<? extends FileConfigurator>, FileConfigurator> configuratorHashMap = new HashMap<Class<? extends FileConfigurator>, FileConfigurator>()


    public FileConfigurator getFileConfigurator (final Class<? extends FileConfigurator> clazz) {
        FileConfigurator fileConfigurator = configuratorHashMap.get(clazz)
        if (fileConfigurator == null) {
            fileConfigurator = clazz.getDeclaredConstructor().newInstance()
            configuratorHashMap.put(clazz, fileConfigurator)
        }

        return fileConfigurator
    }

    public void collectConfiguration (final String context, final String relativeFile, final String key, final Object value) {
        String completeKey = context + " -> " + relativeFile + " -> " + key
        if (alreadyConfigured.get(completeKey) == null) {
            alreadyConfigured.put(completeKey, value.toString())
        }
        else {
            String savedKey = alreadyConfigured.get(completeKey)
            String savedValue = savedKey.split("#").last()
            if (! savedValue.equals(value.toString()))
              throw new IllegalStateException("Overlapping configuration found in " + completeKey + " (Value cannot be " + value.toString() + " and " + savedValue + " at once)")
        }
    }



    void apply(Configuration configuration, final boolean dryRun) {
        if (logger) {
            logger.info ("Apply configurations (" + dryRun + ")")
            logger.info ("   - Global configuration path         : " + globalConfigPath)
            logger.info ("   - Workspace configuration path      : " + workspaceConfigPath)
            logger.info ("   - Project configuration paths       : " + projectConfigPaths.toString().replace(", ", "\n"))
        }
    }

    public void check (final Collection<Configuration> configurations) {
        for (Configuration next: configurations) {
            apply(next, true)
        }
    }

}
