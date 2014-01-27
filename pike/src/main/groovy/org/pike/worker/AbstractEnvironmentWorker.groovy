package org.pike.worker

import groovy.util.logging.Log
import groovy.util.logging.Slf4j
import org.pike.env.AliasEntry
import org.pike.env.DefaultPathEntry
import org.pike.env.IEnvEntry
import org.pike.env.IncludeEnvEntry
import org.pike.env.PathEntry
import org.pike.env.PropertyEntry
import org.pike.os.IOperatingsystemProvider

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 17.09.13
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
abstract class AbstractEnvironmentWorker extends UndoableWorker  {

    String file

    Collection <IEnvEntry> entries = new ArrayList<IEnvEntry>()

    protected global = false


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

        IOperatingsystemProvider osProvider = operatingsystem.provider

        File fileAsFile

        if (file != null) {
          fileAsFile = toFile(file)

          if (! fileAsFile.parentFile.exists())
            if (fileAsFile.parentFile.mkdirs() == false)
                throw new IllegalStateException("Could not create directory " + fileAsFile.parent)
        }

        boolean readExistingFile = fileAsFile != null && fileAsFile.exists()

        List<String> contentOfFile = readExistingFile ? fileAsFile.text.split(NEWLINE).toList() : new ArrayList<String>()
        for (IEnvEntry nextEntry: entries) {

            log.debug("Configuring " + nextEntry.key + (fileAsFile != null ? " in file " + fileAsFile.absolutePath: "without file"))

            String startKey = osProvider.commentPrefix + "pike    BEGIN (${nextEntry.key})"
            String endKey   = osProvider.commentPrefix + "pike    END (${nextEntry.key})"
            //check if it already installed

            int existingFrom = contentOfFile.indexOf(startKey)
            int existingEnd = contentOfFile.indexOf(endKey)

            if (existingFrom >= 0 && existingEnd < 0)
                throw new IllegalStateException("STARTMarker found without an ENDMarker for " + nextEntry.key + " in toFile " + file)

            if (existingFrom < 0 && existingEnd >= 0)
                throw new IllegalStateException("ENDMarker found without and STARTMarker for " + nextEntry.key + " in toFile " + file)

            //Removing existing
            if (existingFrom >= 0) {
                for (int i = existingEnd; i >= existingFrom; i--)
                    contentOfFile.remove(i)
            }
            else
                existingFrom = contentOfFile.size() //if not existing, than append to end

            //Add
            Collection<String> serializedValue = new ArrayList<String>()
            nextEntry.serialize(operatingsystem, serializedValue, global)
            serializedValue.add(0, startKey)
            serializedValue.add(endKey)

            for (String nextSerialized : serializedValue.reverse())
                contentOfFile.add(existingFrom, nextSerialized.toString())

        }

        if (fileAsFile)
          fileAsFile.text = contentOfFile.join(NEWLINE)
    }

    @Override
    void deinstall() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    boolean uptodate() {
        return false  //To change body of implemented methods use File | Settings | File Templates.
    }
}
