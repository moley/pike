package org.pike.os

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.pike.model.operatingsystem.Operatingsystem

/**
 * utils for operatingsystem
 */
@Slf4j
class OperatingsystemUtil {

    public Operatingsystem findOperatingsystem (final Project project) {
        String os = System.getProperty('os.name').toLowerCase()
        log.info("Check operatingsystem $os")

        for (Operatingsystem next: project.operatingsystems) {
            if (next.provider.isActive())
                return next
        }

        throw new IllegalStateException("Current operating system $os could not be determined definitely")
    }
}
