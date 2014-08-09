package org.pike.vagrant

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.model.host.Host

/**
 * Created by OleyMa on 05.08.14.
 */
class VagrantCliTask extends DefaultTask {

    Host host

    List<String> commands = ['vagrant']

    public void executeCli () {

        File hostDir = VagrantUtil.getWorkingDir(project, host)

        project.exec {
            workingDir hostDir.absolutePath
            commandLine commands
        }
    }





}
