package org.pike.worker

import groovy.util.logging.Log
import groovy.util.logging.Slf4j
import org.gradle.api.tasks.TaskAction
import org.gradle.util.ConfigureUtil
import org.pike.common.TaskContext
import org.pike.model.environment.Environment
import org.pike.model.host.Host
import org.pike.os.WindowsProvider

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 08.05.13
 * Time: 13:26
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
abstract class UndoableWorker extends PikeWorker {


    Environment environment

    Host host

    String name

    TaskContext context

    Closure autoconfigClosure

    String paramkey

    String paramvalue

    String fileFlags


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
    protected void adaptFileFlags (File fileToAdapt) {

        //TODO solve with Java NIO
        if (! (operatingsystem.provider instanceof WindowsProvider)) {
          log.debug("adaptFileFlags in os " + operatingsystem.provider)
          String command = "chown -R $user:$group $fileToAdapt"
          log.debug(command)
          Runtime.getRuntime().exec(command)

          if (fileFlags != null) {
            String command2 = "chmod -R $fileFlags $fileToAdapt"
            log.debug(command2)
            Runtime.getRuntime().exec(command2)
          }
        }
    }

    public abstract void install ()

    public abstract void deinstall ()

    public abstract boolean uptodate ()



}
