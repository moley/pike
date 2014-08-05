package org.pike.utils

import java.util.zip.ZipFile

/**
 * Created by OleyMa on 08.05.14.
 */
class ZipUtils {

    /**
     * gets the unzipped rootpaths of the zipfile relative to the param rootpath
     * @param rootpath  path to unzip the zipfile to
     * @param zipfile  zipfile itself
     * @return collection of determined rootpaths that the unzipped zipfile adds
     */
    public Collection<File> getRootpaths (final File rootpath, final File zipfile) {
        Set<String> roots = new HashSet<String>()
        def zipFile = new java.util.zip.ZipFile(zipfile)
        zipFile.entries().each {
            roots.add(it.name.substring(0, it.name.indexOf('/')))
        }

        Collection<File> files = new ArrayList<File>()
        for (String nextRoot: roots) {
            files.add(new File (rootpath, nextRoot))
        }

        return files

    }

    public Collection<File> unzippedFiles (final File zipfile, final File parentPath) {

        Set <File> allRoots = new HashSet<File>()
        ZipFile file = new ZipFile(zipfile)
        file.entries().each { entry ->
            String rootPath = entry.name.substring(0, entry.name.indexOf("/"))
            allRoots.add(new File (parentPath, rootPath))
        }

        return allRoots

    }
}
