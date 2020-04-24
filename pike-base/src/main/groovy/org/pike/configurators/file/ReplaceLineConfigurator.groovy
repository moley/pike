package org.pike.configurators.file

import org.gradle.api.logging.Logger

import java.nio.file.Files

class ReplaceLineConfigurator implements FileConfigurator{
    @Override
    void configure(Logger logger, File configFile, String key, String value, boolean dryRun) {
        if (!configFile.exists())
            throw new IllegalStateException("Configuration file " + configFile.absolutePath + " does not exist")


        List<String> content = configFile.readLines()
        List<String> writeContent = new ArrayList<String>()

        boolean replacements = false
        for (String next: content) {
            if (next.contains(key)) {
                replacements = true
                writeContent.add(key + value)
            }
            else
                writeContent.add(next)
        }

        if (! replacements)
            throw new IllegalStateException("There are no occurrences of " + key + " in file " + configFile.absolutePath)

        if (! dryRun) {
            configFile.parentFile.mkdirs()
            if (logger)
                logger.lifecycle("Replace " + key + " with " + value + " in file " + configFile.absolutePath)

            configFile.withWriter{ out ->
                writeContent.each {out.println it}
            }
        }

    }
}
