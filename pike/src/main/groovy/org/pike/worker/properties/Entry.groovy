package org.pike.worker.properties

import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.IOperatingsystemProvider

/**
 * Created by OleyMa on 08.05.14.
 */
class Entry {

    private List<String> content = new ArrayList<>()
    String pikeKey
    Chapter chapter

    public Entry (final Chapter chapter, final String pikeKey) {
        this.chapter = chapter
        this.pikeKey = pikeKey
    }

    public Entry (final Chapter chapter, final String content, final String pikeKey) {
        this (chapter, pikeKey)
        this.content.add(content)
    }

    public void comment (Operatingsystem os) {
        List<String> commented = new ArrayList<String>()
        for (String next: content) {
            if (! next.startsWith(os.provider.commentPrefix))
              commented.add(os.provider.commentPrefix + next)
            else
              commented.add(next)
        }
        content = commented
    }

    public void replaceContent (final Collection<String> newContent) {
        content.clear()
        content.addAll(newContent)
    }

    public String getContent () {
        return content.join('\n')
    }

    public void addContent (final String nextContent) {
        if (nextContent.contains('pike    END') || nextContent.contains('pike    BEGIN'))
            return
        content.add(nextContent)
    }

    public Entry (final Chapter chapter, final Collection<String> content, final String pikeKey) {
        this (chapter, pikeKey)
        for (String next: content)
            addContent(next)
    }

    public String toString () {
        String stringbuilder = ''
        IOperatingsystemProvider osProvider = chapter.propertyfile.osProvider
        if (pikeKey != null)
            stringbuilder += osProvider.commentPrefix + "pike    BEGIN (${pikeKey})\n"

        for (String nextLine: content)
            stringbuilder += nextLine + '\n'

        if (pikeKey != null)
            stringbuilder += osProvider.commentPrefix + "pike    END (${pikeKey})\n"

        return osProvider.adaptLineDelimiters(stringbuilder)
    }


}
