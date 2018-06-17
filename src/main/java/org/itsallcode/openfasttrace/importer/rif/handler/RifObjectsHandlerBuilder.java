package org.itsallcode.openfasttrace.importer.rif.handler;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import org.itsallcode.openfasttrace.core.Location;
import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.itsallcode.openfasttrace.core.SpecificationItemId.Builder;
import org.itsallcode.openfasttrace.core.xml.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.core.xml.tree.TreeElement;
import org.itsallcode.openfasttrace.importer.ImportEventListener;

public class RifObjectsHandlerBuilder
{
    private final CallbackContentHandler handler;
    private final String fileName;
    private final ImportEventListener listener;

    private Builder idBuilder = new Builder();
    private final String defaultDoctype;
    private Location.Builder locationBuilder;

    public RifObjectsHandlerBuilder(final String fileName, final String defaultDoctype,
            final ImportEventListener listener)
    {
        this.fileName = fileName;
        this.defaultDoctype = defaultDoctype;
        this.listener = listener;
        this.handler = new CallbackContentHandler();

    }

    public CallbackContentHandler build()
    {
        this.handler.addElementListener("rifobject", this::handleStartElement,
                endElement -> handleEndElement());
        return this.handler;
    }

    private void handleStartElement(final TreeElement elem)
    {
        this.listener.beginSpecificationItem();
        this.locationBuilder = new Location.Builder() //
                .path(this.fileName) //
                .line(elem.getLocation().getLine());
        this.idBuilder = new SpecificationItemId.Builder() //
                .artifactType(this.defaultDoctype);
        this.handler.pushDelegate(new SingleRifObjectsHandlerBuilder(this.listener, this.idBuilder,
                this.locationBuilder).build());
    }

    private void handleEndElement()
    {
        this.listener.setId(this.idBuilder.build());
        this.listener.endSpecificationItem();
        this.listener.setLocation(this.locationBuilder.build());
        this.idBuilder = null;
        this.locationBuilder = null;
    }

}