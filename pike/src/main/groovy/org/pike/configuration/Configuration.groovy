package org.pike.configuration

class Configuration {

    String encoding

    Boolean spacesForTabs

    Integer tabWidth

    Boolean showMemory

    Boolean showLineNumbers

    String basepath

    void spacesForTabs (final boolean spacesForTabs) {
        this.spacesForTabs = spacesForTabs
    }

    void encoding (String encoding) {
        this.encoding = encoding
    }

    void basepath (String basepath) {
        this.basepath = basepath
    }

    void tabWidth (final int tabWidth) {
        this.tabWidth = tabWidth
    }

    void showMemory (final boolean showMemory) {
        this.showMemory = showMemory
    }

    void showLineNumbers (final boolean showLineNumbers) {
        this.showLineNumbers = showLineNumbers
    }

}
