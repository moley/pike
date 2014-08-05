package org.pike.autoinstall

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.pike.cache.CacheManager
import org.pike.model.defaults.Defaults
import org.pike.model.host.Host
import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.IOperatingsystemProvider
import org.pike.os.WindowsProvider
import org.pike.remoting.IRemoting
import org.pike.remoting.LocalRemoting
import org.pike.remoting.SshRemoting
import org.pike.worker.PikeWorker

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 13.05.13
 * Time: 22:08
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class AutoinstallWorker extends PikeWorker {

    private CacheManager cachemanager = new CacheManager()

    private File injectDir = new File ("build/injectToInstallation")


    /**
     * copies url to local build folder toFile
     * @param toFile toFile to be injected to
     * @param url url to be injected
     * @return toFile to be injected
     */
    public File inject (final File toFile, String url, final boolean adaptLineDelimiters) {

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

    public File getLocalCacheDir () {
        return new File ("build/cache").getAbsoluteFile()
    }
    public String getPikeDirRemote (final Host host) {
     log.info("determine pike dir remote " + host.operatingsystem.pikedir)
     String pikeDir = host.operatingsystem.pikedir
      if (pikeDir == null)
        throw new IllegalStateException("PikeDir on host " + host.hostname + " not set")
      return pikeDir
    }

    public String getTmpDirRemote (final Host host) {
      IOperatingsystemProvider osProvider = host.operatingsystem.provider
      String pikedir = getPikeDirRemote(host)
      String tmpDir = osProvider.addPath(pikedir, "tmp")
      return tmpDir
    }

    public void initializePaths (final Project project, Host host) {
        log.debug("initialize paths on host " + host.name)

        IRemoting remoting = host.remotingImpl

        String pikeDir = getPikeDirRemote(host)

        Operatingsystem os = host.operatingsystem
        IOperatingsystemProvider osProvider = os.provider

        String command = ""
        String pluginsDir = osProvider.addPath(pikeDir, "plugins")
        String descriptionsDir = osProvider.addPath(pikeDir, "descriptions")
        String tmpDir = osProvider.addPath(pikeDir, "tmp")

        command = addCommand(osProvider, command, osProvider.bootstrapCommandRemovePath, pikeDir)

        command = addCommand(osProvider, command, osProvider.bootstrapCommandMakePath, pluginsDir)
        command = addCommand(osProvider, command, osProvider.bootstrapCommandMakePath, descriptionsDir)
        command = addCommand(osProvider, command, osProvider.bootstrapCommandMakePath, tmpDir)

        log.debug("Complete command " + command)

        remoting.execCmd(command)

    }

    public String addCommand (final IOperatingsystemProvider provider, String command, final String unresolved, final String param) {

        if (provider == null)
            throw new IllegalStateException("No operatingsystem provider defined")

        if (unresolved == null)
            throw new IllegalStateException("No unresolved path defined")

        String resolved = unresolved
        if (param != null)
          resolved = unresolved.replace("PARAM0", param)

        resolved = resolved.replaceAll("//", provider.fileSeparator)

        if (! command.isEmpty())
            command += provider.commandSeparator

        log.debug("resolve <" + unresolved + "> with param <" + param + "> to <" + resolved + ">")

        command += resolved
        return command

    }

    public void uploadBootstrapScripts (final Project project, Host host, final PropertyChangeProgressLogging progressLogging) {
        progressLogging.start("Uploading bootstrap scripts to host ${host.name}")
        IRemoting remoting = host.remotingImpl

        //Inject boostrap Scripts vom Plugin pike
        if (! injectDir.exists())
            injectDir.mkdirs()

        String pikeDirRemote = getPikeDirRemote(host)

        progressLogging.progressLogger.progress("Upload bootstrap scripts to host  ${host.name}")
        remoting.upload(pikeDirRemote, inject(injectDir, "/installation/installPike.sh", false), progressLogging)
        remoting.upload(pikeDirRemote, inject(injectDir, "/installation/installPike.bat", true), progressLogging)
        remoting.upload(pikeDirRemote, inject(injectDir, "/installation/configureHost", false), progressLogging)
        remoting.upload(pikeDirRemote, inject(injectDir, "/installation/configureHost.bat", true), progressLogging)
        remoting.upload(pikeDirRemote, inject(injectDir, "/installation/bootstrapbuild.gradle", false), progressLogging)
        remoting.upload(pikeDirRemote, inject(injectDir, "/installation/unzip.exe", false), progressLogging)
    }

    public void uploadPlugins (final Project project, final Host host, final PropertyChangeProgressLogging progressLogging) {
        log.debug ("upload plugins to host " + host.name)
        IRemoting remoting = host.remotingImpl

        String pikeDirRemote = getPikeDirRemote(host)

        IOperatingsystemProvider osProvider = host.operatingsystem.provider
        String pikeDirPlugins = osProvider.addPath(pikeDirRemote, "plugins")

        //Install plugins, which are used from current project
        for (File nextClasspathEntry: project.buildscript.configurations.classpath) {
            remoting.upload(pikeDirPlugins, nextClasspathEntry, progressLogging)
        }
    }



    public void uploadProjectDescriptions (final Project project, Host host, final PropertyChangeProgressLogging progressLogging) {
        progressLogging.start("Uploading project descriptions to host ${host.name}")
        IRemoting remoting = host.remotingImpl

        IOperatingsystemProvider provider = host.operatingsystem.provider
        String pikeDirDescs = provider.addPath(getPikeDirRemote(host), "descriptions")

        File pikeGradle =  project.file('pike.gradle')
        if (! pikeGradle.exists())
            throw new IllegalStateException(pikeGradle.absolutePath + " does not exist")


        remoting.upload(pikeDirDescs, pikeGradle, progressLogging)
    }

    public void disconnectConnections (final Collection<Host> hosts) {
        for (Host next: hosts) {
            next.remotingImpl.disconnect()
            next.remotingImpl = null
        }
    }



    @Override
    void install() {  //TODO make the official one

    }

    @Override
    boolean uptodate() {
        return false
    }
}
