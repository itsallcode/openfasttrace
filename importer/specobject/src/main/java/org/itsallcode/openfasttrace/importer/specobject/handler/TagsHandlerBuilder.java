package org.itsallcode.openfasttrace.importer.specobject.handler;

import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.xmlparser.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.importer.xmlparser.tree.TreeContentHandler;

class TagsHandlerBuilder
{
    private final ImportEventListener listener;
    private final CallbackContentHandler handler;

    TagsHandlerBuilder(final ImportEventListener listener)
    {
        this.listener = listener;
        this.handler = new CallbackContentHandler();
    }

    TreeContentHandler build()
    {
        return this.handler.addCharacterDataListener("tag", this.listener::addTag);
    }
}
