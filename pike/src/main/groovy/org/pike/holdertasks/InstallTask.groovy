package org.pike.holdertasks

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.model.host.Host

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 16.04.13
 * Time: 00:24
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class InstallTask extends DefaultTask{

    Host currentHost

    @TaskAction
    public void install () {
        log.info("Calling InstallTask")


    }
}
