package org.pike.installers

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.pike.configuration.FileType
import org.pike.utils.ProgressLoggerWrapper


class Download {


    String source

    File toDir

    File cacheDir = new File (System.getProperty("user.home"), ".pike/cache")

    Project project

    boolean force

    File downloadedFile

    File cachedFile

    FileType fileType

    String name


    long processedBytes = 0



    public void executeDownload () {
        if (source == null)
            throw new IllegalStateException("You did not configure a source in download of " + name)
        processedBytes = 0;

        String name = source.substring(source.lastIndexOf("/") + 1)
        name = name.split("\\?")[0]
        if (! toDir.exists())
            toDir.mkdirs()

        String absoluteName = source.replace("/", "#").replace(":", "#")

        if (fileType != null && ! source.endsWith(fileType.suffix))
            name = name + fileType.suffix

        URI uri = URI.create(source)
        URL url = uri.toURL()

        cachedFile = new File (cacheDir, absoluteName)
        downloadedFile = new File (toDir, name)


        project.logger.info("Downloading")
        project.logger.info(" - Name            : " + name)
        project.logger.info(" - Path            : " + toDir.absolutePath)
        project.logger.info(" - Cache           : " + cacheDir.absolutePath)
        project.logger.info(" - Cached file     : " + cachedFile.absolutePath)
        project.logger.info(" - Downloaded file : " + downloadedFile.absolutePath)
        project.logger.info(" - Force           : " + force)

        if (force) {
            cachedFile.delete()
            downloadedFile.delete()
        }

        InputStream inputStream = url.openStream()
        if (!cachedFile.exists()) {
          project.logger.lifecycle("Fill url " + source + " into cache...")
          if (!cachedFile.getParentFile().exists())
              cachedFile.getParentFile().mkdirs()
          stream(url, inputStream, cachedFile)
        }
        else
            project.logger.lifecycle("Cached file exists, reusing downloaded file")

        if (! downloadedFile.exists()) {
            project.logger.lifecycle("Copy file " + downloadedFile.absolutePath + " from cache")
            FileUtils.copyFile(cachedFile, downloadedFile)
        }
        else
            project.logger.info("Downloaded file exists, skip download")


    }

    private static int getFileSize(URL url) {
        URLConnection conn = null;
        try {
            conn = url.openConnection();
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).setRequestMethod("HEAD");
            }
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).disconnect();
            }
        }
    }

    /**
     * Copy bytes from an input stream to a file and log progress
     * @param is the input stream to read
     * @param destFile the file to write to
     * @throws IOException if an I/O error occurs
     */
    private void stream(URL url, InputStream is, File destFile) throws IOException {
        int totalBytes = getFileSize(url)
        String totalBytesAsMB = (int) (totalBytes / (1024 * 1024))
        String urlNormal = url.toString()
        String urlShortened = urlNormal.length() < 30 ? urlNormal : urlNormal.substring(30) + "..."
        String progressLoggerPrefix = "Downloading " + urlShortened + " "
        ProgressLoggerWrapper progressLoggerWrapper = new ProgressLoggerWrapper(project,progressLoggerPrefix)

        try {

            boolean finished = false;
            try {
                OutputStream os = new FileOutputStream(destFile)
                byte[] buf = new byte[1024 * 10];
                int read;
                while ((read = is.read(buf)) >= 0) {
                    os.write(buf, 0, read);
                    processedBytes += read;
                    String processedBytesAsMB = (int) (processedBytes / (1024* 1024))

                    int percent = (processedBytes * 100) / totalBytes

                    String progressInfo = progressLoggerPrefix + "(" + percent + " %, " + processedBytesAsMB + " of " + totalBytesAsMB + " MB)"
                    progressLoggerWrapper.progress(progressInfo)
                }

                os.flush();
                finished = true;
            } catch (Exception e) {
                project.getLogger().error(e.getLocalizedMessage(), e)
            } finally {
                if (!finished) {
                    println "File " + destFile.absolutePath + " is removed due to error while downloading"
                    destFile.delete();
                }
            }
        } finally {
            is.close();
            progressLoggerWrapper.end()
        }
    }


}
