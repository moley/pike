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
     * name of environments bound to this host before any other environments,
     * for example if you want to shutdown a service
     * only called if --pre is used
     */
    protected Set<String> preEnvironments = new HashSet<String> ()

    /**
     * name of environments bound to this host after any other environments
     * for example if you want to restart a service after reconfiguring,
     * only called if --post is used
     */
    protected Set<String> postEnvironments = new HashSet<String>()

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
     * adds an pre environment to the current host instance by name
     * @param preEnv  preEnv name
     */
    public void pre (final String preEnv) {
        preEnvironments.add(preEnv)
    }

    /**
     * adds a post environment to the current host instance by name
     * @param postEnv  postEnv name
     */
    public void post (final String postEnv) {
        postEnvironments.add(postEnv)
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
