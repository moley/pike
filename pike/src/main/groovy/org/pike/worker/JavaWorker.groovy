package org.pike.worker

import groovy.util.logging.Slf4j
import org.pike.worker.java.CertUtils

/**
 * Worker to configure a java vm
 * Created by OleyMa on 15.09.14.
 */
@Slf4j
class JavaWorker extends PikeWorker {

    /**
     * jdk home
     */
    private File jdkHome

    /**
     * list of certificates to import
     */
    private Collection <String> certificateHosts = new HashSet<String>()



    /**
     * sets jdkHome
     * @param jdkHome  jdkHome
     */
    public void jdkHome (final String jdkHome) {
        this.jdkHome = project.file(jdkHome)
    }

    /**
     * adds a certificate to import to jdk
     * @param host  host
     */
    public void certificate (final String host) {
        this.certificateHosts.add(host)
    }

    @Override
    void install() {

        for (String nextHost: certificateHosts) {

            if (! CertUtils.getCertsFile(jdkHome).exists()) {
                log.info("No certfile exists for jdk ${jdkHome.absolutePath}")
                continue
            }

            log.info("Import certifiacte for host ${nextHost} into jdk ${jdkHome.absolutePath}")
            String commandline = CertUtils.readCertificateAndWriteToFile(nextHost, jdkHome)
            if (commandline != null) {
                Process process = commandline.execute()
                log.debug("Execute $commandline returned " + process.exitValue())
            }
        }

    }

    @Override
    boolean uptodate() {
        return false
    }
}
