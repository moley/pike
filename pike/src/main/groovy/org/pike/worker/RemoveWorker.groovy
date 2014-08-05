package org.pike.worker

import groovy.util.logging.Slf4j

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 04.09.13
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class RemoveWorker extends PikeWorker{

    private Collection<File> files = new ArrayList<File>()

    public void file(final String file) {
        files.add(new File(file))
    }

    @Override
    void install() {
        for (File next: files) {
            if (next.exists()) {
                boolean removed = next.delete()
                log.info("Remove file " + next.absolutePath + " - removed: " + removed)
            }
            else
              log.warn("File " + next.absolutePath + " doesn't exist, can't remove it")
        }
    }


    @Override
    boolean uptodate() {
        return false  //To change body of implemented methods use File | Settings | File Templates.
    }
}
