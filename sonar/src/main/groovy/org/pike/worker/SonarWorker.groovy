package org.pike.worker

import org.pike.env.IEnvEntry
import org.pike.env.PropertyEntry

import java.nio.file.Path

/**
 * Created by OleyMa on 06.05.14.
 */
class SonarWorker extends CombinedWorker {

    String url

    File toPath
    Collection <String> plugins = new ArrayList<>()

    Collection <IEnvEntry> entries = new ArrayList<IEnvEntry>()


    public void to (final String to) {
        this.toPath = toFile(to)
    }

    /**
     * configure the sonarworker
     */
    void configure () {
        if (url == null)
            throw new IllegalStateException("You have to set a url of the sonar jar, like url='http://dist.sonar.codehaus.org/sonarqube-4.3.zip'")

        if (toPath == null)
            throw new IllegalStateException("You have to set a to of the sonar jar'")

        //String local = url.substring(url.lastIndexOf("/") + 1)
        String linked = 'sonar'

        DownloadWorker downloadWorker = new DownloadWorker()
        downloadWorker.from = url
        downloadWorker.toPath = toPath
        downloadWorker.user = user
        downloadWorker.group = group
        downloadWorker.executable(linked + "/bin/linux-x86-64/sonar.sh")
        downloadWorker.executable(linked + "/bin/linux-x86-64/wrapper")
        workers.add(downloadWorker)

        LinkWorker linkWorker = new LinkWorker()
        linkWorker.toPath = new File (toPath, directoryName(url))
        linkWorker.fromPath = new File (toPath, linked)
        workers.add(linkWorker)

        File pluginsPath = new File (linkWorker.fromPath, 'extensions/plugins')

        plugins.each {
            DownloadWorker nextPluginWorker = new DownloadWorker()
            nextPluginWorker.from = it
            nextPluginWorker.toPath = pluginsPath
            nextPluginWorker.user = user
            nextPluginWorker.group = group
            workers.add(nextPluginWorker)
        }

        UserenvWorker userEnv = new UserenvWorker()
        userEnv.configure(this)
        userEnv.file(new File (linkWorker.fromPath, '/conf/sonar.properties').absolutePath)
        userEnv.entries = entries

        workers.add(userEnv)
    }


/**
     * configures a property to the toFile
     * @param key       key of property
     * @param value     value of property
     */
    public void property(final String key, final String value, String divider = null) {
        entries.add(new PropertyEntry(key, value, false, divider))
    }

    public void plugin (String url) {
        if (! url.contains("http://"))
            url = 'http://repository.codehaus.org/org/codehaus/sonar-plugins/' + url
        plugins.add(url)
    }


}
