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
    public final static String COMPARE_PREFS = '.metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.compare.prefs'
    public final static String WST_VALIDATION_PREFS = '.metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.wst.validation.prefs'
    public final static String SONAR_PREFS = '.metadata/.plugins/org.eclipse.core.runtime/.settings/org.sonar.ide.eclipse.core.prefs'

    public final static String KEY_PREFERENCES_VERSION = "eclipse.preferences.version"

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

        workspace(FILE_RESOURCES_PREFS, 'encoding', configuration.encoding, dryRun, PropertiesConfigurator.class)
        workspace(FILE_UI_PREFS, 'SHOW_MEMORY_MONITOR', configuration.showMemory, dryRun, PropertiesConfigurator.class)

        workspace(COMPARE_PREFS, KEY_PREFERENCES_VERSION, '1', dryRun, PropertiesConfigurator.class)
        workspace(COMPARE_PREFS, 'org.eclipse.compare.IgnoreWhitespace', configuration.compareDialogWhitespaces, dryRun, PropertiesConfigurator.class)

        workspace(FILE_RESOURCES_PREFS, 'refresh.enabled', Boolean.TRUE, dryRun, PropertiesConfigurator.class) //native refresh hook

        if (configuration.disableAutomaticXmlValidation != null && configuration.disableAutomaticXmlValidation.equals(Boolean.TRUE)) {
            workspace(WST_VALIDATION_PREFS, 'vals/org.eclipse.wst.dtd.core.dtdDTDValidator/global', 'TF01', dryRun, PropertiesConfigurator.class )
            workspace(WST_VALIDATION_PREFS, 'vals/org.eclipse.wst.xml.core.xml/global', 'TF03', dryRun, PropertiesConfigurator.class)
            workspace(WST_VALIDATION_PREFS, 'vals/org.eclipse.wst.xsd.core.xsd/global', 'TF02162org.eclipse.wst.xsd.core.internal.validation.eclipse.Validator', dryRun, PropertiesConfigurator.class)
        }

        if (configuration.sonarqubeUrl != null) {
            workspace(SONAR_PREFS, KEY_PREFERENCES_VERSION, '1', dryRun, PropertiesConfigurator.class)
            workspace(SONAR_PREFS, 'servers/default/url', configuration.sonarqubeUrl, dryRun, PropertiesConfigurator.class)
            workspace(SONAR_PREFS, 'servers/default/auth', Boolean.FALSE.toString(), dryRun, PropertiesConfigurator.class)
            workspace(SONAR_PREFS, 'servers/initialized', Boolean.TRUE.toString(), dryRun, PropertiesConfigurator.class)
        }

        if (configuration.formatter != null) {
            Formatter formatter = configuration.formatter
            workspace(JDT_UI_PREFS, 'formatter_profile', "_" + formatter.name, dryRun, PropertiesConfigurator.class)
            workspace(JDT_UI_PREFS, 'formatter_settings_version', '17', dryRun, PropertiesConfigurator.class)
            workspace(JDT_UI_PREFS, 'org.eclipse.jdt.ui.formatterprofiles', getFormatterXml(formatter), dryRun, PropertiesConfigurator.class)
            workspace(JDT_UI_PREFS, 'org.eclipse.jdt.ui.formatterprofiles.version', '17', dryRun, PropertiesConfigurator.class)
            if (formatter.lineSplit) {
                workspace(FILE_UI_EDITORS_PREFS, 'printMargin', Boolean.TRUE.toString(), dryRun, PropertiesConfigurator.class)
                workspace(FILE_UI_EDITORS_PREFS, 'printMarginColumn', formatter.lineSplit, dryRun, PropertiesConfigurator.class)
            }
        }
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
