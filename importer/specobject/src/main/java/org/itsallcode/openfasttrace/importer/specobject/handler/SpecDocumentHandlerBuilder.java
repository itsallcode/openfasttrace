package org.itsallcode.openfasttrace.importer.specobject.handler;

import java.util.logging.Logger;

import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.specobject.xml.event.Attribute;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.TreeContentHandler;

/**
 * Register handlers for parsing an XML specobject document.
 */
public class SpecDocumentHandlerBuilder
{
    private static final Logger LOG = Logger.getLogger(SpecDocumentHandlerBuilder.class.getName());

    private static final String DOCTYPE_ATTRIBUTE_NAME = "doctype";
    private final CallbackContentHandler handler;
    private final InputFile file;
    private final ImportEventListener listener;

    /**
     * Create a new instance.
     * 
     * @param file
     *            the parsed input file.
     * @param listener
     *            the listener receiving import events.
     */
    public SpecDocumentHandlerBuilder(final InputFile file, final ImportEventListener listener)
    {
        this.file = file;
        this.listener = listener;
        this.handler = new CallbackContentHandler();
    }

    /**
     * Build the handler.
     * 
     * @return the content handler.
     */
    public TreeContentHandler build()
    {
        this.handler.setDefaultStartElementListener(startElement -> {
            if (startElement.isRootElement())
            {
                LOG.info(() -> "Found unknown root element " + startElement + ": skip file");
                this.handler.stopParsing();
            }
            LOG.warning(() -> "Found unknown element " + startElement);
            return;
        });

        this.handler.addElementListener("specdocument", elem -> {
            LOG.finest(() -> "Found specdocument element " + elem);
            if (!elem.isRootElement())
            {
                throw new IllegalStateException("Element specdocument must be root element");
            }
        });
        this.handler.addElementListener("specobjects", elem -> {
            final Attribute doctypeAttribute = elem.getAttributeValueByName(DOCTYPE_ATTRIBUTE_NAME);
            if (doctypeAttribute == null)
            {
                throw new ImporterException("Element " + elem + " does not have an attribute '"
                        + DOCTYPE_ATTRIBUTE_NAME + "' at " + elem.getLocation());
            }

            final String defaultDoctype = doctypeAttribute.getValue();
            this.handler.pushDelegate(
                    new SpecObjectsHandlerBuilder(this.file, defaultDoctype, this.listener)
                            .build());
        });

        return this.handler;
    }
}
