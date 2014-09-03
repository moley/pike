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

    Set<String> disableMessage = new HashSet<String>()

    public boolean isEnvironmentActive (final Project project, final String name) {
        if (log.debugEnabled)
          log.debug("Check if environment " + name + " is active...")

        if (project.hosts.isEmpty()) {
            log.info("Environment $name is active because no hosts are defined")
            return true
        }


        NamedDomainObjectContainer<Host> hosts = project.extensions.hosts
        Defaults defaults = project.extensions.defaults
        String availableHosts = ""

        for (Host nextHost: hosts) {
            availableHosts+= " " + nextHost.name

            if (log.debugEnabled)
              log.debug("check host " + nextHost.name + ", currenthost = " + defaults.currentHost)

            if (ProjectInfo.isCurrentHost(project, nextHost))
                return nextHost.isEnvironmentActive(project, name)
        }

        if (! disableMessage.contains(name)) {
            if (defaults.currentHost == null)
              log.info("Current host is " + defaults.currentHost + ", so we disable environment " + name)
            else
              log.info("Current host " + defaults.currentHost + " not found in configuration (available hosts " + availableHosts.trim() + "), so we disable environment " + name)
        }
        disableMessage.add(name)

        return false
    }
}
