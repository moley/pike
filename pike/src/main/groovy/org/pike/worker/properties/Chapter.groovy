package org.pike.worker.properties

import org.pike.env.IEnvEntry
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.IOperatingsystemProvider

/**
 * Created by OleyMa on 08.05.14.
 */
class Chapter {

    String chaptername

    List<Entry> entries = new ArrayList<Entry>()

    Propertyfile propertyfile

    public Chapter (final Propertyfile propertyfile, final String chaptername) {
        this.propertyfile = propertyfile
        this.chaptername = chaptername
    }

    public void addEntry (Entry entry) {
        this.entries.add(entry)
    }

    public boolean isEmpty () {
        return entries.isEmpty()
    }

    public boolean isDefaultChapter () {
        return chaptername == null
    }

    public String toString () {
        IOperatingsystemProvider osProvider = propertyfile.osProvider
        String chapter = ""
        if (chaptername != null)
          chapter += "[" + chaptername + "]\n"
        for (Entry next: entries) {
            chapter += next.toString()
        }

        osProvider.adaptLineDelimiters(chapter)
        return chapter
    }

    public Entry findEntryByPikeKey(final IEnvEntry entry) {
        for (Entry next: entries) {
            if (next.pikeKey.equals(entry.pikeKey))
                return next
        }
        return null
    }

    public Entry findOriginEntry(final Operatingsystem os, IEnvEntry entry) {
        for (Entry next: entries) {
            if (entry.isOriginEntry(os, next.content))
                return next
        }
        return null
    }

}
