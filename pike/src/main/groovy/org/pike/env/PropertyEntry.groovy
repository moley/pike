package org.pike.env

import org.pike.model.operatingsystem.Operatingsystem

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 14.05.13
 * Time: 08:44
 * To change this template use File | Settings | File Templates.
 */
class PropertyEntry extends UserEnvEntry {

    private String propertyname

    private String propertyvalue

    private boolean exported
    private String divider = "="

    public PropertyEntry (final String pathname, final String pathvalue, final boolean exported, final String divider) {
        this.propertyname = pathname
        this.propertyvalue = pathvalue
        this.exported = exported
        if (divider)
            this.divider = divider
    }

    @Override
    public void serialize(Operatingsystem operatingsystem, Collection<String> serialized, final boolean global) {

        if (propertyvalue == null)
            throw new IllegalStateException("You cannot set a variable " + propertyname + " to null")

        if (global && divider != "=")
            throw new IllegalStateException("A global configuration in windows can only be divided with = and not with " + divider)

        String command = buildCommand(exported, operatingsystem, propertyname, propertyvalue, null, divider)

        if (global)
            operatingsystem.provider.executeGlobalConf(propertyname, propertyvalue)

        serialized.add(command)
    }

    @Override
    String getKey() {
        return "PROPERTY $propertyname"
    }


}
