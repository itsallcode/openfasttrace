package openfasttrack.importer.specobject;

import java.io.BufferedReader;
import java.io.Reader;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterException;

/**
 * Importer for xml files in specobject format.
 */
class SpecobjectImporter implements Importer
{
    private final XMLInputFactory xmlInputFactory;
    private final Reader reader;
    private final ImportEventListener listener;

    SpecobjectImporter(final Reader reader, final XMLInputFactory xmlInputFactory,
            final ImportEventListener listener)
    {
        this.xmlInputFactory = xmlInputFactory;
        this.listener = listener;
        this.reader = new BufferedReader(reader);
    }

    @Override
    public void runImport()
    {
        try
        {
            final XMLEventReader xmlEventReader = this.xmlInputFactory
                    .createXMLEventReader(this.reader);
            final ImportHelper importHelper = new ImportHelper(xmlEventReader, this.listener);
            importHelper.runImport();
        } catch (final XMLStreamException e)
        {
            throw new ImporterException("Error importing specobjects document", e);
        }
    }
}
