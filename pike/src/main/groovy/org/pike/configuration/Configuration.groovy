package org.pike.configuration

import org.gradle.api.Project

class Configuration {

    String encoding

    Formatter formatter

    Boolean showMemory

    Boolean showLineNumbers

    String basepath

    Project project

    void encoding (String encoding) {
        this.encoding = encoding
    }

    void basepath (String basepath) {
        this.basepath = basepath
    }


    void showMemory (final boolean showMemory) {
        this.showMemory = showMemory
    }

    void showLineNumbers (final boolean showLineNumbers) {
        this.showLineNumbers = showLineNumbers
    }

    void formatter (Closure closure) {
        formatter = new Formatter()
        formatter.project = project
        project.configure(formatter, closure)
    }

}
