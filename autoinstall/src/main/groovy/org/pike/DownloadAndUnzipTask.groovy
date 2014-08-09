package org.pike

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.utils.ZipUtils
import org.pike.worker.DownloadWorker

import java.nio.file.Files

/**
 * Created by OleyMa on 31.07.14.
 */
@Slf4j
abstract class DownloadAndUnzipTask extends DefaultTask {

    //TODO replace when workers are tasks themselfs
    String from
    File to
    Operatingsystem os
    String simplifyTo
    ZipUtils ziputils = new ZipUtils()


    File simplifiedFile



    public void from (final String from) {
        this.from = from
        log.info("from " + from)

    }

    public void to (final File to) {
        this.to = to
        log.info("to " + to.absolutePath)
    }



    private File getParent (final File rootpath, final String nameLike) {
        for (File next: rootpath.listFiles()) {
            if (next.name.contains(nameLike)) return next
        }
        throw new IllegalStateException("No file like " + nameLike + " found in path " + rootpath.absolutePath)
    }

    @TaskAction
    public void downloadAndUnzip () {

        if (to == null)
            throw new IllegalStateException("to not set")

        if (from == null)
            throw new IllegalStateException("from not set")

        DownloadWorker worker = new DownloadWorker()
        worker.from = from
        worker.toPath = to
        worker.operatingsystem = os
        worker.project = project
        worker.install()

        //TODO migrate to downloadWorker as simplifyStrategie
        Collection<File> rootpaths = ziputils.getRootpaths(to, worker.cacheFile)
        if (rootpaths.size() != 1)
            throw new IllegalStateException("Zipfile " + worker.cacheFile.absolutePath + " contains not 1, but ${rootpaths.size()} rootpaths")

        simplifiedFile = new File (to, simplifyTo)

        log.info ("Simplified file exists: " + simplifiedFile + "-" + simplifiedFile.exists())

        if (simplifiedFile.exists()) {
            log.info("Simplified file $simplifiedFile.absolutePath exists, deleting directory")
            simplifiedFile.deleteDir()
        }

        log.info ("Simplified file exists: " + simplifiedFile + "-" + simplifiedFile.exists())

        Files.move(rootpaths.iterator().next().toPath(), simplifiedFile.toPath())


    }
}
