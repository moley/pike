package org.pike.model

import groovy.util.logging.Slf4j
import org.gradle.internal.reflect.Instantiator
import org.pike.common.NamedElement

/**
 * Created by OleyMa on 11.08.14.
 */
@Slf4j
class Vagrant extends NamedElement {

    String boxUrl

    /**
     *
     * @param name
     */
    public Vagrant (String name, Instantiator instantiator = null) {
        super (name, instantiator)
        log.info("Creating Vagrant object with name $name")
    }

    void box (final String url) {
        log.info("Configure box $url")
        this.boxUrl = url
    }
}
