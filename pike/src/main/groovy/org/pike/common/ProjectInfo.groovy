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
            log.info ("Checking currenthost from defaults " + currentHost)
            return host.name.equalsIgnoreCase(currentHost) || host.hostname.equalsIgnoreCase(currentHost)
        }

        //other check name or ip matches
        String determinedHostName
        String ipAdress

        try {
          determinedHostName = InetAddress.getLocalHost().getHostName().split("\\.") [0]
        } catch (UnknownHostException e) {
          log.error("Hostname could not be determined")
        }

        try {
          ipAdress = InetAddress.getLocalHost().getHostAddress()
        } catch (UnknownHostException e) {
            log.error("IP could not be determined")
        }

        boolean nameMatches = host.name.equalsIgnoreCase(determinedHostName) || (host.hostname != null && host.hostname.equalsIgnoreCase(determinedHostName))
        boolean ipMatches = host.ip != null && host.ip.equalsIgnoreCase(ipAdress)

        if (log.isDebugEnabled())
          log.debug("Checking currenthost <" + determinedHostName + "><" + ipAdress +
                ">, expected <" + host.name +"><" + host.hostname + "><" + host.ip)

        if (nameMatches || ipMatches)
          log.info("Host " + host.name + " matches current host - (name $nameMatches - ipMatches $ipMatches)")

        return nameMatches || ipMatches
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
