package org.pike.cache

import com.google.common.io.Files
import org.junit.Assert
import org.junit.Test

/**
 * Created by OleyMa on 17.09.14.
 */
class CacheManagerTest {


    @Test
    public void testDownloadNotOverride () {
        CacheManager manager = new CacheManager(Files.createTempDir())
        String url = "http://www.google.de"
        manager.download(url, true)
        Assert.assertTrue (manager.download(url, false).fromCache)
    }


    @Test
    public void testDownloadOverride () {
        CacheManager manager = new CacheManager(Files.createTempDir())
        String url = "http://www.google.de"
        manager.download(url, true)
        Assert.assertFalse (manager.download(url, true).fromCache)
    }
}
