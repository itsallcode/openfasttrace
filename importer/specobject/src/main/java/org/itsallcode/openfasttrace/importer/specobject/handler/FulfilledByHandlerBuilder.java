package org.itsallcode.openfasttrace.importer.specobject.handler;

import org.itsallcode.openfasttrace.importer.xmlparser.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.importer.xmlparser.tree.TreeContentHandler;

class FulfilledByHandlerBuilder
{
    private final CallbackContentHandler handler;

    FulfilledByHandlerBuilder()
    {
        this.handler = new CallbackContentHandler();
    }

    TreeContentHandler build()
    {
        this.handler.addSubTreeHandler("ffbObj", FulfilledByHandlerBuilder::createFulfillByObjectHandler);
        return this.handler;
    }

    private static CallbackContentHandler createFulfillByObjectHandler()
    {
        return new CallbackContentHandler()
                .addCharacterDataListener("ffbId", data -> {})
                .addCharacterDataListener("ffbType", data -> {})
                .addIntDataListener("ffbVersion", data -> {});
    }
}
