package org.itsallcode.openfasttrace.importer.rif;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.itsallcode.openfasttrace.core.xml.ContentHandlerAdapter;
import org.itsallcode.openfasttrace.core.xml.IgnoringEntityResolver;
import org.itsallcode.openfasttrace.core.xml.tree.TreeBuildingContentHandler;
import org.itsallcode.openfasttrace.core.xml.tree.TreeContentHandler;
import org.itsallcode.openfasttrace.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.Importer;
import org.itsallcode.openfasttrace.importer.ImporterException;
import org.itsallcode.openfasttrace.importer.rif.handler.RifDocumentHandlerBuilder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class RifImporter implements Importer
{

    private final String fileName;
    private final SAXParserFactory saxParserFactory;
    private final ImportEventListener listener;
    private final BufferedReader reader;

    RifImporter(final String fileName, final Reader reader, final SAXParserFactory saxParserFactory,
            final ImportEventListener listener)
    {
        this.fileName = fileName;
        this.saxParserFactory = saxParserFactory;
        this.listener = listener;
        this.reader = new BufferedReader(reader);
    }

    @Override
    public void runImport()
    {
        try
        {
            final XMLReader xmlReader = this.saxParserFactory.newSAXParser().getXMLReader();
            xmlReader.setEntityResolver(new IgnoringEntityResolver());

            final RifDocumentHandlerBuilder config = new RifDocumentHandlerBuilder(this.fileName,
                    this.listener);

            final TreeContentHandler treeContentHandler = config.build();
            new ContentHandlerAdapter(this.fileName, xmlReader,
                    new TreeBuildingContentHandler(treeContentHandler)).registerListener();
            final InputSource input = new InputSource(this.reader);
            xmlReader.parse(input);
        }
        catch (SAXException | ParserConfigurationException | IOException e)
        {
            throw new ImporterException("Error importing specobjects document " + this.fileName, e);
        }

    }

}
