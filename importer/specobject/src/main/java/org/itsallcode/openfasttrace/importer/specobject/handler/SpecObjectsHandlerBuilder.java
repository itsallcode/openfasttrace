package org.itsallcode.openfasttrace.importer.specobject.handler;

import org.itsallcode.openfasttrace.api.core.Location;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId.Builder;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.TreeElement;

class SpecObjectsHandlerBuilder
{
    private final CallbackContentHandler handler;
    private final InputFile file;
    private final ImportEventListener listener;

    private Builder idBuilder = new Builder();
    private final String defaultDoctype;
    private Location.Builder locationBuilder;

    SpecObjectsHandlerBuilder(final InputFile file, final String defaultDoctype,
            final ImportEventListener listener)
    {
        this.file = file;
        this.defaultDoctype = defaultDoctype;
        this.listener = listener;
        this.handler = new CallbackContentHandler();
    }

    CallbackContentHandler build()
    {
        this.handler.addElementListener("specobject", this::handleStartElement,
                endElement -> handleEndElement());
        return this.handler;
    }

    private void handleStartElement(final TreeElement elem)
    {
        this.listener.beginSpecificationItem();
        this.locationBuilder = Location.builder() //
                .path(this.file.getPath()) //
                .line(elem.getLocation().getLine());
        this.idBuilder = new SpecificationItemId.Builder() //
                .artifactType(this.defaultDoctype);
        this.handler.pushDelegate(new SingleSpecObjectsHandlerBuilder(this.listener, this.idBuilder,
                this.locationBuilder).build());
    }

    private void handleEndElement()
    {
        this.listener.setId(this.idBuilder.build());
        this.listener.setLocation(this.locationBuilder.build());
        this.listener.endSpecificationItem();
        this.idBuilder = null;
        this.locationBuilder = null;
    }
}
