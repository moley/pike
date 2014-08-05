package org.pike.os

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 14.05.13
 * Time: 23:26
 * To change this template use File | Settings | File Templates.
 */
class LinuxProvider extends AbstractOsProvider {
    @Override
    String getBootstrapCommandRemovePath() {
        return "rm -rf PARAM0"
    }

    @Override
    String getBootstrapCommandMakePath() {
        return "mkdir -p PARAM0"
    }

    @Override
    String getBootstrapCommandChangePath() {
        return "cd PARAM0"
    }

    @Override
    String getBootstrapCommandInstall() {
        return "sh PARAM0/installPike.sh"
    }

    @Override
    String getBootstrapCommandStartConfigure() {
        return "sh PARAM0/configureHost"      //TODO rename to configureHost.sh
    }

    @Override
    String getFileSeparator() {
        return "/"
    }

    @Override
    String getPathSeparator() {
        return ":"
    }

    @Override
    String getCommentPrefix() {
        return "# "
    }

    @Override
    String getCommandSeparator() {
        return ";"
    }

    @Override
    String getScriptSuffix() {
        return ".sh"
    }

    @Override
    String getSetEnvPrefix() {
        return "export "
    }

    @Override
    void executeGlobalConf(String key, String value, final String addOn) {
        //TODO is empty, because global conf is applied by writing it to global conffile
    }

    @Override
    String getAsVariable(String variablename) {
        return "\$" + variablename
    }

    @Override
    boolean isAbsolute(String path) {
        return path.startsWith("/")
    }

    @Override
    String getOsDependendPath(String normalizedPath) {
        return normalizedPath
    }

    @Override
    void adaptLineDelimiters(File fromfile, File tofile) {
        //TODO sometimes we want to administrate linux systems from a windows host.
        //Than we have to replace windows things with linux delimiters
    }

    @Override
    String adaptLineDelimiters(String originText) {
        return originText
    }
}
