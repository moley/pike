package org.pike.env

import org.pike.model.operatingsystem.Operatingsystem

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 03.05.13
 * Time: 10:14
 * To change this template use File | Settings | File Templates.
 */
public interface IEnvEntry {

    /**
     * serialize model element into parameter #serializedValue
     * @param operatingsystem os to be used
     * @param serializedValue serialization
     * @param global true: if global environment, false: if not
     */
    public void serialize (Operatingsystem operatingsystem, Collection<String> serializedValue, final boolean global)

    /**
     * key of this entry to check if it is already configured
     * and has to be merged
     * @return key
     */
    public String getKey ()

}