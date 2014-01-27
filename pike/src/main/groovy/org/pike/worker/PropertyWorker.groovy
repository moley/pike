package org.pike.worker

import groovy.util.logging.Log
import groovy.util.logging.Slf4j
import org.pike.utils.SequencedProperties

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 08.05.13
 * Time: 13:37
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class PropertyWorker extends UndoableWorker {

    String file

    SequencedProperties addProperties = new SequencedProperties()
    Set<String> removeProperties = new HashSet<>()

    /**
     * add the parameterized key/value pair to the
     * @param key
     * @param value
     */
    public void add(final String key, final value) {
        log.debug("Add property " + key + "->" + value)
        addProperties.put(key, value)
    }

    /**
     * adds the parameterized key/value pair to the toFile
     * and adds this value to the toPath also
     * @param key
     * @param value
     * @param addToPath
     */
    public void add(final String key, final value, final boolean addToPath) {
        log.debug("Add property " + key + "->" + value + "(Add to toPath = " + addToPath + ")")
        addProperties.put(key, value)
        if (addToPath)
            addProperties.put("PATH", '$' + key + ':$PATH')

    }

    /**
     * remove the parameterized property key
     * @param key the ey to be removed
     */
    public void remove (final String key) {
        removeProperties.add(key)
    }

    @Override
    void install() {

        File propFile = toFile(file)

        log.debug("Handle propertyfile " + propFile.absolutePath)

        if (! propFile.exists()) {

            if (addProperties.isEmpty())
                return

            propFile.getParentFile().mkdirs()
            propFile.createNewFile()
        }

        Properties readProps = new SequencedProperties()
        readProps.load(new FileInputStream(propFile))

        for (Object nextToAdd : addProperties.keySet()) {
            Object value = addProperties.get(nextToAdd)
            log.debug("Using key value " + nextToAdd + "->" + value)
            readProps.put(nextToAdd.toString(), value.toString())
        }

        for (String nextToRemove : removeProperties)
            readProps.remove(nextToRemove)

        readProps.store(new FileOutputStream(propFile), null)
    }

    @Override
    void deinstall() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    boolean uptodate() {
        return false  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getDetailInfo () {
        String detailinfo = super.getDetailInfo()

        detailinfo += "    - toFile         : " + toFile(file) + NEWLINE

        for (String key : addProperties.keySet()) {
            String value = addProperties.getProperty(key)
            detailinfo +=  "    - addProp      : $key = $value $NEWLINE"
        }

        return detailinfo
    }
}
