package org.pike.worker

import org.gradle.api.Project
import org.pike.model.defaults.Defaults
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.IOperatingsystemProvider

import java.nio.file.FileSystems
import java.nio.file.Path

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 08.05.13
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
public abstract class PikeWorker {

    Project project

    Operatingsystem operatingsystem

    private Defaults defaults

    String user

    String group

    public static String NEWLINE = System.getProperty("line.separator")

    public user (final String user) {
        this.user = user
    }

    public Defaults setDefaults (final Defaults defaults) {
        this.defaults = defaults
    }

    public Defaults getDefaults () {
        return defaults != null ? defaults : project.defaults
    }

    public String getUser () {
        if (user)
            return user
        if (defaults != null)
            return defaults.defaultuser

        return project.defaults.defaultuser

    }

    public String getGroup () {
        if (group)
            return group

        if (getUser() != null)
            return getUser()
        else
            return getUser() //TODO make better
    }


    /**
     * getter
     * @return cachedir
     */
    public File getCacheDir () {
        if (operatingsystem.cachedir == null)
            return null
        return toFile(operatingsystem.cachedir)
    }

    /**
     * getter
     * @return tempdir
     */
    public File getTempDir () {

        if (operatingsystem == null)
            throw new IllegalStateException("No operatingsystem specific data applied")

        if (operatingsystem.tmpdir == null)
            return null

        return toFile (operatingsystem.tmpdir)
    }

    /**
     * getter
     * @return true if cache is defined, false, if not
     */
    public boolean isCacheDirDefined () {
        return getCacheDir() != null
    }

    public Path toPath(final String path) {
        if (path == null)
            return null

        IOperatingsystemProvider osProvider = operatingsystem.provider

        String completeFile = path
        if (getDefaults().rootpath)
            completeFile = osProvider.addPath(getDefaults().rootpath, path)

        return FileSystems.getDefault().getPath(osProvider.getOsDependendPath(completeFile))
    }

    public File toFile(final String path) {
        if (path == null)
            return null

        IOperatingsystemProvider osProvider = operatingsystem.provider

        String completeFile = path
        if (getDefaults().rootpath)
            completeFile = osProvider.addPath(getDefaults().rootpath, path)


        return new File (osProvider.getOsDependendPath(completeFile))
    }




}
