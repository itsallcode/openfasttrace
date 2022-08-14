package org.itsallcode.openfasttrace.importer.specobject.handler;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.TreeContentHandler;

class DependenciesHandlerBuilder
{
    private final ImportEventListener listener;
    private final CallbackContentHandler handler;

    DependenciesHandlerBuilder(final ImportEventListener listener)
    {
        this.listener = listener;
        this.handler = new CallbackContentHandler();
    }

    TreeContentHandler build()
    {
        this.handler.addCharacterDataListener("dependson",
                data -> this.listener.addDependsOnId(SpecificationItemId.parseId(data)));
        return this.handler;
    }
}
