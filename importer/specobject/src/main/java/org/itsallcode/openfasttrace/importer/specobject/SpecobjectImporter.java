package org.itsallcode.openfasttrace.importer.specobject;

import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.specobject.handler.SpecDocumentHandlerBuilder;
import org.itsallcode.openfasttrace.importer.specobject.xml.ContentHandlerAdapter;
import org.itsallcode.openfasttrace.importer.specobject.xml.IgnoringEntityResolver;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.TreeBuildingContentHandler;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.TreeContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Importer for xml files in specobject format.
 */
class SpecobjectImporter implements Importer
{
    private final ImportEventListener listener;
    private final InputFile file;
    private final SAXParserFactory saxParserFactory;

    SpecobjectImporter(final InputFile file, final SAXParserFactory saxParserFactory,
            final ImportEventListener listener)
    {
        this.file = file;
        this.saxParserFactory = saxParserFactory;
        this.listener = listener;
    }

    @Override
    public void runImport()
    {
        try (Reader reader = this.file.createReader())
        {
            final XMLReader xmlReader = this.saxParserFactory.newSAXParser().getXMLReader();
            xmlReader.setEntityResolver(new IgnoringEntityResolver());
            final SpecDocumentHandlerBuilder config = new SpecDocumentHandlerBuilder(this.file,
                    this.listener);
            final TreeContentHandler treeContentHandler = config.build();
            new ContentHandlerAdapter(this.file.getPath(), xmlReader,
                    new TreeBuildingContentHandler(treeContentHandler)).registerListener();
            final InputSource input = new InputSource(reader);
            xmlReader.parse(input);
        }
        catch (SAXException | ParserConfigurationException | IOException exception)
        {
            throw new ImporterException("Error reading \"" + this.file + "\"", exception);
        }
    }
}
