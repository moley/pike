package org.pike.tasks

import org.gradle.api.logging.Logger
import org.pike.configuration.Configuration
import org.pike.configuration.Formatter
import org.pike.configurators.file.FileConfigurator
import org.pike.configurators.file.PropertiesConfigurator

class EclipseConfiguration extends CollectingConfiguration {

    public final static String FILE_RESOURCES_PREFS = '.metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.core.resources.prefs'
    public final static String FILE_UI_EDITORS_PREFS = '.metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.ui.editors.prefs'
    public final static String FILE_UI_PREFS = '.metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.ui.prefs'
    public final static String JDT_UI_PREFS = '.metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.jdt.ui.prefs'
    public final static String KEY_ENCODING = 'encoding'
    public final static String KEY_SPACES_FOR_TABS = 'spacesForTabs'
    public final static String KEY_TAB_WIDTH = 'tabWidth'
    public final static String KEY_SHOW_MEMORY = 'SHOW_MEMORY_MONITOR'
    public final static String KEY_FORMATTER_NAME = "formatter_profile"
    public final static String KEY_FORMATTER_SETTINGS_VERSION = "formatter_settings_version"
    public final static String KEY_FORMATTER_PROFILES_VERSION = "org.eclipse.jdt.ui.formatterprofiles.version"
    public final static String KEY_FORMATTER_PROFILES = "org.eclipse.jdt.ui.formatterprofiles"
    public final static String KEY_PRINTMARGIN = "printMargin"
    public final static String KEY_PRINTMARGIN_COLUMN = "printMarginColumn"


    /**
     * {@inheritDoc}
     */
    public void apply(final Logger logger,
                      final File globalConfigPath,
                      final File workspaceConfigPath,
                      final File projectConfigPath,
                      Configuration configuration,
                      final boolean dryRun) {
        super.apply(logger, globalConfigPath, workspaceConfigPath, projectConfigPath, configuration, dryRun)

        workspace(FILE_RESOURCES_PREFS, KEY_ENCODING, configuration.encoding, dryRun, PropertiesConfigurator.class)
        workspace(FILE_UI_PREFS, KEY_SHOW_MEMORY, configuration.showMemory, dryRun, PropertiesConfigurator.class)

        if (configuration.formatter != null) {
            Formatter formatter = configuration.formatter
            workspace(JDT_UI_PREFS, KEY_FORMATTER_NAME, "_" + formatter.name, dryRun, PropertiesConfigurator.class)
            workspace(JDT_UI_PREFS, KEY_FORMATTER_SETTINGS_VERSION, '17', dryRun, PropertiesConfigurator.class)
            workspace(JDT_UI_PREFS, KEY_FORMATTER_PROFILES, getFormatterXml(formatter), dryRun, PropertiesConfigurator.class)
            workspace(JDT_UI_PREFS, KEY_FORMATTER_PROFILES_VERSION, '17', dryRun, PropertiesConfigurator.class)
            if (formatter.lineSplit) {
                workspace(FILE_UI_EDITORS_PREFS, KEY_PRINTMARGIN, Boolean.TRUE.toString(), dryRun, PropertiesConfigurator.class)
                workspace(FILE_UI_EDITORS_PREFS, KEY_PRINTMARGIN_COLUMN, formatter.lineSplit, dryRun, PropertiesConfigurator.class)
            }
        }

        //TODO Formatter
        //TODO compare ignore whitespace
        //TODO Disable automatic XML validation
        //TODO Sonarqube / SonarLint
        //TODO Proxy
        //TODO linenumbers
    }

    Collection<String> getDefaultFormatter () {
        String defaultFormatterString = getClass().getResourceAsStream("/eclipse_formatter.xml").text
        return defaultFormatterString.split("\n")
    }

    void addFormatterXmlSetting (final Collection<String> configurations, final String key, final Object value) {
        if (value != null)
          configurations.add('<setting id="' + key + '" value="' + value.toString() + '"/>')
    }

    String getFormatterXml (final Formatter formatter) {

        if (formatter.name == null)
            throw new IllegalStateException("A name must be configured for a formatter configuration")

        Collection<String> xmlPerLine = new ArrayList<String>()
        xmlPerLine.add('<?xml version="1.0" encoding="UTF-8" standalone="no"?>')
        xmlPerLine.add('<profiles version="17">')
        xmlPerLine.add('<profile kind="CodeFormatterProfile" name="' + formatter.name + '" version="17">')

        addFormatterXmlSetting(xmlPerLine, 'org.eclipse.jdt.core.formatter.tabulation.char', formatter.spacesForTabs ? 'space' : 'tab')
        addFormatterXmlSetting(xmlPerLine, 'org.eclipse.jdt.core.formatter.tabulation.size', formatter.indent)
        addFormatterXmlSetting(xmlPerLine, 'org.eclipse.jdt.core.formatter.indentation.size', formatter.tabWidth)
        addFormatterXmlSetting(xmlPerLine, 'org.eclipse.jdt.core.formatter.lineSplit', formatter.lineSplit)

        xmlPerLine.add("</profile>")
        xmlPerLine.add("</profiles>")


        String xmlAsString = String.join('\n', xmlPerLine)
        xmlAsString = xmlAsString.replaceAll("=", "\\=")
        return xmlAsString

    }


    private void workspace(String file, String key, Object value, boolean dryRun, Class<? extends FileConfigurator> clazz) {

        if (value == null)
            return

        collectConfiguration('workspace', file, key, value)

        if (!dryRun) {
            if (workspaceConfigPath == null)
                throw new IllegalStateException("Workspace ConfigPath not set")

            File configFile = new File(workspaceConfigPath, file)
            FileConfigurator configurator = getFileConfigurator(clazz)
            configurator.configure(logger, configFile, key, value.toString(), dryRun)
        }

    }

    public void project(String file, String key, Object value, boolean dryRun, Class<? extends FileConfigurator> clazz) {
        if (value == null)
            return

        if (!dryRun) {
            if (projectConfigPaths == null )
                throw new IllegalStateException("Project Config Paths not set")

            File configFile = new File(projectConfigPath, file)

            FileConfigurator configurator = getFileConfigurator(clazz)
            configurator.configure(logger, configFile, key, value.toString(), dryRun)
        }
    }
}
