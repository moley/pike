package org.pike.model.host

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.ExtensionContainer
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.remoting.IRemoting
import org.pike.remoting.SshRemoting

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 15.04.13
 * Time: 22:08
 * To change this template use File | Settings | File Templates.
 */
class Host extends EnvironmentHolder {

    /**
     * reference to project
     */
    ProjectInternal project

    /**
     * reference to the operatingsystem of this host
     */
    Operatingsystem operatingsystem

    /**
     * name of the host
     */
    String hostname

    /**
     * context
     */
    String context

    /**
     * hostgroup which should be configured
     */
    String hostgroups

    /**
     * rootpasswd
     */
    String pikepassword

    String pikeuser

    String pikeport = '22'

    String ip

    /**
     * if current host is a master host, its pike instance
     * tries to connect with the clients. If build is triggered on
     * masterserver it is forwarded to all connected servers
     */
    boolean masterHost


    /**
     * constructor
     * @param name  name of host != hostname
     */
    public Host (String name) {
        super (name)
    }

    @Override
    protected Set<String> getAllEnvironments(Project project) {
        Set<String> allEnvironments = super.getAllEnvironments(project)

        //add environments of groups
        if (hostgroups != null) {

          Collection <String> assignedHostgroups = hostgroups.split(",")
          Collection <String> trimAssignedHostgroups = new HashSet<String> ()
          for (String next: assignedHostgroups)
              trimAssignedHostgroups.add(next.trim())

          NamedDomainObjectContainer<HostGroup> hostgroups = project.extensions.hostgroups
          hostgroups.all { HostGroup group ->

            println ("Check if " + trimAssignedHostgroups + " contains " + group.name)
            if (trimAssignedHostgroups.contains(group.name)) {
                allEnvironments.addAll(group.getAllEnvironments())
            }
          }
        }

        return allEnvironments
    }


    /**
     * returns if current host is part of a hostgroup
     * @return true: if it is part of a hostgroup or no hostgroup is parameterized, false: if not
     */
    public boolean isPartOfHostGroup (final String hostgroup) {
        if (hostgroup == null)
            return true
        return assignedHostGroups.contains(hostgroup)
    }

    /**
     * returns list of all assigned hostgroups
     * @return hostgroups
     */
    public Collection<String> getAssignedHostGroups () {
        if (hostgroups == null)
            return Collections.emptyList()
        else {
          List<String> list =  hostgroups.split(",").toList()
          return list*.trim()
        }
    }

	public int getPikePortInt () {
		return new Integer (pikeport).intValue()
	}

    /**
     * {@inheritDoc}
     */
    public String toString () {
        String objectAsString = "Host <" + name + ">$NEWLINE"
        if (operatingsystem != null)
          objectAsString += "    * operatingsystem         : $operatingsystem.name $NEWLINE"
        else
          objectAsString += "    * operatingsystem         : null $NEWLINE"
        objectAsString += "    * hostname                : $hostname $NEWLINE"
        objectAsString += "    * ip                      : $ip $NEWLINE"
        objectAsString += "    * hostgroup               : $hostgroups $NEWLINE"
        objectAsString += "    * context                 : $context $NEWLINE"
        objectAsString += "    * masterhost              : $masterHost $NEWLINE"
        objectAsString += "    * pike user               : $pikeuser $NEWLINE"
        objectAsString += "    * pike password           : $pikepassword $NEWLINE"
		objectAsString += "    * pike port               : $pikeport $NEWLINE"

        for (String nextEnv: environments) {
          objectAsString += "    * environment             : $nextEnv $NEWLINE"
        }

        return objectAsString + NEWLINE

    }



}
