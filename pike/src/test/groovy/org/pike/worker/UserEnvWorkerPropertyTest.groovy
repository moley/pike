package org.pike.worker

import com.google.common.io.Files
import org.junit.After
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.pike.model.defaults.Defaults
import org.pike.model.operatingsystem.Operatingsystem

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 20.09.13
 * Time: 23:09
 * To change this template use File | Settings | File Templates.
 */
class UserEnvWorkerPropertyTest {

    File file = new File ("tmp/hallo")

    @After
    public void after () {
        if (file.exists())
            Assert.assertTrue (file.delete())
    }

    @Test
    public void testIgnoring () {
        File file = new File (Files.createTempDir(), 'hallo')
        file.text = '''# pike    BEGIN (ALIASl)
alias l='ls -lh\'
# pike    END (ALIASl)
# pike    BEGIN (PROPERTY PS1)
PS1="\\u@\\h:\\w # "
# pike    END (PROPERTY PS1)
# pike    BEGIN (DEFAULTPATH /sbin)
export PATH=/sbin:$PATH
# pike    END (DEFAULTPATH /sbin)
# pike    BEGIN (DEFAULTPATH /usr/sbin)
export PATH=/usr/sbin:$PATH
# pike    END (DEFAULTPATH /usr/sbin)
# pike    BEGIN (PATH GROOVY_HOME)
export GROOVY_HOME=/home/nightly/jenkins/tools/groovy
export PATH=$GROOVY_HOME/bin:$PATH
# pike    END (PATH GROOVY_HOME)
# pike    BEGIN (PATH JDK5_HOME)
export JDK5_HOME=/home/nightly/jenkins/tools/jdk1.5.0_20
# pike    END (PATH JDK5_HOME)
# pike    BEGIN (PATH JDK5PATCHED_HOME)
export JDK5PATCHED_HOME=/home/nightly/jenkins/tools/jdk1.5.0_20patched
# pike    END (PATH JDK5PATCHED_HOME)
# pike    BEGIN (PATH JDK6_HOME)
export JDK6_HOME=/home/nightly/jenkins/tools/jdk1.6.0_45
# pike    END (PATH JDK6_HOME)
# pike    BEGIN (PATH JDK7_HOME)
export JDK7_HOME=/home/nightly/jenkins/tools/jdk1.7.0_40
# pike    END (PATH JDK7_HOME)
# pike    BEGIN (PROPERTY ENVIRONMENT)
export ENVIRONMENT=jump
# pike    END (PROPERTY ENVIRONMENT)
# pike    BEGIN (PROPERTY CVSROOT)
export CVSROOT=:pserver:nightly@cvs.intra.vsa.de:/work/cvsdata
# pike    END (PROPERTY CVSROOT)
# pike    BEGIN (PROPERTY CVS_RSH)
export CVS_RSH=ssh
# pike    END (PROPERTY CVS_RSH)
# pike    BEGIN (PROPERTY SWARM_USER)
export SWARM_USER=nightly
# pike    END (PROPERTY SWARM_USER)
# pike    BEGIN (PROPERTY HOME)
export HOME=/home/nightly
'''

        UserenvWorker worker = new UserenvWorker()
        worker.operatingsystem = new Operatingsystem("linux")
        worker.defaults = new Defaults()
        worker.file = file.absolutePath
        worker.path("JDK5_HOME", "hans")
        worker.install()
        println (file.text)
    }

    @Test
    /**
     */
    public void testAllChapter () {
        File file = new File (Files.createTempDir(), 'hallo')
        file.text = '[chapter1]\n' +
                    '  max_allowed_packet=3M\n'+
                    '[chapter2]\n' +
                    '  max_allowed_packet=3M\n'

        UserenvWorker worker = new UserenvWorker()
        worker.operatingsystem = new Operatingsystem("linux")
        worker.defaults = new Defaults()
        worker.file = file.absolutePath
        worker.property("max_allowed_packet", "32M")
        worker.install()

        file = worker.toFile(worker.file)
        String text = file.text
        println (text)

        String content = "[chapter1]\n" +
                         "#   max_allowed_packet=3M\n" +
                         "# pike    BEGIN (PROPERTY max_allowed_packet)\n" +
                         "max_allowed_packet=32M\n" +
                         "# pike    END (PROPERTY max_allowed_packet)\n" +
                         "[chapter2]\n" +
                         "#   max_allowed_packet=3M\n" +
                         "# pike    BEGIN (PROPERTY max_allowed_packet)\n" +
                         "max_allowed_packet=32M\n" +
                         "# pike    END (PROPERTY max_allowed_packet)\n"
        Assert.assertEquals(content, text)

    }

    @Test
    /**
     */
    public void testUpdateOneChapter () {
        File file = new File (Files.createTempDir(), 'hallo')
        file.text = '[chapter1]\n' +
                '  max_allowed_packet=3M\n'+
                '[chapter2]\n' +
                '  max_allowed_packet=3M'

        UserenvWorker worker = new UserenvWorker()
        worker.operatingsystem = new Operatingsystem("linux")
        worker.defaults = new Defaults()
        worker.file = file.absolutePath
        worker.chapter = 'chapter1'
        worker.property("max_allowed_packet", "32M")
        worker.install()

        file = worker.toFile(worker.file)
        String text = file.text
        println (text)

        String content = "[chapter1]\n" +
                "#   max_allowed_packet=3M\n" +
                "# pike    BEGIN (PROPERTY max_allowed_packet)\n" +
                "max_allowed_packet=32M\n" +
                "# pike    END (PROPERTY max_allowed_packet)\n" +
                "[chapter2]\n" +
                "  max_allowed_packet=3M\n"
        Assert.assertEquals(content, text)

    }




    @Test
    public void testLinuxExistingProperty () {
        File file = new File (Files.createTempDir(), 'hallo')
        file.text = 'http_proxy=old_value'

        UserenvWorker worker = new UserenvWorker()
        worker.operatingsystem = new Operatingsystem("linux")
        worker.defaults = new Defaults()
        worker.file = file.absolutePath
        worker.property("http_proxy", "proxy.mycompany.de")
        worker.property("https_proxy", "proxy.mycompany.de")
        worker.install()

        file = worker.toFile(worker.file)
        String text = file.text
        println (text)

        String content = "# http_proxy=old_value\n" +
                "# pike    BEGIN (PROPERTY http_proxy)\n" +
                "http_proxy=proxy.mycompany.de\n" +
                "# pike    END (PROPERTY http_proxy)\n" +
                "# pike    BEGIN (PROPERTY https_proxy)\n" +
                "https_proxy=proxy.mycompany.de\n" +
                "# pike    END (PROPERTY https_proxy)\n"
        Assert.assertEquals(content, text)

    }



    @Test
    public void testLinux () {

        UserenvWorker worker = new UserenvWorker()
        worker.operatingsystem = new Operatingsystem("linux")
        worker.defaults = new Defaults()
        worker.file = "tmp/hallo"
        worker.property("http_proxy", "proxy.mycompany.de", ":")
        worker.property("https_proxy", "proxy.mycompany.de")
        worker.install()

        File file = worker.toFile(worker.file)
        String text = file.text
        println (text)

        String content = "# pike    BEGIN (PROPERTY http_proxy)\n" +
                "http_proxy:proxy.mycompany.de\n" +
                "# pike    END (PROPERTY http_proxy)\n" +
                "# pike    BEGIN (PROPERTY https_proxy)\n" +
                "https_proxy=proxy.mycompany.de\n" +
                "# pike    END (PROPERTY https_proxy)\n"
        Assert.assertEquals(content, text)

    }
}
