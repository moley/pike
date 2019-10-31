package org.pike.configuration


class Tool {

    PikeExtension pikeExtension

    String version

    String globalConfFolder

    void version (String version) {
        this.version = version
    }

    void globalConfFolder (final globalConfFolder) {
        this.globalConfFolder = globalConfFolder
    }
}
