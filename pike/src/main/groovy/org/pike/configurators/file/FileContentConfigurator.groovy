package org.pike.configurators.file

import org.gradle.api.logging.Logger

class FileContentConfigurator implements FileConfigurator {
    @Override
    void configure(Logger logger, File configFile, String key, String value, boolean dryRun) {
        if (key != null)
            throw new IllegalArgumentException("Parameter 'key' must be null")

        if (value == null)
            throw new IllegalArgumentException("Parameter 'value' must be not null")


        if (dryRun)
            return

        logger.lifecycle("Write content of length " + value.size() + " to file " + configFile.absolutePath)
        FileWriter fileWriter = new FileWriter(configFile)
        try {
            fileWriter.write(value)
        } finally {
            if (fileWriter)
            fileWriter.close()
        }


    }
}
