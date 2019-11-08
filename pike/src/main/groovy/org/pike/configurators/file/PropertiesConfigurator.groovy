package org.pike.configurators.file

import org.gradle.api.logging.Logger


class PropertiesConfigurator implements FileConfigurator{
    @Override
    void configure(Logger logger, File configFile, String key, String value, boolean dryRun) {

        Properties properties = new Properties()

        if (configFile.exists())
            properties.load(new FileInputStream(configFile))

        properties.setProperty(key, value.toString())
        if (logger)
            logger.lifecycle("Set " + key + " = " + value + " (" + configFile.absolutePath + ")")


        if (! dryRun) {
            configFile.parentFile.mkdirs()
            properties.store(new FileOutputStream(configFile), 'Saved by pike at ' + new Date())
        }

    }
}
