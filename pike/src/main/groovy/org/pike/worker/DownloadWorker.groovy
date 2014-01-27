package org.pike.worker

import groovy.util.logging.Log
import groovy.util.logging.Slf4j
import org.pike.cache.CacheManager
import org.pike.os.IOperatingsystemProvider
import org.pike.os.WindowsProvider
import org.pike.utils.FileUtils

import java.util.zip.ZipFile

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 08.05.13
 * Time: 12:10
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class DownloadWorker extends UndoableWorker {

    String from
    String to
    boolean overwrite = false
    boolean adaptLineDelimiters = false


    Collection <String> executables = new ArrayList<String>()

    CacheManager cacheManager = new CacheManager()

    FileUtils fileutils = new FileUtils()

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
    @Override
    public void install() {

        log.debug("Installing from " + from + " to " + to + " (Overwrite: " + overwrite + ", Adapt linedelimiters: " + adaptLineDelimiters + ")")

        File downloadedFile = cacheManager.getCacheFile(operatingsystem, from, tempDir, cacheDir, overwrite)

        File toPath = toFile(to)
        if (! toPath.exists()) {
            toPath.mkdirs()
            log.debug("Make dir " + toPath.absolutePath)
        }

        if  (downloadedFile.name.toLowerCase().endsWith(".zip")) {
          log.debug("Unzip " + downloadedFile.absolutePath + " to " + toPath.absolutePath)
          def ant = new AntBuilder()   // create an antbuilder
          ant.unzip(  src: downloadedFile.absolutePath,
                dest: toPath.absolutePath ,
                overwrite:true )
          adaptFileFlags(toPath)

          if (adaptLineDelimiters)
              throw new IllegalStateException("The feature adaptLineDelimiters is currently not supported for zipfile")
        }
        else {
            String [] nameTokens = from.split("/")
            File toFile = new File(toPath, nameTokens[nameTokens.length - 1])
            log.debug("Copy " + downloadedFile.absolutePath + " to " + toFile.absolutePath)
            fileutils.copyFile(downloadedFile, toFile)
            adaptFileFlags(toFile)

            if (adaptLineDelimiters) {
                IOperatingsystemProvider osProvider = operatingsystem.provider
                osProvider.adaptLineDelimiters(toFile, toFile)
            }
        }


        if (! (operatingsystem.provider instanceof WindowsProvider)) {
          log.debug("adaptFileFlags in os " + operatingsystem.provider)
          for (String nextExecutable : executables) {
            File executableFile = new File (toPath, nextExecutable)
            if (toFile(executableFile.absolutePath).exists()) {
              String excommand = "chmod -R a+x " + executableFile.absolutePath
              log.debug(excommand)
              Runtime.getRuntime().exec(excommand) //TODO Libmethod for Handling external returncode
            }
            else
              log.warn("File " + executableFile.absolutePath + " not available, cannot adaptFileFlags")
          }
        }



    }

    final Collection<File> unzippedFiles (final File zipfile, final File parentPath) {

        Set <File> allRoots = new HashSet<File>()
        ZipFile file = new ZipFile(zipfile)
        file.entries().each { entry ->
            String rootPath = entry.name.substring(0, entry.name.indexOf("/"))
            allRoots.add(new File (parentPath, rootPath))
        }

        return allRoots

    }

    @Override
    public void deinstall() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean uptodate() {
        File downloadedFile = cacheManager.getCacheFile(operatingsystem, from, tempDir, cacheDir)

        if  (downloadedFile.getName().endsWith(".zip")) {
          Collection <File> files = unzippedFiles(downloadedFile, toFile(to))
          return files.iterator().next().exists()
        }

        //Check if files has changed
        return false
    }

    public String getDetailInfo () {
        String detailinfo = super.getDetailInfo()
        detailinfo += "    - from         : " + from + NEWLINE
        detailinfo += "    - to           : " + toFile(to).absolutePath + NEWLINE
        return detailinfo
    }
}
