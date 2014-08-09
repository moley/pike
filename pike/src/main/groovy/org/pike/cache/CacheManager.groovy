package org.pike.cache

import groovy.util.logging.Slf4j
import org.gradle.api.GradleException
import org.pike.model.operatingsystem.Operatingsystem

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 26.04.13
 * Time: 11:52
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class CacheManager {

    static File cacheDir


    public File getCacheFile (final Operatingsystem os, final String url) {
        return getCacheFile(url, false)
    }

    private File getCacheDir () {
        if (cacheDir == null) {
            File userHome = new File (System.getProperty("user.home"))
            File pikeHome = new File (userHome, ".pike")
            cacheDir = new File (pikeHome, 'cache')
        }

        return cacheDir
    }


    public File getCacheFile (final String url, final boolean overwrite) {


        //TODO make generic
        System.properties.putAll( ["http.proxyHost":"proxy.vsa.de", "http.proxyPort":"8080", "http.nonProxyHosts" : "*vsa.de"] )


        File toDir = getCacheDir()

        if (toDir == null)
            throw new IllegalStateException("You did not installPike a cachedir nor a tempdir for your host, I don't know were to save downloaded artefact to")

        File downloadedFile = getCacheFile(url)
        try {

            if (overwrite && downloadedFile.exists())
                downloadedFile.delete()


            if (downloadedFile.exists()) {
                if (log.debugEnabled)
                    log.debug("Cachefile " + downloadedFile.absolutePath + " exists")
                if (downloadedFile.length() > 0) {
                    if (log.debugEnabled)
                        log.debug("Cachefile " + downloadedFile.absolutePath + " has length > 0")
                    return downloadedFile
                }
            } else
                println("Downloading file $downloadedFile.absolutePath")

            //Download to cache
            downloadedFile.parentFile.mkdirs()

            def file = new FileOutputStream(downloadedFile)
            def out = new BufferedOutputStream(file)
            out << new URL(url).openStream()
            out.close()

        } catch (Exception e) {
            throw new GradleException("Error get cached file from $url", e)
        }

        return downloadedFile
    }

    public File getCacheFile (final String url) {
        return new File (getCacheDir(), url.toUpperCase().replace("/", "").replace(":",""))
    }
}
