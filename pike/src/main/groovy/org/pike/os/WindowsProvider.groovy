package org.pike.os

import groovy.util.logging.Slf4j

/**
 * Provider for operatingsystem specialities depending on windows
 */
@Slf4j
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
        return "unzip.exe PARAM0"
    }

    @Override
    String getBootstrapCommandStartConfigure() {
        return "configureHost.bat"
    }

    @Override
    String getBootstrapCommandMakeWritablePath() {
        return ""
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
    String getSetEnvPrefix() {
        return "SET "
    }

    @Override
    String getId() {
        return 'win'
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
    boolean isActive() {
        return System.getProperty('os.name').toLowerCase().contains('win')
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
