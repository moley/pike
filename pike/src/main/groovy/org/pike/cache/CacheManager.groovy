package org.pike.cache

import org.pike.model.operatingsystem.Operatingsystem

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 26.04.13
 * Time: 11:52
 * To change this template use File | Settings | File Templates.
 */
class CacheManager {

    public File getCacheFile (final Operatingsystem os, final String url, final File tmpDir, final File cacheDir) {
        return getCacheFile(os, url, tmpDir, cacheDir, false)
    }


    public File getCacheFile (final Operatingsystem os, final String url, final File tmpDir, final File cacheDir, final boolean overwrite) {

        //TODO make generic
        System.properties.putAll( ["http.proxyHost":"proxy.vsa.de", "http.proxyPort":"8080", "http.nonProxyHosts" : "*vsa.de"] )


        File toDir = cacheDir != null ? cacheDir : tmpDir

        if (toDir == null)
            throw new IllegalStateException("You did not installPike a cachedir nor a tempdir for your host, I don't know were to save downloaded artefact to")

        File downloadedFile = toDir != null ? getCacheFile(toDir, url) : tmpDir

        if (overwrite && downloadedFile.exists())
          downloadedFile.delete()


        if (downloadedFile.exists()) {
            println ("Cachefile " + downloadedFile.absolutePath + " exists")
            if (downloadedFile.length() > 0) {
                println ("Cachefile " + downloadedFile.absolutePath + " has length > 0")
                return downloadedFile
            }
        }

        //Download to cache
        if (! downloadedFile.parentFile.exists())
          downloadedFile.parentFile.mkdirs()

        def file = new FileOutputStream(downloadedFile)
        def out = new BufferedOutputStream(file)
        out << new URL(url).openStream()
        out.close()


        return downloadedFile
    }

    public File getCacheFile (final File cachedir, final String url) {
        if (cachedir == null)
            return null

        return new File (cachedir, url.toUpperCase().replace("/", "").replace(":",""))
    }
}
