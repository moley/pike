package org.pike.vagrant

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.model.host.Host

/**
 * Created by OleyMa on 05.08.14.
 */
@Slf4j
class VagrantCliTask extends DefaultTask {

    Host host

    List<String> commands = ['vagrant']

    public void executeCli () {

        File hostDir = VagrantUtil.getWorkingDir(project, host)

        log.info("Executing $commands")
        project.exec {
            workingDir hostDir.absolutePath
            commandLine commands
        }
    }





}
