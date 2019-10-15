package org.pike.configuration

import org.gradle.api.Project


class Configurable {

    Configuration configuration

    Project project

    void configuration (Closure closure) {
        configuration = new Configuration()
        project.configure(configuration, closure)
    }


}
