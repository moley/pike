package org.pike.utils

import groovy.util.logging.Slf4j

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 04.09.13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class FileUtils {


    public void copyFile (File source, final File target) throws IOException  {

        if (! target.parentFile.exists()) {
            if (!target.parentFile.mkdirs())
                throw new IllegalStateException("Could not create path " + target.parent);
            else if (log.debugEnabled)
              log.debug("Create path " + target.parent)
        }

        InputStream inStream = null
        OutputStream outStream = null

        try{
            inStream = new FileInputStream(source)
            outStream = new FileOutputStream(target)

            byte[] buf = new byte[1024]
            int len;
            while ((len = inStream.read(buf)) > 0){
                outStream.write(buf, 0, len)
            }
            inStream.close()
            outStream.close()
            if (log.debugEnabled)
                log.debug("File copied from ${source} to ${target}")
        }
        catch(IOException ex){
            if (inStream != null)
            inStream.close()

            if (outStream != null)
                outStream.close()
            throw ex;
        }

    }
}
