package org.pike.utils

import org.junit.Assert
import org.junit.Test


class ObjectMergeUtilTest {

    ObjectMergeUtil<ObjectMergeTestClass> merger = new ObjectMergeUtil<ObjectMergeTestClass>()

    @Test
    public void genericWins () {
        ObjectMergeTestClass generic = new ObjectMergeTestClass("generic")
        ObjectMergeTestClass specific = new ObjectMergeTestClass()

        ObjectMergeTestClass merged = merger.merge(generic, specific)
        Assert.assertEquals ("Field wrong", "generic", merged.field)

    }

    @Test
    public void specificWins () {
        ObjectMergeTestClass generic = new ObjectMergeTestClass("generic")
        ObjectMergeTestClass specific = new ObjectMergeTestClass("specific")

        ObjectMergeTestClass merged = merger.merge(generic, specific)
        Assert.assertEquals ("Field wrong", "specific", merged.field)

    }

    @Test
    public void noValue () {
        ObjectMergeTestClass generic = new ObjectMergeTestClass()
        ObjectMergeTestClass specific = new ObjectMergeTestClass()

        ObjectMergeTestClass merged = merger.merge(generic, specific)
        Assert.assertNull("Field must be null", merged.field)
    }

    @Test(expected = IllegalArgumentException)
    public void differentObjects () {
        merger.merge(new StringBuilder(), new Properties())

    }

    @Test
    public void genericObjectNull () {
        ObjectMergeTestClass generic = new ObjectMergeTestClass("hello")
        ObjectMergeTestClass merged = merger.merge(null, generic)
        Assert.assertEquals ("hello", merged.field)

    }

    @Test
    public void specificObjectNull () {
        ObjectMergeTestClass generic = new ObjectMergeTestClass("hello")
        ObjectMergeTestClass merged = merger.merge( generic, null)
        Assert.assertEquals ("hello", merged.field)
    }

    @Test
    public void bothNull () {
        Assert.assertNull (merger.merge( null, null))
    }
}
