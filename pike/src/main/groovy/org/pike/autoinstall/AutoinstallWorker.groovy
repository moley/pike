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

    private HashMap <Host, IRemoting> connections = new HashMap<Host, IRemoting>()

    private CacheManager cachemanager = new CacheManager()

    private File injectDir = new File ("build/injectToInstallation")

    static IRemoting injectedRemoting //TODO Mocking


    public IRemoting createRemoting (Project project, Host nextHost) {
      this.project = project

      if (injectedRemoting != null)
        return injectedRemoting
      else
      return new SshRemoting(project, nextHost)
    }

    /**
     * gets remoting instance of the given host
     * @param project       project
     * @param nextHost      host to determine remoting instance
     * @return remoting instance
     */
    public IRemoting getRemoting (Project project, Host nextHost) {
        IRemoting remoting = connections.get(nextHost)
        if (remoting == null || ! remoting.connectedToHost(nextHost)) {
            remoting = createRemoting(project, nextHost)
            if (log.debugEnabled) {
              log.debug("Establish connection to  " + nextHost + "-" + nextHost.operatingsystem)
              log.debug("- Using remote pikedir " + getPikeDirRemote(nextHost))
              log.debug("- Using remote tempdir " + getTmpDirRemote(nextHost))
              log.debug("- Using localcache dir " + getLocalCacheDir())
            }
            connections.put(nextHost, remoting)
        }

        return remoting
    }

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
        return new File ("").getAbsoluteFile()
    }
    public String getPikeDirRemote (final Host host) {
     String pikeDir = host.operatingsystem.pikedir
      if (pikeDir == null)
        throw new IllegalStateException("PikeDir on host " + host.hostname + " not set")
      return pikeDir
    }

    public String getTmpDirRemote (final Host host) {
      IOperatingsystemProvider osProvider = host.operatingsystem.provider
      String tmpDir = osProvider.addPath(host.operatingsystem.pikedir, "tmp")
      return tmpDir
    }

    public void initializePaths (final Project project, Host host) {
        log.debug("initialize paths on host " + host.name)

        IRemoting remoting = getRemoting(project, host)

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
        log.debug("upload bootstrapScripts to host " + host.name)
        IRemoting remoting = getRemoting(project, host)

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
        IRemoting remoting = getRemoting(project, host)

        String pikeDirRemote = getPikeDirRemote(host)

        IOperatingsystemProvider osProvider = host.operatingsystem.provider
        String pikeDirPlugins = osProvider.addPath(pikeDirRemote, "plugins")

        //Install plugins, which are used from current project
        for (File nextClasspathEntry: project.buildscript.configurations.classpath) {
            remoting.upload(pikeDirPlugins, nextClasspathEntry, progressLogging)
        }
    }

    private File getLocalTmpDir () {
        return new File ("/tmp")
    }

    public void uploadProjectDescriptions (final Project project, Host host, final PropertyChangeProgressLogging progressLogging) {
        log.debug ("upload project descriptions to host " + host.name)
        IRemoting remoting = getRemoting(project, host)


        IOperatingsystemProvider provider = host.operatingsystem.provider
        String pikeDirDescs = provider.addPath(getPikeDirRemote(host), "descriptions")

        remoting.upload(pikeDirDescs, new File ("configureHost.gradle"), progressLogging)
    }

    public void uploadGradle (final Project project, Host host, final Defaults defaults, final PropertyChangeProgressLogging progressLogging) {
        log.debug ("upload gradle to " + host.name)

        IRemoting remoting = getRemoting(project, host)

        if (defaults.pikegradle == null)
            throw new IllegalStateException("PikeGradle not set. Please use a valid download link to gradle zip toFile")

        progressLogging.progressLogger.progress("Download bootstrap gradle from ${defaults.pikegradle}")
        File downloadedGradle = cachemanager.getCacheFile(host.operatingsystem, defaults.pikegradle, localTmpDir, localCacheDir)

        log.debug ("- Using gradle " + downloadedGradle.absolutePath)

        IOperatingsystemProvider osProvider = host.operatingsystem.provider
        String uploadedGradle = osProvider.addPath(getTmpDirRemote(host), downloadedGradle.name)
        progressLogging.progressLogger.progress("Upload bootstrap gradle to host  ${host.name}")
        remoting.upload(uploadedGradle, downloadedGradle, progressLogging)
    }

    public void uploadJre (final Project project, Host host, final PropertyChangeProgressLogging progressLogging) {
        log.debug ("upload jre to host " + host.name)

        IRemoting remoting = getRemoting(project, host)

        if (host.operatingsystem.pikejre == null)
            throw new IllegalStateException("PikeJre not set. Please use a valid download link to bootstrap jre zip toFile")

        progressLogging.progressLogger.progress("Download bootstrap jre from ${host.operatingsystem.pikejre}")
        File downloadedJre = cachemanager.getCacheFile(host.operatingsystem, host.operatingsystem.pikejre, localTmpDir, localCacheDir)

        log.debug ("- Using jre " + downloadedJre.absolutePath)
        IOperatingsystemProvider provider = host.operatingsystem.provider
        String uploadedJre = provider.addPath(getTmpDirRemote(host) , downloadedJre.name)
        progressLogging.progressLogger.progress("Upload bootstrap jre to host  ${host.name}")
        remoting.upload(uploadedJre, downloadedJre, progressLogging)
    }

    public void disconnectConnections () {
        for (IRemoting nextRemoting : connections.values()) {
            nextRemoting.disconnect()
        }
    }

    public void installPike(Project project, Host host, PropertyChangeProgressLogging propertyChangeProgressLogging) {
        log.debug ("installPike pike on host " + host.name)
        IRemoting remoting = getRemoting(project, host)

        IOperatingsystemProvider osProvider = host.operatingsystem.provider

        String pikeDir = getPikeDirRemote(host)
        String command = ""

        command = addCommand(osProvider, command, osProvider.bootstrapCommandChangePath, pikeDir)

        command = addCommand(osProvider, command, osProvider.bootstrapCommandInstall, pikeDir)
        log.debug ("Complete command to installPike pike: " + command)
        remoting.execCmd(command)

    }
}
