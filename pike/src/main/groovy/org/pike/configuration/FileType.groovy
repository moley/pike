package org.pike.configuration


enum FileType {

    ZIP (".zip")

    final String suffix

    private FileType (String suffix) {
        this.suffix = suffix
    }

}
