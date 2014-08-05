package org.pike.worker

import groovy.io.FileType
import groovy.util.logging.Slf4j
import org.pike.os.IOperatingsystemProvider
import org.pike.os.WindowsProvider
import org.pike.utils.FileUtils
import org.pike.utils.ZipUtils

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 08.05.13
 * Time: 12:10
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class DownloadWorker extends PikeWorker {

    String from
    File toPath
    boolean overwrite = false
    boolean adaptLineDelimiters = false


    Collection <String> executables = new ArrayList<String>()

    FileUtils fileutils = new FileUtils()

    ZipUtils ziputils = new ZipUtils()

    File downloadedFile

    /**
     * define files to make executable
     * @param file
     */
    public void executable (String file) {
        executables.add(file)
    }

    /**
     * everything should be executable
     */
    public void executable () {
        fileFlags = "755";
    }

    public void to (String to) {
        this.toPath = toFile(to)
    }

    public void from (String from) {
        this.from = from
    }
    @Override
    public void install() {

        if (log.debugEnabled)
          log.debug("Installing from " + from + " to " + toPath.absolutePath + " (Overwrite: " + overwrite + ", Adapt linedelimiters: " + adaptLineDelimiters + ")")

        downloadedFile = cacheManager.getCacheFile(operatingsystem, from, overwrite)


        if (! toPath.exists()) {
            toPath.mkdirs()
            if (log.debugEnabled)
              log.debug("Make dir " + toPath.absolutePath)
        }

        if  (downloadedFile.name.toLowerCase().endsWith(".zip")) {

            if (log.debugEnabled)
                log.debug("Unzip " + downloadedFile.absolutePath + " to " + toPath.absolutePath)
            def ant = new AntBuilder()   // create an antbuilder
            ant.unzip(src: downloadedFile.absolutePath,
                    dest: toPath.absolutePath,
                    overwrite: true)

            for (File next : ziputils.getRootpaths(toPath, downloadedFile)) {
                next.eachFileRecurse(FileType.DIRECTORIES) {
                    if(it.name == 'bin') {
                        if (log.infoEnabled)
                            log.info("Adapting file flags for detected binpath $it.absolutePath")
                        adaptFileFlags(it, user, group, 'a+x')
                    }
                }

                if (log.infoEnabled)
                    log.info("Adapting file flags for next rootpath $next")
                adaptFileFlags(next, user, group, fileFlags)
            }

          if (adaptLineDelimiters)
              throw new IllegalStateException("The feature adaptLineDelimiters is currently not supported for zipfile")
        }
        else {
            String [] nameTokens = from.split("/")
            File toFile = new File(toPath, nameTokens[nameTokens.length - 1])
            if (log.debugEnabled)
              log.debug("Copy " + downloadedFile.absolutePath + " to " + toFile.absolutePath)
            fileutils.copyFile(downloadedFile, toFile)
            adaptFileFlags(toFile, user, group, fileFlags)

            if (adaptLineDelimiters) {
                IOperatingsystemProvider osProvider = operatingsystem.provider
                osProvider.adaptLineDelimiters(toFile, toFile)
            }
        }


        if (! (operatingsystem.provider instanceof WindowsProvider)) {

          for (String nextExecutable : executables) {
              File next = new File (toPath, nextExecutable)
              adaptFileFlags(next, user, group, 'a+x')
          }
        }



    }



    @Override
    public boolean uptodate() {
        File downloadedFile = cacheManager.getCacheFile(operatingsystem, from)

        if  (downloadedFile.name.toUpperCase().endsWith(".ZIP")) {
          Collection <File> files = ziputils.unzippedFiles(downloadedFile, toPath)
          File firstFile = files.iterator().next()
          boolean firstFileExists = firstFile.exists()
          if (firstFileExists && log.isInfoEnabled())
            log.info("Worker $name not uptodate due to file ${firstFile.absolutePath} does not exist")
          return firstFileExists
        }

        //Check if files has changed
        log.info("Worker $name not uptodate due to TODO ")
        return false
    }

    public String getDetailInfo () {
        String detailinfo = super.getDetailInfo()
        detailinfo += "    - from         : " + from + NEWLINE
        detailinfo += "    - to           : " + toFile(to).absolutePath + NEWLINE
        return detailinfo
    }
}
