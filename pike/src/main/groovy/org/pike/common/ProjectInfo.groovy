package org.pike.common

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.pike.model.defaults.Defaults
import org.pike.model.host.Host

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 18.04.13
 * Time: 00:06
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class ProjectInfo {


    /**
     * getter returns all groups defined in all hosts, to be logged if a wrong
     * group is parameterized
     * @param project  project
     * @return list of groups as string
     */
    public Collection<String> getAllGroups (final Project project) {
        Set<String> groups = new HashSet<String>()
        for (Host nextHost: project.hosts) {
            groups.addAll(nextHost.assignedHostGroups)
        }

        return groups
    }

    public static boolean isCurrentHost (Project project, Host host) {

        Defaults defaults = project.extensions.defaults
        String currentHost = defaults.currentHost

        //if default ist configured than use this
        if (currentHost != null) {
            log.info ("Checking if host $host.name matches current host from defaults $currentHost")
            final boolean matches = host.name.equalsIgnoreCase(currentHost) || host.hostname.equalsIgnoreCase(currentHost)
            if (matches)
                return true
        }

        //other check name or ip matches
        String determinedHostName
        String ipAdress

        log.info("Checking if host $host.name matches real host")

        try {
          determinedHostName = InetAddress.getLocalHost().getHostName().split("\\.") [0]
          boolean nameMatches = host.name.equalsIgnoreCase(determinedHostName) || (host.hostname != null && host.hostname.equalsIgnoreCase(determinedHostName))
          log.info("- Matching hostname: $host.name (expected) - $host.hostname - $determinedHostName")
          if (nameMatches)
              return true
        } catch (UnknownHostException e) {
          log.error("Hostname could not be determined")
        }

        try {
          InetAddress localhost = InetAddress.getLocalHost()
          InetAddress[] allMyIps = InetAddress.getAllByName(localhost.getCanonicalHostName())
            for (InetAddress nextAdress: allMyIps) {
                log.info("- Matching ip: $host.ip (expected) - $nextAdress.hostAddress")
                boolean ipMatches = host.ip != null && host.ip.equalsIgnoreCase(nextAdress.hostAddress)
                if (ipMatches)
                    return true
            }
        } catch (UnknownHostException e) {
            log.error("IP could not be determined")
        }

        log.info("- Host does not match")
        return false

    }

    /**
     * gets the current host to work
     * @return
     */
    public static Host getCurrentHost (Project project) {

            for (Host nextHost : project.hosts) {
                if (ProjectInfo.isCurrentHost(project, nextHost))
                    return nextHost
            }

            String hostnames = ""
            for (Host nextHost : project.hosts) {
               if (! hostnames.isEmpty())
                    hostnames += ","
                 hostnames += nextHost.name
            }

            throw new IllegalStateException ("Current host not found in configuration and no configuration in dsl (Valid hosts: $hostnames)")

    }
}
