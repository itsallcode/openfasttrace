package org.itsallcode.openfasttrace.importer.specobject.handler;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId.Builder;
import org.itsallcode.openfasttrace.api.core.LocatedSpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.xmlparser.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.importer.xmlparser.tree.TreeContentHandler;

class ProvidesCoverageHandlerBuilder
{
    private final ImportEventListener listener;
    private final CallbackContentHandler handler;
    private Builder providesCoverageIdBuilder;

    ProvidesCoverageHandlerBuilder(final ImportEventListener listener)
    {
        this.listener = listener;
        this.handler = new CallbackContentHandler();
    }

    TreeContentHandler build()
    {
        this.handler.addElementListener("provcov",
                elem -> this.providesCoverageIdBuilder = new Builder(), //
                endElem -> {
                    // [impl->dsn~located-specification-item-id-specobject~1]
                    this.listener.addCoveredId(LocatedSpecificationItemId.builder()
                            .id(this.providesCoverageIdBuilder.build()).build());
                    this.providesCoverageIdBuilder = null;
                });

        this.handler.addCharacterDataListener("linksto",
                data -> this.providesCoverageIdBuilder.name(data));
        this.handler.addIntDataListener("dstversion",
                data -> this.providesCoverageIdBuilder.revision(data));

        return this.handler;
    }
}
