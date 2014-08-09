package org.pike.remoting

import org.junit.Assert
import org.junit.Test
import org.pike.os.IOperatingsystemProvider
import org.pike.os.LinuxProvider
import org.pike.os.WindowsProvider

/**
 * Created by OleyMa on 08.08.14.
 */
class CommandBuilderTest {

    @Test
    void root () {
        IOperatingsystemProvider osProvider = new LinuxProvider()
        CommandBuilder builder = new CommandBuilder().asUser('root').onOperatingSystem(osProvider)
        builder.addCommand(osProvider.bootstrapCommandRemovePath, '/opt/pike')
        builder.addCommand(osProvider.bootstrapCommandMakePath, '/opt/pike')
        Assert.assertEquals ('rm -rf /opt/pike;mkdir -p /opt/pike', builder.get())
    }

    @Test
    void user () {
        IOperatingsystemProvider osProvider = new LinuxProvider()
        CommandBuilder builder = new CommandBuilder().asUser('user').onOperatingSystem(osProvider)
        builder.addCommand(osProvider.bootstrapCommandRemovePath, '/opt/pike')
        builder.addCommand(osProvider.bootstrapCommandMakePath, '/opt/pike')
        Assert.assertEquals ('sudo rm -rf /opt/pike;sudo mkdir -p /opt/pike', builder.get())
    }

    @Test
    void windows () {
        IOperatingsystemProvider osProvider = new WindowsProvider()
        CommandBuilder builder = new CommandBuilder().asUser('root').onOperatingSystem(osProvider)
        builder.addCommand(osProvider.bootstrapCommandRemovePath, '/opt/pike')
        builder.addCommand(osProvider.bootstrapCommandMakePath, '/opt/pike')
        Assert.assertEquals ('cmd /c rmdir /opt/pike /s /q & cmd /c mkdir /opt/pike', builder.get())

    }
}
