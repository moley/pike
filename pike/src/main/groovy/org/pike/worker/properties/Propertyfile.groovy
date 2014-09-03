package org.pike.worker.properties

import org.pike.env.IEnvEntry
import org.pike.os.IOperatingsystemProvider

/**
 * Created by OleyMa on 08.05.14.
 */
class Propertyfile {

    private static String NEWLINE = System.getProperty("line.separator")

    Set<Chapter> chapters = new LinkedHashSet<Chapter>()

    IOperatingsystemProvider osProvider

    /**
     * default constructor
     */
    public Propertyfile (IOperatingsystemProvider osProvider) {
        this.osProvider = osProvider
    }


    String readKey (final String nextLine) {
        int bracketStart = nextLine.indexOf('(') + 1
        int bracketEnd = nextLine.indexOf(')')
        return nextLine.substring(bracketStart, bracketEnd)
    }


    public Propertyfile (IOperatingsystemProvider osProvider, final File propFile) {
        this.osProvider = osProvider

        if (propFile == null)
            throw new IllegalStateException("Propertyfile can not be read from null")


        Chapter nextChapter = new Chapter(this, null)

        boolean fileHasContent = propFile.exists() && ! propFile.text.trim().isEmpty()

        List<String> contentOfFile = fileHasContent ? propFile.text.trim().split(NEWLINE).toList() : new ArrayList<String>()


        Entry nextPikedEntry = null

        for (int i = 0; i < contentOfFile.size(); i++) {
            String nextLine = contentOfFile.get(i)
            if (nextLine.startsWith('[')) { //Chapter
                String chaptername = nextLine.substring(1, nextLine.length() - 1)
                if (! nextChapter.empty)
                    chapters.add(nextChapter)
                nextChapter = new Chapter(this, chaptername)

                continue
            }


            if (nextLine.contains('pike    BEGIN')) {
                String key = readKey(nextLine)
                nextPikedEntry = new Entry(nextChapter, key)
                continue
            }

            if (nextLine.contains('pike    END')) {
                String keyEnd = readKey(nextLine)
                if (keyEnd != nextPikedEntry.pikeKey)
                    throw new IllegalStateException("Cannot read endkey $keyEnd with startkey $nextPikedEntry.pikeKey")
                nextChapter.addEntry(nextPikedEntry)
                nextPikedEntry = null
                continue
            }

            if (nextPikedEntry != null)
                nextPikedEntry.addContent(nextLine)
            else
                nextChapter.addEntry(new Entry(nextChapter, nextLine, null))

        }

        if (! nextChapter.empty)
            chapters.add(nextChapter)




    }

    /**
     *
     * @param chapter  null: all chapters are returned, not null: only the searched chapter is returned
     * @return
     */
    Collection<Chapter> getChapters (final String chapter) {
        Collection<Chapter> filteredchapters = new ArrayList<Chapter>()
        for (Chapter next: chapters) {
            if (chapter == null || next.chaptername == chapter)
                filteredchapters.add(next)
        }

        return filteredchapters
    }



    String toString () {
        String all = ""
        for (Chapter nextChapter: chapters) {
            all += nextChapter.toString()
        }

        return all
    }
}
