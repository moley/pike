package org.pike.remoting

import groovy.util.logging.Slf4j
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.internal.tasks.options.Option
import org.pike.model.environment.Environment
import org.pike.model.host.Host
import org.pike.tasks.PikeTask

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 08.10.13
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class RemoteTask extends PikeTask {

    private String host


    @Option(option='host', description='host to be configured')
    public void setHost(String host) {
        this.host = host
        log.info("set host to " + host)
    }

    protected Environment findEnvByName(final Project project, final String name) {

        String validEnvs = ""
        NamedDomainObjectContainer<Environment> envs = project.extensions.environments
        for (Environment nextEnv: envs) {
            if (nextEnv.name.equals(name))
                return nextEnv
            else
                validEnvs += " " + nextEnv.name
        }

        throw new IllegalStateException("Environment " + name + " not found (valid environments: " + validEnvs.trim() + ")")

    }

    private Host findHostByName(final Project project, final String name) {
        NamedDomainObjectContainer<Host> hosts = project.extensions.hosts
        for (Host nextHost: hosts) {
            if (nextHost.name.equals(name))
                return nextHost
        }
        return null
    }

    private Collection<Host> findHostsByGroup(final Project project, final String name) {
        Collection<Host> hostsByGroup = new ArrayList<Host>()
        NamedDomainObjectContainer<Host> hosts = project.extensions.hosts
        for (Host nextHost: hosts) {
            if (nextHost.assignedHostGroups.contains(name))
                hostsByGroup.add(nextHost)
        }
        return hostsByGroup
    }


    /**
     *
     * get list of hosts from the hostparam
     * @param project  project
     */
    public Collection<Host> getHostsToBuild () {

        Collection<Host> allHostsToBeBuild = new ArrayList<Host>()

        if (host != null && ! host.trim().isEmpty()) {

          String [] hosts = host.split(",")

          for (String nextTaskName: hosts) {

            Host newHost = findHostByName(project, nextTaskName)
            Collection<Host> newHostsByGroup = findHostsByGroup(project, nextTaskName)

            if (newHost != null && ! newHostsByGroup.isEmpty())
                throw new IllegalStateException("Your model contains both hosts with a name and a group named " + nextTaskName + ", which is not allowed")

            if (newHost != null) {
                println ("Found host " + newHost.name + " to be built")
                allHostsToBeBuild.add(newHost)
            }

            if (! newHostsByGroup.isEmpty()) {
                println ("Found hostgroup " + newHostsByGroup.name + " to be built")
                allHostsToBeBuild.addAll(newHostsByGroup)
            }

          }
        }

        if (allHostsToBeBuild.isEmpty()) {

            if (project.hosts.size() == 1)
                return project.hosts

            Set<String> validValues = new HashSet<String>()
            NamedDomainObjectContainer<Host> hosts = project.extensions.hosts
            for (Host nextHost: hosts) {
                validValues.add("  -" + nextHost.name + "(host)")
                for (String next: nextHost.assignedHostGroups) {
                    validValues.add("  -" + next + " (group)")
                }
            }
            String validValuesText = ""
            for (String nextValidValue : validValues.sort())
                validValuesText += nextValidValue + "\n"
            throw new IllegalStateException("You have to parameterize a hostname or a group to call the action for\n " +
                                            "Valid are: \n" + validValuesText + "\n" +
                                            "Example call: gradle configureRemote --host=HOSTNAME")
        }

        return allHostsToBeBuild
    }

}
