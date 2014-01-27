package org.pike.autoinstall

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.TaskAction
import org.gradle.logging.ProgressLoggerFactory

import org.pike.model.host.Host
import org.pike.remoting.RemoteTask

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 29.04.13
 * Time: 00:15
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class AutoinstallTask extends RemoteTask {


    private AutoinstallWorker logic = new AutoinstallWorker()

    @TaskAction
    public void installPike () {

        for (Host nextHost: getHostsToBuild()) {

            PropertyChangeProgressLogging progressLogging = new PropertyChangeProgressLogging(getServices().get(ProgressLoggerFactory), AutoinstallTask)

            try {
            progressLogging.getProgressLogger().setDescription("Installing pike on host $nextHost.hostname")
            progressLogging.start("Start installation of pike on host $nextHost.hostname")

            logic.operatingsystem = nextHost.operatingsystem

            logic.initializePaths(project, nextHost)
            logic.uploadGradle(project, nextHost, defaults, progressLogging)
            logic.uploadJre(project, nextHost, progressLogging)
            logic.uploadBootstrapScripts(project, nextHost, progressLogging)
            logic.uploadProjectDescriptions(project, nextHost, progressLogging)
            logic.uploadPlugins(project, nextHost, progressLogging)
            logic.installPike(project, nextHost, progressLogging)

            } catch (Exception e) {
              log.error("Could not installPike host $nextHost due to error ${e.toString()}", e)
            } finally {
              logic.disconnectConnections()
              progressLogging.end()
            }
        }


    }



}
