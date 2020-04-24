package org.pike.configurators.file

import org.gradle.api.logging.Logger
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


class XmlConfigurator implements FileConfigurator {
    @Override
    void configure(final Logger logger, File configFile, String completeKey, String value, boolean dryRun) {
        if (dryRun)
            return

        String [] tokensCompleteKey = completeKey.split("->")
        if (tokensCompleteKey.length < 1 || tokensCompleteKey.length > 2)
            throw new IllegalStateException("Invalid number of tokens in key " + completeKey)

        String key = tokensCompleteKey[0]
        String valueAttribute = tokensCompleteKey.length > 1 ? tokensCompleteKey[1] : "value"

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
                nextNode.setAttribute(valueAttribute, valueAsString)
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
