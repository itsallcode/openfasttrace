package org.itsallcode.openfasttrace.importer.rif.handler;

import java.util.logging.Logger;

import org.itsallcode.openfasttrace.core.xml.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.core.xml.tree.TreeContentHandler;
import org.itsallcode.openfasttrace.importer.ImportEventListener;

public class RifDocumentHandlerBuilder
{

    private static final Logger LOG = Logger.getLogger(RifDocumentHandlerBuilder.class.getName());

    private static final String DOCTYPE_ATTRIBUTE_NAME = "doctype";
    private final CallbackContentHandler handler;
    private final String fileName;
    private final ImportEventListener listener;

    public RifDocumentHandlerBuilder(final String fileName, final ImportEventListener listener)
    {
        this.fileName = fileName;
        this.listener = listener;

        this.handler = new CallbackContentHandler();
    }

    public TreeContentHandler build()
    {
        return this.handler;

    }

}
