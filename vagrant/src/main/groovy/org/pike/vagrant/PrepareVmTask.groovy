package org.pike.vagrant

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.model.host.Host

/**
 * Created by OleyMa on 05.08.14.
 */
class PrepareVmTask extends DefaultTask {

    Host host

    @TaskAction
    public void prepare () {

    }
}
