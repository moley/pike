package org.pike.common

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.pike.model.defaults.Defaults
import org.pike.model.host.Host
import org.pike.os.OperatingsystemUtil

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

            final boolean matches = host.name.equalsIgnoreCase(currentHost)

            if (!matches && host.hostname != null)
              matches = host.hostname.equalsIgnoreCase(currentHost)

            if (matches)
                return true
        }

        //other check name or ip matches
        log.info("Checking if host $host.name matches real host")

        try {
          String determinedHostName = InetAddress.getLocalHost().getHostName().split("\\.") [0]
          boolean nameMatches = host.name.equalsIgnoreCase(determinedHostName) || (host.hostname != null && host.hostname.equalsIgnoreCase(determinedHostName))
          log.info("- Matching hostname: $host.name (expected) - $host.hostname - $determinedHostName")
          if (nameMatches)
              return true
        } catch (UnknownHostException e) {
          log.error("Hostname could not be determined")
        }

        try {
          for (InetAddress nextAdress: getAdresses()) {
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

    public static InetAddress [] getAdresses () {
        InetAddress localhost = InetAddress.getLocalHost()
        return InetAddress.getAllByName(localhost.getCanonicalHostName())
    }

    /**
     * if not hosts are configured gets the current host automatically created
     * if hosts are configured the host which fits the currents host hostname or ip is searched and returned
     * @return found host or automatically defaulted host
     */
    public static Host getCurrentHost (Project project) {

            //simple case, no hosts defined, create a host object with current hosts data
            if (project.hosts.isEmpty()) {
                Host currentHost = new Host('localhost', project.services.get(Instantiator.class))
                currentHost.ip = getAdresses() [0].hostAddress
                currentHost.hostname = getAdresses() [0].hostName
                currentHost.operatingsystem = new OperatingsystemUtil().findOperatingsystem(project)
                return currentHost
            }

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

            log.warn("Current host not found in configuration (Valid hosts: $hostnames)")

    }
}
