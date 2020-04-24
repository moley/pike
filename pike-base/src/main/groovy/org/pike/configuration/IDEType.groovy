package org.pike.configuration


enum IDEType {

    IDEA ('Idea'),
    ECLIPSE ('Eclipse'),
    VS_CODE ('VSCode')

    String name

    private IDEType (final String name) {
        this.name = name
    }



}
