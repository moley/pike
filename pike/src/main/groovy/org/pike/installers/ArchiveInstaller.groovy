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

        HashSet<String> roots = new HashSet<String>()

        HashSet<String> items = new HashSet<String>()
        ArchiveStream stream = archiver.stream(downloadedFile)
        ArchiveEntry entry
        while((entry = stream.getNextEntry()) != null) {
            items.add(entry.name)
            if (entry.isDirectory()) {
                String [] tokens = entry.getName().split("/")
                roots.add(tokens[0])
            }

        }
        stream.close()

        if (roots.size() != 1)
            throw new IllegalStateException("Not exactly one child found in " + downloadedFile.absolutePath + ", but " + roots.size() + "\n(" + roots.toString().replace(",", "\n") + "of " + items.toString().replace(",", "\n") + ")")

        return roots.iterator().next()
    }
}
