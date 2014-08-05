package org.pike.os

import groovy.util.logging.Log

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 13.09.13
 * Time: 10:14
 * To change this template use File | Settings | File Templates.
 */
@Log
class WindowsProvider extends AbstractOsProvider{
    @Override
    String getBootstrapCommandRemovePath() {
        return "cmd /c rmdir PARAM0 /s /q"
    }

    @Override
    String getBootstrapCommandMakePath() {
        return "cmd /c mkdir PARAM0"
    }

    @Override
    String getBootstrapCommandChangePath() {
        return "cmd /c cd PARAM0"
    }

    @Override
    String getBootstrapCommandInstall() {
        return "installPike.bat"
    }

    @Override
    String getBootstrapCommandStartConfigure() {
        return "configureHost.bat"
    }

    @Override
    String getFileSeparator() {
        return "\\"
    }

    @Override
    String getPathSeparator() {
        return ";"
    }

    @Override
    String getCommentPrefix() {
        return "@REM "
    }



    @Override
    String getCommandSeparator() {
        return " & "
    }

    @Override
    String getScriptSuffix() {
        return ".bat"
    }

    @Override
    String getSetEnvPrefix() {
        return "SET "
    }

    @Override
    void executeGlobalConf(String key, String value, String addon) {
        log.fine("executing global confs is deactivated because it breaks everything :-) (" + key + "," + value + "," + addon)

        /**if (addon == null)
            addon = ""
        else
            addon = pathSeparator + addon

        value = getOsDependendPath(value)

        String command = "SETX " + key + " \"" + value + addon + "\" /m"

        Process process = command.execute()
        int returnCode = process.waitFor()
        log.fine("execute global conf: " + command + "\n" + process.text)
        if (returnCode != 0)
            throw new IllegalStateException("Command "+ command + " ended up with returncode " + returnCode)**/

    }

    @Override
    String getAsVariable(String variablename) {
        return "%" + variablename + "%"
    }

    @Override
    boolean isAbsolute(String path) {
        return path.charAt(1) == ':'
    }



    @Override
    String getOsDependendPath(String normalizedPath) {

        boolean isLocalFile = true
        try {
          URL url = new URL(normalizedPath)
          isLocalFile = false
        } catch (MalformedURLException e) {
            isLocalFile = ! normalizedPath.contains("@")
        }

        return isLocalFile ? normalizedPath.replace("/", "\\"): normalizedPath
    }

    String adaptLineDelimiters (String originText) {
        return originText.replace('\n', '\r\n')
    }

    @Override
    void adaptLineDelimiters(File originfile, File adaptedfile) {
        if (originfile.name.contains("configureHost"))
            return

        log.fine("adapt line delimiters from file " + originfile.absolutePath + " to file " + adaptedfile.absolutePath)

        adaptedfile.text = originfile.text.replace('\n', '\r\n') //from linux to windows delimiters
        //TODO sometimes we want to administrate linux systems from a windows host.
        //Than we have to replace windows things with linux delimiters
    }
}
