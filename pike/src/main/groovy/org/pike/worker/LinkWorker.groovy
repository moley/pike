package org.pike.worker

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
class LinkWorker extends PikeWorker {
    File fromPath
    File toPath

    void from (final String path) {
        fromPath= toFile(path)
    }

    void to (final String path) {
      toPath = toFile(path)
    }


    @Override
    void install() {


        Files.deleteIfExists(fromPath.toPath())

        log.info("Linking from " + fromPath.absolutePath + " to " + toPath.absolutePath)

        Files.createSymbolicLink(fromPath.toPath(), toPath.toPath())

        adaptFileFlags(fromPath, fsUser, group, ordinaryFileFlag)
    }

    @Override
    boolean uptodate() {


        if (! fromPath.exists())
            return false

        if (! Files.isSymbolicLink(fromPath.toPath()))
            return false

        Path linkTo = Files.readSymbolicLink(fromPath.toPath())
        boolean updated = linkTo.toFile().absolutePath.equals(toPath.absolutePath)

        log.info("Check link " + toPath.absolutePath  + "- updated ${updated}")

        return updated
    }

    public String getDetailInfo () {
        String detailinfo = super.getDetailInfo()
        detailinfo += "    - from         : " + fromPath.absolutePath + NEWLINE
        detailinfo += "    - to           : " + toPath.absolutePath + NEWLINE
        return detailinfo
    }
}
