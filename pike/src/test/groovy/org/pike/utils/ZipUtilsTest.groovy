package org.pike.utils

import org.junit.Assert
import org.junit.Test
import org.pike.test.TestUtils

/**
 * Created by OleyMa on 08.05.14.
 */
class ZipUtilsTest {

    ZipUtils ziputils = new ZipUtils()

    @Test
    public void checkRootpaths () {

        final String rootpath = '/hallo'
        final File rootpathAsFile = new File (rootpath)

        File dummyZip = TestUtils.projectfile("pike", "src/test/resources/testzip.zip")

        Collection<File> rootpaths = ziputils.getRootpaths(rootpathAsFile, dummyZip)

        Assert.assertTrue (rootpaths.contains(new File ("/hallo/rootpath1")))
        Assert.assertTrue (rootpaths.contains(new File ("/hallo/rootpath2")))
        Assert.assertTrue (rootpaths.contains(new File ("/hallo/rootpath3")))
        Assert.assertFalse (rootpaths.contains(new File ("/hallo/rootpath4")))
    }
}
