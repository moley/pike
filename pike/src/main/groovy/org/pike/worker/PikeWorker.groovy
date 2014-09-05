package org.pike.worker

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.pike.cache.CacheManager
import org.pike.common.TaskContext
import org.pike.model.defaults.Defaults
import org.pike.model.environment.Environment
import org.pike.model.host.Host
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.IOperatingsystemProvider
import org.pike.os.WindowsProvider

import java.nio.file.FileSystems
import java.nio.file.Path

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 08.05.13
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
public abstract class PikeWorker {

    Project project

    CacheManager cacheManager = new CacheManager()

    Operatingsystem operatingsystem

    private Defaults defaults

    String user

    String group

    public static String NEWLINE = System.getProperty("line.separator")

    Environment environment

    Host host

    String name

    TaskContext context

    Closure autoconfigClosure

    String paramkey

    String paramvalue

    String fileFlags


    public void configure (final PikeWorker otherWorker) {
        project = otherWorker.project
        cacheManager = otherWorker.cacheManager
        operatingsystem = otherWorker.operatingsystem
        defaults = otherWorker.defaults
        user = otherWorker.user
        group = otherWorker.group
    }

    public String directoryName (final String url) {
        String cutoff = url.substring(url.lastIndexOf("/") + 1)
        return cutoff.substring(0, cutoff.lastIndexOf("."))
    }

    public group (final String group) {
        this.group = group
    }

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

        if (operatingsystem == null)
            throw new IllegalStateException("No operatingsystem set")

        IOperatingsystemProvider osProvider = operatingsystem.provider

        String completeFile = path
        if (getDefaults().rootpath)
            completeFile = osProvider.addPath(getDefaults().rootpath, path)

        File file = new File (osProvider.getOsDependendPath(completeFile))
        if (log.debugEnabled)
          log.debug("toFile " + path + "->" + completeFile + "->" + file.absolutePath)

        return new File (osProvider.getOsDependendPath(completeFile))
    }





    public String getDetailInfo () {
        return  "  * worker " + getClass().name + NEWLINE +
                "    - environment  : ${environment.name}$NEWLINE" +
                "    - workername   : ${name}$NEWLINE" +
                "    - user         : ${user}$NEWLINE" +
                "    - paramkey     : ${paramkey} $NEWLINE" +
                "    - paramvalue   : ${paramvalue}$NEWLINE"
    }



    /**
     * adapts file to user and flags
     * @param fileToAdapt file to be adapted to user from worker or default and fileflags from worker
     */
    protected void adaptFileFlags (File fileToAdapt, String user, String group, String fileFlags) {

        //TODO solve with Java NIO
        if (! (operatingsystem.provider instanceof WindowsProvider)) {
            if (user != null && group != null) {

                String command = "chown -R $user:$group $fileToAdapt"
                log.debug(command)
                Process process = Runtime.getRuntime().exec(command)
                int returnCode = process.waitFor()
                log.info("Modify user $user and group $group of file $fileToAdapt.absolutePath (returncode $returnCode)")
                if (returnCode != 0)
                    log.warn(" - Command " + command)
            }
            else
              log.info("Skip modifying user $user and group $group of file $fileToAdapt.absolutePath")

            if (fileFlags != null) {

                String command2 = "chmod -R $fileFlags $fileToAdapt"
                log.debug(command2)
                Process process = Runtime.getRuntime().exec(command2)
                int returnCode = process.waitFor()
                log.info("Modify fileflags $fileFlags of file $fileToAdapt.absolutePath (returncode $returnCode)")
                if (returnCode != 0)
                    log.warn(" - Command " + command2)
            }
            else
                log.info("Skip modifying fileflags of file $fileToAdapt.absolutePath")
        }
    }



    public abstract void install ()


    public abstract boolean uptodate ()




}
