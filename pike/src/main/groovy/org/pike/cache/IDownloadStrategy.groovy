package org.pike.cache

/**
 * Created by OleyMa on 17.09.14.
 */
public interface IDownloadStrategy {

    /**
     * check sif download strategy is active
     * @param url   url defines strategy
     * @return true: is active, false: is not active
     */
    public boolean isActive (final String url)

    /**
     * checks if the url was updated since last check
     * @param url url
     * @param cacheFile downloaded cache file
     */
    public boolean isUptodate (final URL url, final File cacheFile)

    /**
     * downloads the given url to cache file
     * @param url   url
     * @param cacheFile  downloaded cache file
     */
    public void download (final URL url, final File cacheFile)
}
