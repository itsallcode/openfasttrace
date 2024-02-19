package org.itsallcode.openfasttrace.importer.specobject;

import java.io.IOException;
import java.io.Reader;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.specobject.handler.SpecDocumentHandlerBuilder;
import org.itsallcode.openfasttrace.importer.xmlparser.XmlParserFactory;
import org.itsallcode.openfasttrace.importer.xmlparser.tree.TreeContentHandler;

/**
 * Importer for xml files in specobject format.
 */
class SpecobjectImporter implements Importer
{
    private final ImportEventListener listener;
    private final InputFile file;
    private final XmlParserFactory xmlParserFactory;

    SpecobjectImporter(final InputFile file, final XmlParserFactory xmlParserFactory,
            final ImportEventListener listener)
    {
        this.file = file;
        this.xmlParserFactory = xmlParserFactory;
        this.listener = listener;
    }

    @Override
    public void runImport()
    {
        try (Reader reader = this.file.createReader())
        {
            final SpecDocumentHandlerBuilder config = new SpecDocumentHandlerBuilder(this.file,
                    this.listener);
            final TreeContentHandler treeContentHandler = config.build();
            this.xmlParserFactory.createParser().parse(this.file.getPath(), reader, treeContentHandler);
        }
        catch (final IOException exception)
        {
            throw new ImporterException(
                    "Failed to read input file '" + this.file.getPath() + "': " + exception.getMessage(), exception);
        }
    }
}
