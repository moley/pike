package org.pike.env

import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.WindowsProvider

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 14.05.13
 * Time: 23:40
 * To change this template use File | Settings | File Templates.
 */
class IncludeEnvEntry implements IEnvEntry{

    File includeFile
    boolean toDevNull

    public IncludeEnvEntry (final File includeFile, boolean toDevNull) {
      this.includeFile = includeFile
      this.toDevNull = toDevNull
    }

    @Override
    void serialize(Operatingsystem operatingsystem, Collection<String> serializedValue, final boolean global) {

        if (operatingsystem.provider instanceof WindowsProvider)
            throw new IllegalStateException("Include is not yet implemented in windows")

        String includeLine = getIncludeLine()
        if (toDevNull)
            includeLine += " >/dev/null"

        serializedValue.add(includeLine)
    }

    private String getIncludeLine () {
        return  "source $includeFile.absolutePath"
    }

    @Override
    String getPikeKey() {
        return "INCLUDE" + includeFile.absolutePath
    }

    @Override
    boolean isOriginEntry(Operatingsystem operatingsystem, String originEntry) {
        return originEntry.contains(getIncludeLine())
    }
}
