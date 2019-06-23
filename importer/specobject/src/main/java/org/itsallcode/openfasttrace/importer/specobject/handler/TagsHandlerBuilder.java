package org.itsallcode.openfasttrace.importer.specobject.handler;

import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.TreeContentHandler;

public class TagsHandlerBuilder
{
    private final ImportEventListener listener;
    private final CallbackContentHandler handler;

    public TagsHandlerBuilder(final ImportEventListener listener)
    {
        this.listener = listener;
        this.handler = new CallbackContentHandler();
    }

    public TreeContentHandler build()
    {
        return this.handler.addCharacterDataListener("tag", this.listener::addTag);
    }
}
