package org.pike.env

import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.IOperatingsystemProvider

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 09.09.13
 * Time: 11:23
 * To change this template use File | Settings | File Templates.
 */
class DefaultPathEntry extends UserEnvEntry{

    private String addpath

    public DefaultPathEntry (final String addpath) {
        this.addpath = addpath
    }



    @Override
    void serialize (Operatingsystem operatingsystem, Collection<String> serialized, final boolean global) {

        IOperatingsystemProvider osProvider = operatingsystem.provider

        String key = "PATH"
        String value = addpath
        String addon = osProvider.getAsVariable("PATH")
        String command = buildCommand(true, operatingsystem, key, value, addon)

        if (global)
            osProvider.executeGlobalConf(key, value, addon)

        if (addpath != null)
            serialized.add(command)
    }

    @Override
    String getPikeKey() {
        return "DEFAULTPATH " +  addpath
    }

    @Override
    boolean isOriginEntry(Operatingsystem operatingsystem, String originEntry) {
        IOperatingsystemProvider osProvider = operatingsystem.provider
        return originEntry.contains('PATH') && originEntry.contains(osProvider.getOsDependendPath(addpath))
    }
}
