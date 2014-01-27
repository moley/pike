package org.pike.tasks

import org.pike.cache.CacheManager
import org.pike.model.operatingsystem.Operatingsystem

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 02.05.13
 * Time: 14:23
 * To change this template use File | Settings | File Templates.
 */
class DummyCacheManager extends CacheManager {

    private File dummyFile

    public DummyCacheManager (final File dummyfile) {
        this.dummyFile = dummyfile
    }

    public File getCacheFile (final Operatingsystem os, final String url, final File tmpDir, final File cacheDir, final boolean overwrite) {
      return dummyFile
    }
}
