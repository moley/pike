package org.pike.cache

import groovy.util.logging.Log4j
import groovy.util.logging.Slf4j

/**
 * Created by OleyMa on 17.09.14.
 */
@Slf4j
class HttpDownloadStrategy implements IDownloadStrategy {
    @Override
    boolean isActive(String url) {
        return url.startsWith("http")
    }

    @Override
    boolean isUptodate(URL url, File cacheFile) {
        return cacheFile.exists()
        /** TODO uptodatecheck
        URLConnection connection = url.openConnection(CacheManager.proxy)
        if (connection.contentLength < 0)
            throw new IllegalStateException("Could not connect to ${url.toExternalForm()}")
        boolean isUptodate = connection.lastModified < cacheFile.lastModified()
        log.info("Check if url ${url} is uptodate: ${isUptodate}")**/
    }

    @Override
    void download(URL url, File cacheFile) {
        log.info("Downloading ${url}")

        //Download to cache
        cacheFile.parentFile.mkdirs()

        def file = new FileOutputStream(cacheFile)
        def out = new BufferedOutputStream(file)
        out << url.openStream()
        out.close()

        cacheFile.setLastModified(url.openConnection(CacheManager.proxy).lastModified)

    }
}
