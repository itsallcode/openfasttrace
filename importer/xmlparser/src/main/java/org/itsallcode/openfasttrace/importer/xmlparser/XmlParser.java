package org.itsallcode.openfasttrace.importer.xmlparser;

import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.itsallcode.openfasttrace.importer.xmlparser.tree.TreeBuildingContentHandler;
import org.itsallcode.openfasttrace.importer.xmlparser.tree.TreeContentHandler;
import org.xml.sax.*;

/**
 * This XML parser reads input from a {@link Reader} and invokes a
 * {@link TreeContentHandler} for the read XML events.
 */
public class XmlParser {

    private final SAXParserFactory saxParserFactory;

    XmlParser(final SAXParserFactory saxParserFactory) {
        this.saxParserFactory = saxParserFactory;
    }

    /**
     * Parse the content from the given {@link Reader}, invoking the callback
     * methods of the given {@link TreeContentHandler}.
     * 
     * @param filePath     the input file path used in error messages and
     *                     callback methods
     * @param reader       the reader containing the input to parse
     * @param eventHandler the handler for XML events
     */
    public void parse(final String filePath, final Reader reader, final TreeContentHandler eventHandler) {
        final XMLReader xmlReader = createXmlReader();
        xmlReader.setEntityResolver(new IgnoringEntityResolver());
        new ContentHandlerAdapter(filePath, xmlReader,
                new TreeBuildingContentHandler(eventHandler)).registerListener();
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
