package org.pike.worker

import org.pike.env.IEnvEntry
import org.pike.env.PropertyEntry
import org.pike.model.host.Host

/**
 * Created by OleyMa on 06.05.14.
 */
class SonarWorker extends CombinedWorker {

    String url

    File toPath
    Collection <String> plugins = new ArrayList<>()

    Collection <IEnvEntry> entries = new ArrayList<IEnvEntry>()

    String downloadUrlPlugins = 'http://repository.codehaus.org/org/codehaus/sonar-plugins/'

    public void url (final String url) {
        this.url = url
    }

    public void to (final String to) {
        this.toPath = toFile(to)
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
        plugins.add(url)
    }

    /**
     * configure the sonarworker
     */
    void configure (final Host host) {
        super.configure(host)
        if (url == null)
            throw new IllegalStateException("You have to set a url of the sonar jar, like url='http://dist.sonar.codehaus.org/sonarqube-4.3.zip'")

        if (toPath == null)
            throw new IllegalStateException("You have to set a to of the sonar jar'")

        String linked = 'sonar'

        DownloadWorker downloadWorker = project.task(name + "_download", type:DownloadWorker)
        downloadWorker.configure(this)
        downloadWorker.from = url
        downloadWorker.toPath = toPath
        downloadWorker.fsUser = fsUser
        downloadWorker.fsGroup = fsGroup
        //downloadWorker.executable(linked + "/bin/linux-x86-64/sonar.sh")
        //downloadWorker.executable(linked + "/bin/linux-x86-64/wrapper")

        LinkWorker linkWorker = project.task(name + "_link", type:LinkWorker)
        linkWorker.configure (this)
        linkWorker.toPath = new File (toPath, directoryName(url))
        linkWorker.fromPath = new File (toPath, linked)
        linkWorker.dependsOn downloadWorker

        File pluginsPath = new File (linkWorker.fromPath, 'extensions/plugins')

        UserenvWorker userEnv = project.task(name + "_userenv", type:UserenvWorker)
        userEnv.configure(this)
        userEnv.file(new File (linkWorker.fromPath, '/conf/sonar.properties').absolutePath)
        userEnv.entries = entries
        userEnv.dependsOn linkWorker

        plugins.each {

            String nextPlugin = it

            if (! url.contains("http://"))
                url = downloadUrlPlugins + url

            String simplePluginName = nextPlugin.substring(nextPlugin.lastIndexOf("/") + 1)
            DownloadWorker nextPluginWorker = project.task(name + "_plugin_" + simplePluginName, type:DownloadWorker)
            nextPluginWorker.configure (this)
            nextPluginWorker.from = nextPlugin
            nextPluginWorker.toPath = pluginsPath
            nextPluginWorker.fsUser = fsUser
            nextPluginWorker.fsGroup = fsGroup
            nextPluginWorker.dependsOn userEnv
            this.dependsOn nextPluginWorker
        }


    }




    @Override
    void install() {

    }

    @Override
    boolean uptodate() {
        return false
    }
}
