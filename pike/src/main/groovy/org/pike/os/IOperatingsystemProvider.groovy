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

    String getBootstrapCommandStartConfigure()

    //End of bootstrap

    String getFileSeparator ()

    String getPathSeparator ()

    String getCommentPrefix ()

    String getCommandSeparator ()

    String getScriptSuffix ()

    String getSetEnvPrefix()

    void executeGlobalConf (final String key, final String value, final String addon)

    String getAsVariable (final String variablename)

    boolean isAbsolute(final String path)

    String addPath (final String path, final String addPath)

    String getOsDependendPath (final String normalizedPath)

    void adaptLineDelimiters (final File originfile, final File adaptedfile)

    String adaptLineDelimiters (String originText)


}