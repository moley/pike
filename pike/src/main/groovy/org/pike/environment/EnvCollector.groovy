package org.pike.environment

import groovy.util.logging.Slf4j
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.pike.common.ProjectInfo
import org.pike.model.defaults.Defaults
import org.pike.model.host.Host

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 19.09.13
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class EnvCollector {

    public boolean isEnvironmentActive (final Project project, final String name) {
        log.debug("Check if environment " + name + " is active...")
        NamedDomainObjectContainer<Host> hosts = project.extensions.hosts
        Defaults defaults = project.extensions.defaults
        String availableHosts = ""

        for (Host nextHost: hosts) {
            availableHosts+= " " + nextHost.name

            log.debug("check host " + nextHost.name + ", currenthost = " + defaults.currentHost)
            if (ProjectInfo.isCurrentHost(project, nextHost))
                return nextHost.isEnvironmentActive(project, name)
        }

        log.info("Current host " + defaults.currentHost + " not found in configuration (available hosts " + availableHosts.trim() + "), so we deactivate environment "+ name)
        return false
    }
}
