package org.itsallcode.openfasttrace.importer.specobject.xml;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * Configures a SAX parser.
 */
public class SaxParserConfigurator
{
    private SaxParserConfigurator()
    {
    }

    /**
     * Creates a new {@link SAXParserFactory} for secure processing.
     * 
     * @return the configured factory.
     */
    public static SAXParserFactory createSaxParserFactory()
    {
        final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        try
        {
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
