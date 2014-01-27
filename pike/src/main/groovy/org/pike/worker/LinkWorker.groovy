package org.pike.worker

import groovy.util.logging.Log
import groovy.util.logging.Slf4j

import java.nio.file.Files
import java.nio.file.Path

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 08.05.13
 * Time: 13:36
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class LinkWorker extends UndoableWorker {
    String from
    String to


    @Override
    void install() {
        log.debug("Linking from " + from + " to " + to)

        Path fromPath = toPath (from)
        log.debug("Resolved from : " + fromPath.toString())

        Path toPath = toPath (to)
        log.debug("Resolved to : " + fromPath.toString())

        Files.deleteIfExists(fromPath)

        log.debug("Linking from " + fromPath.toString() + " to " + toPath.toString());

        Files.createSymbolicLink(fromPath, toPath)

        adaptFileFlags(fromPath.toFile())
    }

    @Override
    void deinstall() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    boolean uptodate() {
        log.debug("Check link " + toPath (to).toFile().absolutePath )
        Path from = toPath (from)
        Path to = toPath (to)

        if (! from.toFile().exists())
            return false

        if (! Files.isSymbolicLink(from))
            return false

        Path linkTo = Files.readSymbolicLink(from)
        return linkTo.toFile().absolutePath.equals(to.toFile().absolutePath)
    }

    public String getDetailInfo () {
        String detailinfo = super.getDetailInfo()
        detailinfo += "    - from         : " + toPath (from) + NEWLINE
        detailinfo += "    - to           : " + toPath (to) + NEWLINE
        return detailinfo
    }
}
