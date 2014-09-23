package org.pike.tasks

import groovy.util.logging.Log
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.pike.model.environment.Environment
import org.pike.worker.PikeWorker

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 08.05.13
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
@Log
class DelegatingTask extends DefaultTask {

    Project project

    String paramkey

    String paramvalue

    Environment environment


}
