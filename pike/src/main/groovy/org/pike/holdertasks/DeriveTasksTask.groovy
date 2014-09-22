package org.pike.holdertasks

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.common.ProjectInfo
import org.pike.model.host.Host
import org.pike.tasks.DelegatingTask
import org.pike.worker.PikeWorker

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 30.04.13
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class DeriveTasksTask extends DefaultTask {

        @TaskAction
        public void deriveTasks () {
            log.info("Configure all tasks")
            Host currentHost = ProjectInfo.getCurrentHost(project)
            project.tasks.withType(PikeWorker).each { PikeWorker task ->
                if (log.debugEnabled)
                  log.debug("Configure task " + task.name)
                task.configure(currentHost)
            }
            log.info("Configuration of tasks completed")
        }

}
