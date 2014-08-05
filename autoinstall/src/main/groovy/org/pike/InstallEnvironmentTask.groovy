package org.pike

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.WindowsProvider

/**
 * Created by OleyMa on 19.02.14.
 */
class InstallEnvironmentTask extends DefaultTask{

    File toDir
    Operatingsystem operatingsystem

    @TaskAction
    public void configure () {
        loadFromClasspath(toDir, "/installation/bootstrapbuild.gradle", false)

        if (operatingsystem.provider instanceof WindowsProvider) {
            loadFromClasspath(toDir, "/installation/configureHost.bat", true)
            loadFromClasspath(toDir, "/installation/unzip.exe", false)
        } else {
            loadFromClasspath(toDir, "/installation/configureHost", false)
        }
    }

    /**
     * load file with url from classpath and copies to
     * @param toFile toFile to be injected to
     * @param url url to be injected
     * @return toFile to be injected
     */
    private File loadFromClasspath (final File toFile, String url, final boolean adaptLineDelimiters) {

        String name = url.substring(url.lastIndexOf("/") + 1)

        InputStream inputstream = getClass().getResourceAsStream(url)
        if (inputstream == null)
            throw new IllegalStateException("Cannot find $url on classpath.")

        File injectFile = new File (toFile, name)
        FileOutputStream fos = new FileOutputStream(injectFile)

        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = inputstream.read(bytes)) != -1) {
            fos.write(bytes, 0, read);
        }

        if (adaptLineDelimiters) {
            WindowsProvider provider = new WindowsProvider()
            provider.adaptLineDelimiters(injectFile, injectFile)
        }

        return injectFile

    }
}
