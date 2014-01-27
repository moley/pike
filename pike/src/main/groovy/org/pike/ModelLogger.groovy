package org.pike

import groovy.util.logging.Log
import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle
import org.pike.common.NamedElement
import org.pike.model.environment.Environment
import org.pike.model.host.Host
import org.pike.model.operatingsystem.Operatingsystem

import java.util.logging.Level

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 17.04.13
 * Time: 00:37
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class ModelLogger {

    
    private static void log (final boolean asDebug, final String logmessage) {
        if (asDebug)
            log.debug(logmessage)
        else
            log.info(logmessage)
    }

    public static void logConfiguration (String context, Project project, boolean asDebug) {
        if (asDebug && ! log.debugEnabled)
            return

        log(asDebug, "___________________________________________________________________")
        log(asDebug,  "")
        log(asDebug,  "MODEL OF BUILDFILE $project.buildscript.sourceURI (context $context)")
        log(asDebug,  "")
        log(asDebug,  project.defaults.toString())

        //Operatingsystems
        if (project.operatingsystems.size() == 0)
            log(asDebug,  "NO OPERATINGSYSTEMS CONFIGURED")
        else {
            for (Operatingsystem nextOperatingsystem : project.operatingsystems)
                log(asDebug,  nextOperatingsystem.toString())
        }

        //Environments
        if (project.environments.isEmpty ())
            log(asDebug,  "NO ENVIRONMENTS CONFIGURED")
        else {
            for (Environment nextEnv: project.environments)
                log(asDebug,  nextEnv.toString())
        }

        if (project.hosts.isEmpty ())
            log(asDebug,  "NO HOSTS CONFIGURED")
        else {
            for (Host nextHost : project.hosts)
                log(asDebug,  nextHost.toString())
        }

        log(asDebug,  "")
        log(asDebug,  "___________________________________________________________________")
        log(asDebug,  "")
    }
}
