package org.pike.holdertasks

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 16.04.13
 * Time: 22:59
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class CheckTask extends DefaultTask {

    @TaskAction
    public void check () {
        log.info("Calling CheckTask")


    }
}
