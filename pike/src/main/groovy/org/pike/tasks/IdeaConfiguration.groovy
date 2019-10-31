package org.pike.tasks

import org.apache.xerces.dom.DeferredElementImpl
import org.gradle.api.logging.Logger
import org.pike.configuration.Configuration
import org.w3c.dom.Attr
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory

class IdeaConfiguration extends CollectingConfiguration {

    public IdeaConfiguration() {

    }

    public IdeaConfiguration(final Logger logger,
                             final File globalConfigPath,
                             final Collection<File> projectConfigPaths) {
        this.projectConfigPaths = projectConfigPaths
        this.globalConfigPath = globalConfigPath
        this.logger = logger
    }

    @Override
    void apply(Configuration configuration, boolean dryRun) {
        super.apply(configuration, dryRun)

        global("options/editor.xml", "/application/component[@name='EditorSettings']/option[@name='ARE_LINE_NUMBERS_SHOWN']", configuration.showLineNumbers, dryRun)
        global("options/ui.lnf.xml", "/application/component[@name='UISettings']/option[@name='SHOW_MEMORY_INDICATOR']", configuration.showMemory, dryRun)

    }


    public void global(String file, String key, Object value, boolean dryRun) {
        if (value == null)
            return

        collectConfiguration('workspace', file, key, value)

        if (!dryRun) {
            if (globalConfigPath == null)
                throw new IllegalStateException("GlobalConfigPath not set")
            File configFile = new File(globalConfigPath, file)

            xml(configFile, key, value)
        }

    }

    public void project(String file, String key, String value, boolean dryRun) {
        throw new IllegalStateException("TODO")
    }

    void xml(final File configFile, String key, Object value) {

        String valueAsString = value.toString()
        String[] tokens = key.split("/")
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
        DocumentBuilder builder = factory.newDocumentBuilder()
        Document doc = null
        if (configFile.exists())
            doc = builder.parse(configFile)
        else
            doc = builder.newDocument()

        XPathFactory xPathfactory = XPathFactory.newInstance()


        List<String> currentKey = []

        Node parentElement = doc

        for (String next: tokens) {

            String tagname = next.split('\\[').first()
            String attributeToken = next.contains('[') ? next.split('\\[')[1].replace("@", "").replace("]", ""): null
            String attributeName = attributeToken != null ? attributeToken.split("=").first(): null
            String attributeValue = attributeToken != null ? attributeToken.split("=").last().replace("'", ""): null


            currentKey.add(next)

            XPath xpath = xPathfactory.newXPath()
            String pointer = String.join('/', currentKey)
            if (pointer.trim().isEmpty())
                continue

            boolean lastElement = pointer.length() == key.length()

            XPathExpression expr = xpath.compile(pointer)
            Element nextNode = expr.evaluate(doc, XPathConstants.NODE)

            if (nextNode == null) {
                nextNode = doc.createElement(tagname)
                if (attributeToken != null) {
                    nextNode.setAttribute(attributeName, attributeValue)
                }
                parentElement.appendChild(nextNode)
            }


            if (lastElement) {
                if (logger)
                    logger.lifecycle("Set " + key + " = " + valueAsString + " (" + configFile.absolutePath + ")")
                nextNode.setAttribute("value", valueAsString)
            }

            parentElement = nextNode

        }




        TransformerFactory transformerFactory = TransformerFactory.newInstance()
        Transformer transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        DOMSource source = new DOMSource(doc)

        configFile.parentFile.mkdirs()

        StreamResult result = new StreamResult(configFile)
        transformer.transform(source, result);



    }


}
