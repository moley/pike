package org.pike.vagrant

import groovy.util.logging.Slf4j
import org.pike.model.host.Host
import org.pike.model.operatingsystem.Operatingsystem

/**
 * Created by OleyMa on 05.08.14.
 */
@Slf4j
class Vagrant {
    String boxUrl
    Operatingsystem os

    public Vagrant (Operatingsystem os) {
        log.info("Creating Vagrant object for host $os.name")
        this.os = os
    }

    void box (final String url) {
        log.info("Configure box $url")
        this.boxUrl = url
    }
}
