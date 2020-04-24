package org.pike.configuration

import org.junit.Assert
import org.junit.Test

class FileTypeTest {

    @Test
    public void suffix () {
        Assert.assertEquals (".zip", FileType.ZIP.suffix)

    }
}
