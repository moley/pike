package org.pike.configuration

import org.gradle.api.Project


class Tool {

    Project project

    String version

    String globalConfFolder

    String xmx

    void version (String version) {
        this.version = version
    }

    void globalConfFolder (final globalConfFolder) {
        this.globalConfFolder = globalConfFolder
    }

    void xmx (final String xmx) {
        this.xmx = xmx
    }
}
