package org.pike.tasks

import com.google.common.io.Files
import org.junit.Assert
import org.junit.Test
import org.pike.configuration.Configuration
import org.pike.configuration.Formatter

class EclipseConfigurationTest {

    @Test(expected = IllegalStateException)
    public void formatterWithoutName () {
        Formatter formatter = new Formatter()
        EclipseConfiguration eclipseConfiguration = new EclipseConfiguration()
        eclipseConfiguration.getFormatterXml(formatter)
    }

    @Test
    public void formatter () {
        Formatter formatter = new Formatter()
        formatter.name("MyFormatter")
        formatter.tabWidth(7)
        formatter.spacesForTabs(true)
        formatter.indent(8)
        formatter.lineSplit (80)
        EclipseConfiguration eclipseConfiguration = new EclipseConfiguration()
        String formatterString = eclipseConfiguration.getFormatterXml(formatter)
        println formatterString
        Assert.assertTrue("Header incorrect", formatterString.startsWith("""<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<profiles version="17">
<profile kind="CodeFormatterProfile" name="MyFormatter" version="17">
"""))
        Assert.assertTrue("Header incorrect", formatterString.endsWith("""</profile>
</profiles>"""))

        Assert.assertTrue ("TabChar invalid", formatterString.contains('<setting id="org.eclipse.jdt.core.formatter.tabulation.char" value="space"/>'))
        Assert.assertTrue ("TabSize invalid", formatterString.contains('<setting id="org.eclipse.jdt.core.formatter.indentation.size" value="7"/>'))
    }

    @Test
    public void formatter2 () {
        Formatter formatter = new Formatter()
        formatter.name("MyFormatter")
        formatter.tabWidth(2)
        EclipseConfiguration eclipseConfiguration = new EclipseConfiguration()
        String formatterString = eclipseConfiguration.getFormatterXml(formatter)
        println formatterString
        Assert.assertTrue("Header incorrect", formatterString.startsWith("""<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<profiles version="17">
<profile kind="CodeFormatterProfile" name="MyFormatter" version="17">
"""))
        Assert.assertTrue("Header incorrect", formatterString.endsWith("""</profile>
</profiles>"""))

        Assert.assertTrue ("TabChar invalid", formatterString.contains('<setting id="org.eclipse.jdt.core.formatter.tabulation.char" value="tab"/>'))
        Assert.assertTrue ("TabSize invalid", formatterString.contains('<setting id="org.eclipse.jdt.core.formatter.indentation.size" value="2"/>'))
    }

    @Test
    public void eclipseConfiguration () {
        File rootPath = Files.createTempDir()
        File globalConfigPath = new File (rootPath, "global")
        File workspaceConfigPath = new File (rootPath, "workspace")
        File projectConfigPath = new File (rootPath, "project")

        Configuration configuration = new Configuration()
        configuration.encoding 'ISO-8859-15'
        configuration.showLineNumbers Boolean.TRUE
        configuration.showMemory Boolean.TRUE
        configuration.disableAutomaticXmlValidation()
        configuration.sonarqubeUrl("http://sonarqube.pike.org")

        Formatter formatter = new Formatter()
        formatter.name 'MyFormatter'
        formatter.tabWidth 2
        formatter.spacesForTabs Boolean.TRUE
        configuration.formatter = formatter

        EclipseConfiguration eclipseConfiguration = new EclipseConfiguration()
        eclipseConfiguration.apply(null, globalConfigPath, workspaceConfigPath, projectConfigPath, configuration, false)
        println rootPath.absolutePath
    }

    @Test(expected = IllegalStateException)
    public void overlappingDifferentInSameFile () {
        Configuration configuration1 = new Configuration()
        configuration1.encoding = "ISO-8859-15"

        Configuration configuration2 = new Configuration()
        configuration2.encoding = "UTF-8"

        EclipseConfiguration eclipseConfiguration = new EclipseConfiguration()
        eclipseConfiguration.check(Arrays.asList(configuration1, configuration2))
    }

    @Test
    public void overlappingSameInSameFile () {
        Configuration configuration1 = new Configuration()
        configuration1.encoding = "ISO-8859-15"

        Configuration configuration2 = new Configuration()
        configuration2.encoding = "ISO-8859-15"

        EclipseConfiguration eclipseConfiguration = new EclipseConfiguration()
        eclipseConfiguration.check(Arrays.asList(configuration1, configuration2))
    }

    @Test
    public void nonOverlappingCheckBoolean () {
        Configuration configuration1 = new Configuration()
        configuration1.showMemory true

        Configuration configuration2 = new Configuration()

        EclipseConfiguration eclipseConfiguration = new EclipseConfiguration()
        eclipseConfiguration.check(Arrays.asList(configuration1, configuration2))
    }

    @Test
    public void overlappingCheckBoolean () {
        Configuration configuration1 = new Configuration()
        configuration1.showMemory true

        Configuration configuration2 = new Configuration()
        configuration2.showMemory true

        EclipseConfiguration eclipseConfiguration = new EclipseConfiguration()
        eclipseConfiguration.check(Arrays.asList(configuration1, configuration2))
    }
}
