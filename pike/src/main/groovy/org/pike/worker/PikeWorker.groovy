package org.pike.worker

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.util.ConfigureUtil
import org.pike.cache.CacheManager
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
public abstract class PikeWorker extends DefaultTask {

    CacheManager cacheManager = new CacheManager()

    Operatingsystem operatingsystem

    private Defaults defaults

    String fsUser

    String fsGroup

    public static String NEWLINE = System.getProperty("line.separator")

    Environment environment

    Host host

    Closure autoconfigClosure

    String paramkey

    String paramvalue

    String ordinaryFileFlag

    String executableFileFlag = '755'


    public void configure(final PikeWorker otherWorker) {
        cacheManager = otherWorker.cacheManager
        operatingsystem = otherWorker.operatingsystem
        defaults = otherWorker.defaults
        this.fsUser = otherWorker.fsUser
        if (log.debugEnabled)
            log.debug("group ${group} was configure by worker $otherWorker.name")
        this.fsGroup = otherWorker.fsGroup
    }

    /**
     * installPike the task, set all relevant model elements
     * @param host
     */
    public void configure(final Host host) {
        this.host = host
        this.operatingsystem = host.operatingsystem
        if (this.autoconfigClosure != null)
            ConfigureUtil.configure(autoconfigClosure, this)

    }

    public String directoryName(final String url) {
        String cutoff = url.substring(url.lastIndexOf("/") + 1)
        return cutoff.substring(0, cutoff.lastIndexOf("."))
    }

    public group(final String group) {
        if (log.debugEnabled)
            log.debug("User configures group=${group}")
        this.fsGroup = group
    }

    public user(final String user) {
        this.fsUser = user
    }

    public Defaults setDefaults(final Defaults defaults) {
        this.defaults = defaults
    }

    public Defaults getDefaults() {
        return defaults != null ? defaults : project.defaults
    }

    public String getFsUser() {
        if (this.fsUser != null)
            return this.fsUser
        if (defaults != null)
            return defaults.fsUser

        return project.defaults.fsUser

    }

    public String getFsGroup() {
        if (this.fsGroup != null) {
            if (log.debugEnabled)
                log.debug("Group set to ${this.fsGroup} ")
            return this.fsGroup
        }

        if (defaults != null)
            return defaults.fsGroup

        return project.defaults.fsGroup
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

        File file = new File(osProvider.getOsDependendPath(completeFile))
        if (log.debugEnabled)
            log.debug("toFile " + path + "->" + completeFile + "->" + file.absolutePath)

        return new File(osProvider.getOsDependendPath(completeFile))
    }


    public String getDetailInfo() {
        String env = environment != null ? environment.name : null
        return "  * worker " + getClass().name + NEWLINE +
                "    - environment  : ${env}$NEWLINE" +
                "    - workername   : ${name}$NEWLINE" +
                "    - fsUser       : ${fsUser}$NEWLINE" +
                "    - fsUser      : ${fsGroup}$NEWLINE" +
                "    - paramkey     : ${paramkey} $NEWLINE" +
                "    - paramvalue   : ${paramvalue}$NEWLINE"
    }

    /**
     * adapts file to user and flags
     * @param fileToAdapt file to be adapted to user from worker or default and fileflags from worker
     */
    protected void adaptFileFlags(File fileToAdapt, String fsUser, String fsGroup, String fileFlags) {

        //TODO solve with Java NIO
        if (!(operatingsystem.provider instanceof WindowsProvider)) {

            if (fsUser == null)
                throw new IllegalStateException('No user set. Please set an user at environment or fsUser')

            String command = "chown -R $fsUser:$fsGroup $fileToAdapt.absolutePath"
            if (fsGroup == null)
                command = "chown -R $fsUser $fileToAdapt.absolutePath"

            log.info(command)
            Process process = Runtime.getRuntime().exec(command)
            int returnCode = process.waitFor()
            log.info("Modify user $fsUser and group $fsGroup of file $fileToAdapt.absolutePath (returncode $returnCode)")
            if (returnCode != 0)
                throw new IllegalStateException("$command failed")


            if (fileFlags != null) {

                String command2 = "chmod -R $fileFlags $fileToAdapt.absolutePath"
                log.info(command2)
                process = Runtime.getRuntime().exec(command2)
                returnCode = process.waitFor()
                log.info("Modify fileflags $fileFlags of file $fileToAdapt.absolutePath (returncode $returnCode)")
                if (returnCode != 0)
                    throw new IllegalStateException("$command2 failed")
            } else
                log.info("Skip modifying fileflags of file $fileToAdapt.absolutePath")
        }
    }

    @TaskAction
    public abstract void install()

    public abstract boolean uptodate()
}
