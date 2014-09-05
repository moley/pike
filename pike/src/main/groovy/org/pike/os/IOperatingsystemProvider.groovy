package org.pike.os

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 14.05.13
 * Time: 23:25
 * To change this template use File | Settings | File Templates.
 */
public interface IOperatingsystemProvider {

    //Start of bootstrap

    String getBootstrapCommandRemovePath ()

    String getBootstrapCommandMakePath ()

    String getBootstrapCommandChangePath()

    String getBootstrapCommandInstall()

    String getBootstrapCommandMakeWritablePath()

    String getBootstrapCommandStartConfigure()

    String getBootstrapCommandAdaptUser ()

    //End of bootstrap

    String getFileSeparator ()

    String getPathSeparator ()

    String getCommentPrefix ()

    String getCommandSeparator ()

    String getSetEnvPrefix()

    void executeGlobalConf (final String key, final String value, final String addon)

    String getAsVariable (final String variablename)

    String addPath (final String path, final String addPath)

    String getOsDependendPath (final String normalizedPath)

    void adaptLineDelimiters (final File originfile, final File adaptedfile)

    String adaptLineDelimiters (String originText)

    /**
     * method checks if the current host is a host with the current os
     * @return true: current host has this os, false: current host has another os
     */
    boolean isActive ()

    String getId ()


}