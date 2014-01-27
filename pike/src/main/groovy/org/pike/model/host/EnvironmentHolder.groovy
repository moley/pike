package org.pike.model.host

import org.gradle.api.Project
import org.pike.common.NamedElement

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 19.09.13
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
abstract class EnvironmentHolder extends NamedElement{

    /**
     * name of environments bound to this host
     */
    protected Set<String> environments = new HashSet<String>()

    /**
     * constructor
     * @param name  name of host != hostname
     */
    public EnvironmentHolder (String name) {
        super (name)
    }

    /**
     * add an environment to the current host instance by name
     * @param environment environment name
     */
    public void environment (final String environment) {
        environments.add(environment)
    }

    /**
     * getter
     * @return returns set of all environments that belong to the environment holder
     */
    protected Set<String> getAllEnvironments(final Project project) {
        return environments
    }

    /**
     * checks if environment is active
     * @param envname  environment to check
     * @return true: active, false: not active
     */
    public final isEnvironmentActive (final Project project, final String envname) {
        return getAllEnvironments(project).contains(envname)
    }
}
