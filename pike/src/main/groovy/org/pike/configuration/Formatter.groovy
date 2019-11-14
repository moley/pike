package org.pike.configuration

import org.gradle.api.Project


class Formatter {

    Boolean spacesForTabs

    Integer tabWidth

    Integer indent

    String name

    Project project

    Integer lineSplit

    void name (final String name) {
        this.name = name
    }

    void spacesForTabs (final boolean spacesForTabs) {
        this.spacesForTabs = spacesForTabs
    }

    void tabWidth (final int tabWidth) {
        this.tabWidth = tabWidth
    }

    void indent (final int indent) {
        this.indent = indent
    }

    void lineSplit (final int lineSplit) {
        this.lineSplit = lineSplit
    }

}
