package org.pike.vagrant

import groovy.util.logging.Slf4j
import org.pike.model.host.Host

/**
 * Created by OleyMa on 05.08.14.
 */
@Slf4j
class Vagrant {
    String boxUrl
    Host host

    public Vagrant (final Host host) {
        log.info("Createing Vagrant object for host $host.name")
        this.host = host
    }

    void box (final String url) {
        log.info("Configure box $url")
        this.boxUrl = url
    }
}
