package org.itsallcode.openfasttrace.importer.specobject.handler;

import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.TreeContentHandler;

class NeedsCoverageHandlerBuilder
{
    private final ImportEventListener listener;
    private final CallbackContentHandler handler;

    NeedsCoverageHandlerBuilder(final ImportEventListener listener)
    {
        this.listener = listener;
        this.handler = new CallbackContentHandler();
    }

    TreeContentHandler build()
    {
        return this.handler.addCharacterDataListener("needsobj",
                this.listener::addNeededArtifactType);
    }
}
