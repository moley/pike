package org.pike.env

import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.IOperatingsystemProvider

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 03.05.13
 * Time: 10:14
 * To change this template use File | Settings | File Templates.
 */
public class PathEntry extends UserEnvEntry {

    private String pathname

    private String pathvalue

    private String subpathAddedToPath

    public PathEntry (final String pathname, final String pathvalue, final String subpathAddedToPath) {
        this.pathname = pathname
        this.pathvalue = pathvalue
        this.subpathAddedToPath = subpathAddedToPath
    }

    private handleSpecificVariable(final Operatingsystem os, Collection<String> serialized, final boolean global) {
        IOperatingsystemProvider osProvider = os.provider
        String command = buildCommand(true, os, pathname, pathvalue, null)
        serialized.add(command)
        if (global)
            osProvider.executeGlobalConf(pathname, pathvalue, null)
    }

    private handlePathVariable(final Operatingsystem os, Collection<String> serialized, final boolean global) {
        IOperatingsystemProvider osProvider = os.provider
        //and PATH variable
        String pathnameKey = "PATH"

        String pathnameValue = osProvider.getAsVariable(pathname)
        if (subpathAddedToPath != null && ! subpathAddedToPath.trim().isEmpty())
            pathnameValue = pathnameValue + os.provider.fileSeparator + subpathAddedToPath

        String pathnameAddon = osProvider.getAsVariable("PATH")

        String command = buildCommand(true, os, pathnameKey, pathnameValue, pathnameAddon)
        serialized.add(command)
        if (global)
            osProvider.executeGlobalConf(pathnameKey, pathnameValue, pathnameAddon)
    }

    public void serialize(Operatingsystem operatingsystem, Collection<String> serialized, final boolean global = false) {

        if (pathvalue == null)
            throw new IllegalStateException("You cannot set a path " + pathname + " to null")

        //userspecific path variable
        handleSpecificVariable(operatingsystem, serialized, global)

        //and the userspecific variable is added to PATH
        if (subpathAddedToPath != null)
          handlePathVariable(operatingsystem, serialized, global)

    }

    @Override
    String getPikeKey() {
        return "PATH $pathname"
    }

    @Override
    boolean isOriginEntry(Operatingsystem operatingsystem, String originEntry) {
        return originEntry.contains(pathname) && originEntry.contains('=')
    }
}
