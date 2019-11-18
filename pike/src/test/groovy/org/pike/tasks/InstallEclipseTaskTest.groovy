package org.pike.tasks


import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.pike.PikePlugin

class InstallEclipseTaskTest {

    final String proxyHostSaved = System.getProperty('http.proxyHost')
    final String proxyPortSaved = System.getProperty('http.proxyPort')
    final String nonProxyHosts = System.getProperty('http.nonProxyHosts')

    @After
    public void after () {
        System.setProperty("http.proxyHost", proxyHostSaved)
        System.setProperty("https.proxyHost", proxyHostSaved)
        System.setProperty("http.proxyPort", proxyPortSaved)
        System.setProperty("https.proxyPort", proxyPortSaved)
        System.setProperty("http.nonProxyHosts", nonProxyHosts)
    }

    @Test
    public void proxy () {

        System.setProperty("http.proxyHost", "my.proxy")
        System.setProperty("https.proxyHost", "my.proxy")
        System.setProperty("http.proxyPort", "80")
        System.setProperty("https.proxyPort", "80")
        System.setProperty("http.nonProxyHosts", "noproxy")

        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            eclipse {
                repo 'https://download.eclipse.org/releases/2019-09/'
                feature 'org.eclipse.egit'
                feature 'org.eclipse.buildship'
            }
        }

        InstallEclipseTask installEclipseTask = project.tasks.installEclipse
        installEclipseTask.prepareEclipse()
        String text = project.file('build/pike/proxy.ini').text
        Assert.assertEquals ("Proxyini Content invalid", """org.eclipse.core.net/proxyData/HTTP/host=my.proxy
org.eclipse.core.net/proxyData/HTTPS/host=my.proxy
org.eclipse.core.net/proxyData/HTTPS/hasAuth=false
org.eclipse.core.net/proxyData/HTTP/port=80
org.eclipse.core.net/proxyData/HTTPS/port=80
org.eclipse.core.net/org.eclipse.core.net.hasMigrated=true
org.eclipse.core.net/nonProxiedHosts=noproxy
org.eclipse.core.net/systemProxiesEnabled=false
org.eclipse.core.net/proxyData/HTTP/hasAuth=false
""", text)

    }

    @Test
    public void task () {

        System.clearProperty("http.proxyHost")
        System.clearProperty("https.proxyHost")
        System.clearProperty("http.proxyPort")
        System.clearProperty("https.proxyPort")
        System.clearProperty("http.nonProxyHosts")

        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(PikePlugin)
        project.pike {
            eclipse {
                repo 'https://download.eclipse.org/releases/2019-09/'
                feature 'org.eclipse.egit'
                feature 'org.eclipse.buildship'
                xmx '2G'
            }
        }

        InstallEclipseTask installEclipseTask = project.tasks.installEclipse
        installEclipseTask.prepareEclipse()
        Assert.assertFalse ("Proxyini does exist", project.file('build/pike/proxy.ini').exists())

        //the state of OomphIdeExtension is writeOnly, so we have no assertions here

    }
}
