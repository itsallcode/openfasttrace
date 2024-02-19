package org.itsallcode.openfasttrace.importer.xmlparser;

import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.itsallcode.openfasttrace.importer.xmlparser.tree.TreeBuildingContentHandler;
import org.itsallcode.openfasttrace.importer.xmlparser.tree.TreeContentHandler;
import org.xml.sax.*;

public class XmlParser {

    private final SAXParserFactory saxParserFactory;

    XmlParser(final SAXParserFactory saxParserFactory) {
        this.saxParserFactory = saxParserFactory;
    }

    public void parse(final String filePath, final Reader reader, final TreeContentHandler treeContentHandler) {
        final XMLReader xmlReader = createXmlReader();
        xmlReader.setEntityResolver(new IgnoringEntityResolver());
        new ContentHandlerAdapter(filePath, xmlReader,
                new TreeBuildingContentHandler(treeContentHandler)).registerListener();
        final InputSource input = new InputSource(reader);
        try {
            xmlReader.parse(input);
        } catch (IOException | SAXException exception) {
            throw new XmlParserException("Failed to parse file '" + filePath + "': " + exception.getMessage(),
                    exception);
        }
    }

    private XMLReader createXmlReader() {
        try {
            return this.saxParserFactory.newSAXParser().getXMLReader();
        } catch (SAXException | ParserConfigurationException exception) {
            throw new XmlParserException("Failed to create XML reader", exception);
        }
    }
}
