import org.junit.Assert
import org.junit.Test
import org.pike.os.IOperatingsystemProvider
import org.pike.os.Linux64Provider
import org.pike.test.TestUtils
import org.pike.worker.properties.Chapter
import org.pike.worker.properties.Entry
import org.pike.worker.properties.Propertyfile

import java.nio.file.Files

/**
 * Created by OleyMa on 08.05.14.
 */
class PropertyfileTest {

    @Test(expected = IllegalStateException)
    public void nullError () {
        new Propertyfile(osProvider, null)

    }

    @Test
    public void chapteredPropertiesfile () {
        File dummyZip = Files.createTempFile(null, null).toFile()
        dummyZip.text = '''[chapter1]
max_allowed_packet=3M
[chapter2]
max_allowed_packet=3M'''
        Propertyfile propFile = new Propertyfile(osProvider, dummyZip)

        println (propFile.toString())

        Assert.assertEquals (2, propFile.chapters.size())
        Chapter chapter1 = propFile.chapters.toArray() [0]
        Assert.assertFalse (chapter1.defaultChapter)
        Assert.assertFalse (chapter1.empty)
        Assert.assertEquals ("chapter1", chapter1.chaptername)

        Chapter chapter2 = propFile.chapters.toArray() [1]
        Assert.assertFalse (chapter2.defaultChapter)
        Assert.assertFalse (chapter2.empty)
        Assert.assertEquals ("chapter2", chapter2.chaptername)
    }

    @Test
    public void regularPropertiesfile () {
        File dummyZip = Files.createTempFile(null, null).toFile()
        dummyZip.text = '''max_allowed_packet=3M
max_allowed_packet2 = 3M'''
        Propertyfile propFile = new Propertyfile(osProvider, dummyZip)
        Assert.assertEquals (1, propFile.chapters.size())
        Chapter defaultChapter = propFile.chapters.toArray() [0]
        Assert.assertTrue (defaultChapter.defaultChapter)
        Assert.assertFalse (defaultChapter.empty)

    }

    @Test
    public void pikedPropertiesfile () {
        File dummyZip = Files.createTempFile(null, null).toFile()
        dummyZip.text = '''#max_allowed_packet=3M
#pike    BEGIN (max_allowed_packet)
max_allowed_packet=32M
#pike    END (max_allowed_packet)
'''
        Propertyfile propFile = new Propertyfile(osProvider, dummyZip)
        Assert.assertEquals (1, propFile.chapters.size())
        Chapter defaultChapter = propFile.chapters.toArray() [0]
        Assert.assertTrue (defaultChapter.defaultChapter)
        Assert.assertFalse (defaultChapter.empty)

        println ("toString: " + propFile.toString())

        Entry entry1 = defaultChapter.entries.get(0)
        Assert.assertNotNull (entry1.content)

    }

    private IOperatingsystemProvider getOsProvider () {
        return new Linux64Provider()
    }
}
