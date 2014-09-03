package org.pike.tasks

import org.gradle.api.DefaultTask
import org.gradle.util.ConfigureUtil
import org.pike.common.TaskContext
import org.pike.model.defaults.Defaults
import org.pike.model.environment.Environment
import org.pike.model.host.Host
import org.pike.model.operatingsystem.Operatingsystem

import java.nio.file.FileSystems
import java.nio.file.Path

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 30.04.13
 * Time: 23:26
 * To change this template use File | Settings | File Templates.
 */
public abstract class PikeTask extends DefaultTask {


    Operatingsystem operatingsystem

    private Defaults defaults

    String user

    public static String NEWLINE = System.getProperty("line.separator")

    public user (final String user) {
        this.user = user
    }

    public Defaults getDefaults () {
        return defaults != null ? defaults : project.defaults
    }

    public String getUser () {
        if (user)
            return user
        if (defaults)
            return defaults.defaultuser

        return project.defaults.defaultuser

    }


    public Path path (final String path) {
        if (path == null)
            return null
        if (path.startsWith("/") && getDefaults().rootpath) //TODO make it cool for windows
            return FileSystems.getDefault().getPath(getDefaults().rootpath, path)
        else
            return FileSystems.getDefault().getPath(path)
    }

    public File file (final String path) {
        if (path == null)
            return null

        if (path.startsWith("/") && getDefaults().rootpath) { //TODO make it cool for windows
            return new File (getDefaults().rootpath + path)
        }
        else
            return new File (path)
    }

}
