package org.pike.tasks

import org.gradle.api.tasks.TaskAction
import org.pike.configuration.Configuration
import org.pike.configuration.Eclipse
import org.pike.configuration.Module

class InstallEclipseTask extends PikeTask {

    {
        description = 'Installs and configures an eclipse instance for the given project'
    }


    @TaskAction
    public void prepareEclipse() {

        getLogger().info("prepare eclipse ide")

        Eclipse eclipse = pikeExtension.eclipse

        project.oomphIde.repoEclipseLatest()
        for (String nextRepo : eclipse.repos) {
            project.oomphIde.repo nextRepo
        }

        String javahomeBin = new File (System.getenv("JAVA_HOME"), 'bin').absolutePath
        logger.info("Set java home to " + javahomeBin)
        //set java vm path
        project.oomphIde.eclipseIni {
            set '-vm', javahomeBin
        }


        //Install features
        for (String nextFeature : eclipse.features) {
            project.oomphIde.feature nextFeature
        }


        //Import projects
        //TODO think about importing projects at once, but they have to be built before, .project must exist
        /**for (Module module: pikeExtension.git.modules) {
            Configuration mergedConfiguration = pikeExtension.getMergedConfiguration(module.configuration)
            File basepath = project.file(mergedConfiguration.basepath)
            File clonePath = new File(basepath, module.name)
            logger.info("Importing project " + clonePath.absolutePath)
            project.oomphIde.addProjectFolder(clonePath)
        }**/

        boolean proxyConfigured = System.getProperty("http.proxyHost") != null || System.getProperty("https.proxyHost") != null
        project.logger.info("Proxy configured: " + proxyConfigured)

        if (proxyConfigured) {
            File proxyConf = project.file('build/pike/proxy.ini')
            proxyConf.parentFile.mkdirs()

            proxyConf.text = """org.eclipse.core.net/proxyData/HTTP/host=${System.getProperty("http.proxyHost")}
org.eclipse.core.net/proxyData/HTTPS/host=${System.getProperty("https.proxyHost")}
org.eclipse.core.net/proxyData/HTTPS/hasAuth=false
org.eclipse.core.net/proxyData/HTTP/port=${System.getProperty("http.proxyPort")}
org.eclipse.core.net/proxyData/HTTPS/port=${System.getProperty("https.proxyPort")}
org.eclipse.core.net/org.eclipse.core.net.hasMigrated=true
org.eclipse.core.net/nonProxiedHosts=${System.getProperty("http.nonProxyHosts")}
org.eclipse.core.net/systemProxiesEnabled=false
org.eclipse.core.net/proxyData/HTTP/hasAuth=false
"""

            project.logger.info( "Configured proxy with " + proxyConf.text)

            project.oomphIde.p2director {
                addArg('plugincustomization', proxyConf.absolutePath)
            }
        }

        project.oomphIde {
            jdt {}
            style {
                classicTheme()
                niceText()        // nice fonts and visible whitespace
                lineNumbers true
            }
        }

    }
}
