package org.pike.configuration


class Eclipse extends Tool {

    Collection <String> repos = []

    Collection <String> features = []



    void repo (final String repo) {
        this.repos.add(repo)
    }

    void feature (final String feature) {
        this.features.add(feature)
    }

}
