package org.pike.cache

import groovy.util.logging.Slf4j

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 02.05.13
 * Time: 14:23
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class DummyCacheManager extends CacheManager {

    public DummyCacheManager () {
    }

    @Override
    public File getCacheFile (final String url, final boolean overwrite) {
       File file = new File (url)
       if (! file.exists())
         throw new IllegalStateException("Url ${url} does not exist")

        return file
    }
}
