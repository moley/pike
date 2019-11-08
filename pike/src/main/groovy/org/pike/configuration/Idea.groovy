package org.pike.configuration


class Idea extends Tool {

    Collection <String> plugins = []

    void plugin (String url) {
        this.plugins.add(url)
    }



}
