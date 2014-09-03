package org.pike.model.host

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.internal.reflect.Instantiator
import org.pike.BitEnvironment
import org.pike.model.operatingsystem.Operatingsystem

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
     * context of this host, can be used to distinguish hosts
     */
    String context

    /**
     * hostgroup which should be configured
     */
    String hostgroups

    /**
     * password that is used to configure via pike
     */
    String pikepassword

    /**
     * user that is used to configure via pike
     */
    String pikeuser

    /**
     * port that is used to configure via pike
     */
    String pikeport = '22'

    /**
     * ip of the host
     */
    String ip

    /**
     * true: 64 bit environment, false: 32 bit environment
     */
    BitEnvironment bitEnvironment = BitEnvironment._32

    /**
     * constructor
     * @param name  name of host != hostname
     */
    public Host (String name, Instantiator instantiator = null) {
        super (name, instantiator)
    }

    /**
     * getter app desc
     * @return describtion for repository
     */
    public getAppdesc () {
        return operatingsystem.provider.id + bitEnvironment.id
    }



    @Override
    public Set<String> getAllEnvironments(Project project) {
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
        objectAsString += "    * pike user               : $pikeuser $NEWLINE"
        objectAsString += "    * pike password           : $pikepassword $NEWLINE"
		objectAsString += "    * pike port               : $pikeport $NEWLINE"

        for (String nextEnv: environments) {
          objectAsString += "    * environment             : $nextEnv $NEWLINE"
        }

        return objectAsString + NEWLINE

    }



}
