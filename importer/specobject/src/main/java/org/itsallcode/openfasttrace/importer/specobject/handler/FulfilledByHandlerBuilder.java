package org.itsallcode.openfasttrace.importer.specobject.handler;

import org.itsallcode.openfasttrace.importer.specobject.xml.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.TreeContentHandler;

class FulfilledByHandlerBuilder
{
    private final CallbackContentHandler handler;

    FulfilledByHandlerBuilder()
    {
        this.handler = new CallbackContentHandler();
    }

    TreeContentHandler build()
    {
        this.handler.addSubTreeHandler("ffbObj", this::createFulfillByObjectHandler);
        return this.handler;
    }

    private CallbackContentHandler createFulfillByObjectHandler()
    {
        return new CallbackContentHandler()
                .addCharacterDataListener("ffbId", data -> {})
                .addCharacterDataListener("ffbType", data -> {})
                .addIntDataListener("ffbVersion", data -> {});
    }
}
