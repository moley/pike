package org.pike.installers

import org.rauschig.jarchivelib.ArchiveEntry
import org.rauschig.jarchivelib.ArchiveStream
import org.rauschig.jarchivelib.Archiver


abstract class ArchiveInstaller extends AbstractInstaller {

    /**
     * get the roots of the file
     * @param downloadedFile  downloadedFile
     * @return
     */
    protected String getSingleRootPath (final Archiver archiver, final File downloadedFile) {

        if (! downloadedFile.exists())
            throw new IllegalArgumentException("Argument 'downloadedFile' is a non existing file")

        HashSet<String> roots = new HashSet<String>()

        ArchiveStream stream = archiver.stream(downloadedFile)
        ArchiveEntry entry
        while((entry = stream.getNextEntry()) != null) {
            String [] tokens = entry.getName().split("/")
            roots.add(tokens[0])
        }
        stream.close()

        if (roots.size() != 1)
            throw new IllegalStateException("Not exactly one child found in " + downloadedFile.absolutePath + ", but " + roots.size() + "\n(" + roots.toString().replace(",", "\n") + ")")

        return roots.iterator().next()
    }
}
