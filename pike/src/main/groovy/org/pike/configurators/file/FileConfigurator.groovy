package org.pike.configurators.file

import org.gradle.api.logging.Logger


interface FileConfigurator {

    void configure (final Logger logger, final File configFile, final String key, final String value, boolean dryRun)

}
