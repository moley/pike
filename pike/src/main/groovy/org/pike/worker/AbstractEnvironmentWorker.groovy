package org.pike.worker

import groovy.util.logging.Slf4j
import org.pike.env.AliasEntry
import org.pike.env.DefaultPathEntry
import org.pike.env.IEnvEntry
import org.pike.env.IncludeEnvEntry
import org.pike.env.PathEntry
import org.pike.env.PropertyEntry
import org.pike.os.IOperatingsystemProvider
import org.pike.worker.properties.Chapter
import org.pike.worker.properties.Entry
import org.pike.worker.properties.Propertyfile

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 17.09.13
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
abstract class AbstractEnvironmentWorker extends PikeWorker  {

    private File file

    Collection <IEnvEntry> entries = new ArrayList<IEnvEntry>()

    protected global = false

    String chapter

    /**
     * configures file path
     * @param file file path
     */
    public void file (String file) {
        if (file == null)
            throw new NullPointerException('Parameter file must not be null')

        this.file = toFile(file)
    }

    /**
     * configures a chapter the settings are stored to
     * @param chapter
     */
    public void chapter (final String chapter) {
        if (chapter != null)
            throw new IllegalStateException('You cannot override chapter')

        this.chapter = chapter

    }

    /**
     * getter
     * @return file which is configured
     */
    public File getFile ()  {
        file
    }



    public void alias (final String from, final String to) {
        entries.add(new AliasEntry(from, to))
    }

    /**
     * configures a toPath which is prefixed to PATH and exports it
     * @param pathname      name of toPath, e.g. GRADLE_HOME
     * @param pathvalue     value of toPath
     * @param subPathAddedToDefaultPath    null: nothing added to PATH,
     *                              "" the toPath itself is added to PATH
     *                              different value: subpath is added to PATH
     * @param addToPath     true: toPath is added to PATH variable, false: not added
     */
    public void path (final String pathname, final String pathvalue, String subPathAddedToDefaultPath = null) {
        entries.add(new PathEntry(pathname, pathvalue, subPathAddedToDefaultPath))
    }

    /**
     * configures a extention of the PATH variable
     * @param defaultpathadd
     */
    public void defaultpath (final String defaultpathadd) {
        entries.add(new DefaultPathEntry(defaultpathadd))
    }

    /**
     * configures a includefile in the current env toFile
     * @param includefile  toFile to be included
     * @param toDevNull     boolean flag if >/dev/null should be appended
     */
    public void include (final String includefile, boolean toDevNull = false) {
        if (includefile == null)
            throw new NullPointerException("Parameter include must not be null")

        entries.add(new IncludeEnvEntry(new File (includefile), toDevNull))
    }


    /**
     * configures a property to the toFile
     * @param key       key of property
     * @param value     value of property
     */
    public void property(final String key, final String value, String divider = null) {
        entries.add(new PropertyEntry(key, value, false, divider))
    }

    /**
     * configures a property to the toFile
     * @param key       key of property
     * @param value     value of property
     */
    public void exportedproperty(final String key, final String value, String divider = null) {
        entries.add(new PropertyEntry(key, value, true, divider))
    }



    @Override
    void install() {
        log.info("Configure file $file")


        if (file != null) {
          if (! file.parentFile.exists())
            if (file.parentFile.mkdirs() == false)
                throw new IllegalStateException("Could not create directory " + file.parent)
        }

        boolean readExistingFile = file != null && file.exists()
        IOperatingsystemProvider osProvider = operatingsystem.provider
        Propertyfile propertyfile = readExistingFile ? new Propertyfile(osProvider, file) : new Propertyfile(osProvider)

        Collection<Chapter> chapters = propertyfile.getChapters(chapter)

        if (chapters.isEmpty()) {
            Chapter newChapter = new Chapter(propertyfile, chapter)
            chapters.add(newChapter)
            propertyfile.chapters.add(newChapter)
        }

        for (IEnvEntry nextEntry: entries) {

            Collection<String> serializedValue = new ArrayList<String>()
            nextEntry.serialize(operatingsystem, serializedValue, global)

            for (Chapter nextChapter: chapters) {

                //If we find a already piked entry, than adapt it
                Entry foundEntry = nextChapter.findEntryByPikeKey(nextEntry)
                if (foundEntry != null)
                    foundEntry.replaceContent(serializedValue)
                else {
                    //Comment original value
                    Entry originEntry = nextChapter.findOriginEntry(operatingsystem, nextEntry)
                    if (originEntry != null)
                        originEntry.comment(operatingsystem)

                    //Add a new entry
                    if (foundEntry == null) {
                        foundEntry = new Entry(nextChapter, serializedValue, nextEntry.pikeKey)
                        nextChapter.addEntry(foundEntry)
                    }
                }
            }
        }

        if (file)
          file.text = propertyfile.toString()
    }

    public boolean uptodate () {
        log.info("Worker $name is not uptodate due to TODO")
        return false
    } //TODO



}
