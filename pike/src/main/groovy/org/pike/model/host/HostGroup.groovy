package org.pike.model.host

import org.gradle.internal.reflect.Instantiator

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 19.09.13
 * Time: 14:29
 * To change this template use File | Settings | File Templates.
 */
class HostGroup extends EnvironmentHolder{

    /**
     * constructor
     * @param name name of host != hostname
     */
    public HostGroup(String name, Instantiator instantiator = null) {
        super(name, instantiator)
    }

}
