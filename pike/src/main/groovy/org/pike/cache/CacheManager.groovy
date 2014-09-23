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

    public static File cacheDir //TODO handle over DownloadStrategies, remove DummyZeuchs

    public static Proxy proxy = Proxy.NO_PROXY


    static {
        //TODO make generic
        try {
            if (InetAddress.getByName('proxy.vsa.de').isReachable(1000)) {
                log.info('Enable proxy')
                System.properties.putAll(["http.proxyHost": "proxy.vsa.de", "http.proxyPort": "8080", "http.nonProxyHosts": "*vsa.de"])
                //proxy = new Proxy(Proxy.Type.HTTP, new InetAddress()SocketAddress() {})
            }
            else {
                proxy = Proxy.NO_PROXY
                log.info('Disable proxy')
            }
        } catch (UnknownHostException e) {
            log.info('Disable proxy')
            proxy = Proxy.NO_PROXY
        }
    }

    private Set<IDownloadStrategy> downloadStrategies = new HashSet<IDownloadStrategy>()

    public CacheManager (final File cacheDir) {
        this.cacheDir = cacheDir
        if (cacheDir == null) {
            File userHome = new File (System.getProperty("user.home"))
            File pikeHome = new File (userHome, ".pike")
            this.cacheDir = new File (pikeHome, 'cache')
        }

        downloadStrategies.add(new HttpDownloadStrategy())
        downloadStrategies.add(new FileDownloadStrategy())
    }

    public CacheManager () {
        this (null)
    }

    private File getCacheDir () {
        return cacheDir
    }

    private IDownloadStrategy getStrategy (final String url) {

        for (IDownloadStrategy nextStrat: downloadStrategies) {
            if (nextStrat.isActive(url))
                return nextStrat
        }

        throw new IllegalStateException("No download strategy available for url ${url}")

    }


    public DownloadInfo download(final String url, final boolean overwrite) {

        DownloadInfo downloadInfo = new DownloadInfo()

        File toDir = getCacheDir()

        if (toDir == null)
            throw new IllegalStateException("You did not installPike a cachedir nor a tempdir for your host, I don't know were to save downloaded artefact to")

        File downloadedFile = getCacheFile(url)
        try {


            IDownloadStrategy strategy = getStrategy(url)

            URL downloadUrl = new URL(url)
            File cacheFile = getCacheFile(url)

            if (overwrite || !strategy.isUptodate(downloadUrl, cacheFile)) {
              if (downloadedFile.exists()) {
                  if (overwrite)
                      log.info("Overwriting ${downloadUrl.toExternalForm()}, removing cached file ${downloadedFile.absolutePath}")
                  else
                      log.info("Content of URL ${downloadUrl.toExternalForm()} has changed, removing cached file ${downloadedFile.absolutePath}")

                  if (downloadedFile.delete() == false)
                      throw new IllegalStateException("Could not delete cachefile ${downloadedFile.absolutePath}")
              }
            }

            log.info("Downloading ${downloadUrl.toExternalForm()} to ${downloadedFile.absolutePath}")

            if (! downloadedFile.exists())
                strategy.download(downloadUrl, cacheFile)
            else
              downloadInfo.fromCache = true

            downloadInfo.cacheFile = cacheFile

        } catch (Exception e) {
            throw new GradleException("Error get cached file from $url", e)
        }


        return downloadInfo
    }

    public File getCacheFile (final String url) {
        return new File (getCacheDir(), url.toUpperCase().replace("/", "").replace(":",""))
    }
}
