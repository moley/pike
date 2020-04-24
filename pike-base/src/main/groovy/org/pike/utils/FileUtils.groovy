package org.pike.utils

import groovy.io.FileType


class FileUtils {

    public File getSingleChild (final File parent) {
        File[] children = parent.listFiles()
        if (children.length == 1)
            return children[0]
        else
            throw new IllegalStateException("Not exactly one child found in " + parent.absolutePath + ", but " + children.length + "\n(" + children.toString().replace(",", "\n") + ")")
    }

    public List<File> findFiles (final File parent, final String filename) {
        List<File> files = new ArrayList<File>()
        parent.eachFileRecurse(FileType.FILES) {
            if(it.name.equals(filename))
                files.add(it)
        }

        return files
    }

    public File findFile (final File parent, final String filename) {
        List<File> foundFiles = findFiles(parent, filename)
        if (foundFiles.size() != 1)
            throw new IllegalStateException("Did not find one file " + filename + " in path " + parent.absolutePath + ", but " + foundFiles.size())
        return foundFiles.get(0)
    }
}
