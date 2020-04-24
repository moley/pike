package org.pike.tasks

import org.gradle.api.logging.Logger
import org.pike.configuration.Configuration
import org.pike.configuration.Formatter
import org.pike.configurators.file.FileConfigurator
import org.pike.configurators.file.FileContentConfigurator
import org.pike.configurators.file.XmlConfigurator

class IdeaConfiguration extends CollectingConfiguration {

    /**
     * {@inheritDoc}
     */
    @Override
    void apply(final Logger logger,
               final File globalConfigPath,
               final File workspaceConfigPath,
               final File projectConfigPath,
               Configuration configuration,
               final boolean dryRun) {
        super.apply(logger, globalConfigPath, workspaceConfigPath, projectConfigPath, configuration, dryRun)

        global("options/editor.xml", "/application/component[@name='EditorSettings']/option[@name='ARE_LINE_NUMBERS_SHOWN']", configuration.showLineNumbers, dryRun, XmlConfigurator.class)
        global("options/ui.lnf.xml", "/application/component[@name='UISettings']/option[@name='SHOW_MEMORY_INDICATOR']", configuration.showMemory, dryRun, XmlConfigurator.class)

        project ("encodings.xml", "/project/component[@name='Encoding']/file[@url='PROJECT']->charset", configuration.encoding, dryRun, XmlConfigurator.class)
        project("encodings.xml", "/project/component[@name='Encoding']->defaultCharsetForPropertiesFiles", configuration.encoding, dryRun, XmlConfigurator.class)

        if (configuration.formatter != null) {
            Formatter formatter = configuration.formatter
            project("codeStyles/codeStyleConfig.xml", "component[@name='ProjectCodeStyleConfiguration']/state/option[@name='USE_PER_PROJECT_SETTINGS']", "true", dryRun, XmlConfigurator.class)
            project("codeStyles/Project.xml", null, getFormatterXml(formatter), dryRun, FileContentConfigurator.class)
        }
    }

    void addFormatterXmlSetting (final Collection<String> configurations, final String prefix, final String key, final Object value) {
        if (value != null)
            configurations.add(prefix + '<option name="' + key + '" value="' + value.toString() + '"/>')
    }

    String getFormatterXml (final Formatter formatter) {

        if (formatter.name == null)
            throw new IllegalStateException("A name must be configured for a formatter configuration")

        Collection<String> xmlPerLine = new ArrayList<String>()
        xmlPerLine.add('<component name="ProjectCodeStyleConfiguration">')
        xmlPerLine.add('  <code_scheme name="Project" version="173">')
        if (formatter.lineSplit) {
          xmlPerLine.add('    <option name="RIGHT_MARGIN" value="' + formatter.lineSplit.toString() + '" />')
        }
        xmlPerLine.add('    <codeStyleSettings language="JAVA">')

        xmlPerLine.add('      <indentOptions>')
        addFormatterXmlSetting(xmlPerLine, '         ', 'USE_TAB_CHARACTER', formatter.spacesForTabs ? 'false' : 'true')
        addFormatterXmlSetting(xmlPerLine, '         ', 'TAB_SIZE', formatter.tabWidth)
        addFormatterXmlSetting(xmlPerLine, '         ', 'INDENT_SIZE', formatter.indent)

        xmlPerLine.add('      </indentOptions>')

        xmlPerLine.add('    </codeStyleSettings>')
        xmlPerLine.add('  </code_scheme>')
        xmlPerLine.add('</component>')

        String xmlAsString = String.join('\n', xmlPerLine)
        xmlAsString = xmlAsString.replaceAll("=", "\\=")
        return xmlAsString

    }


    public void global(String file, String key, Object value, boolean dryRun, Class<? extends FileConfigurator> clazz) {
        if (value == null)
            return

        collectConfiguration('workspace', file, key, value)

        if (!dryRun) {
            if (globalConfigPath == null)
                throw new IllegalStateException("GlobalConfigPath not set")
            File configFile = new File(globalConfigPath, file)

            FileConfigurator fileConfigurator = getFileConfigurator(clazz)
            fileConfigurator.configure(logger, configFile, key, value.toString(), dryRun)
        }

    }

    public void project(String file, String key, Object value, boolean dryRun, Class<? extends FileConfigurator> clazz) {
        if (value == null)
            return

        if (!dryRun) {
            if (projectConfigPath == null)
                throw new IllegalStateException("ProjectConfigPath not set")

            File configFile = new File(projectConfigPath, file)

            FileConfigurator configurator = getFileConfigurator(clazz)
            configurator.configure(logger, configFile, key, value.toString(), dryRun)
        }
    }

}
