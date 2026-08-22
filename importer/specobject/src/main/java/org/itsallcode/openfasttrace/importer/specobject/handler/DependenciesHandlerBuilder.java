package org.itsallcode.openfasttrace.importer.specobject.handler;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.core.LocatedSpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.xmlparser.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.importer.xmlparser.tree.TreeContentHandler;

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
        // [impl->dsn~located-specification-item-id-specobject~1]
        this.handler.addCharacterDataListener("dependson",
                data -> this.listener.addDependsOnId(LocatedSpecificationItemId.builder()
                        .id(SpecificationItemId.parseId(data)).build()));
        return this.handler;
    }
}
