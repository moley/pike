package org.pike.utils


class FileUtils {

    public File getSingleChild (final File parent) {
        File[] children = parent.listFiles()
        if (children.length == 1)
            return children[0]
        else
            throw new IllegalStateException("Not exactly one child found in " + parent.absolutePath + ", but " + children.length + "\n(" + children.toString().replace(",", "\n") + ")")

    }
}
