package org.pike.cache


import org.apache.commons.io.FileUtils

/**
 * Created by OleyMa on 23.09.14.
 */
class FileDownloadStrategy implements IDownloadStrategy{
    @Override
    boolean isActive(String url) {
        return url.startsWith('file://')
    }

    @Override
    boolean isUptodate(URL url, File cacheFile) {
        return false
    }

    @Override
    void download(URL url, File cacheFile) {
        File file = new File (url.toString().replace('file:/', '/'))
        if (! file.exists())
            throw new IllegalStateException("Url ${file.absolutePath} does not exist")

        FileUtils.copyFile(file, cacheFile)


    }
}
