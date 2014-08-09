package org.pike.remoting

import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import org.apache.tools.ant.filters.StringInputStream
import org.gradle.api.Project
import org.pike.autoinstall.PropertyChangeProgressLogging
import org.pike.model.host.Host

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 14.05.13
 * Time: 01:12
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class LocalRemoting extends AbstractRemoting {
    @Override
    void upload(String toDir, File from, PropertyChangeProgressLogging logging) {
        log.info("Uploading " + from.absolutePath + " to " + toDir)
        if (from.isDirectory())
          FileUtils.copyDirectory(from, new File (toDir))
        else
          FileUtils.copyFile(from, new File (toDir, from.name))
    }

    @Override
    RemoteResult execCmd(String cmd) {
        log.info("Executing <" + cmd + ">")
        Process process = Runtime.runtime.exec(cmd)

        BufferedReader reader = new BufferedReader (new InputStreamReader(process.errorStream));
        int failed = process.waitFor()

        String line
        while ((line = reader.readLine ()) != null) {
            log.error("Stdout: " + line);
        }


        log.info("... returned with returncode " + failed)
        RemoteResult result = new RemoteResult("localhost", "Returned with " + failed)

        return result
    }

    @Override
    void disconnect() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    boolean connectedToHost(Host host) {
        return true
    }

    @Override
    void configure(Project project, Host host) {

    }
}
