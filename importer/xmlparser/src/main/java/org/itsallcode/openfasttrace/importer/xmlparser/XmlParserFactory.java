package org.itsallcode.openfasttrace.importer.xmlparser;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * This factory creates new {@link XmlParser}s.
 */
public class XmlParserFactory
{

    private final SAXParserFactory parserFactory = createSaxParserFactory();

    /**
     * Create a new instance of a {@link XmlParserFactory}.
     */
    public XmlParserFactory()
    {
        // Default constructor to fix compiler warning "missing-explicit-ctor"
    }

    /**
     * Create a new {@link XmlParser}.
     * 
     * @return a new {@link XmlParser}
     */
    public XmlParser createParser()
    {
        return new XmlParser(parserFactory);
    }

    /**
     * Creates a new {@link SAXParserFactory} configured for secure processing.
     * 
     * @return the configured factory.
     */
    private static SAXParserFactory createSaxParserFactory()
    {
        final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        try
        {
            parserFactory.setNamespaceAware(true);
            forbidDoctypeDeclaration(parserFactory);
            enableSecureProcessing(parserFactory);
        }
        catch (SAXNotRecognizedException | SAXNotSupportedException
                | ParserConfigurationException e)
        {
            throw new IllegalStateException("Error configuring sax parser factory", e);
        }
        return parserFactory;
    }

    private static void enableSecureProcessing(final SAXParserFactory parserFactory)
            throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException
    {
        parserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    }

    private static void forbidDoctypeDeclaration(final SAXParserFactory parserFactory)
            throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException
    {
        parserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    }
}
