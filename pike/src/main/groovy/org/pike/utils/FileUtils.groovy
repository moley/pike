package org.pike.utils

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 04.09.13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
class FileUtils {


    public void copyFile (File source, final File target) throws IOException  {

        if (! target.getParentFile().exists())
            if (! target.getParentFile().mkdirs())
                throw new IllegalStateException("Could not create path " + target.getParent());

        InputStream inStream = null;
        OutputStream outStream = null;

        try{
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);

            byte[] buf = new byte[1024];
            int len;
            while ((len = inStream.read(buf)) > 0){
                outStream.write(buf, 0, len);
            }
            inStream.close();
            outStream.close();
            System.out.println("File copied.");
        }
        catch(IOException ex){
            if (inStream != null)
            inStream.close();

            if (outStream != null)
                outStream.close();
            throw ex;
        }

    }
}
