package org.itsallcode.openfasttrace.importer.specobject.handler;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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

import java.util.logging.Logger;

import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.specobject.xml.event.Attribute;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.TreeContentHandler;

/**
 * Register handlers for parsing an XML specobject document.
 */
public class SpecDocumentHandlerBuilder
{
    private static final Logger LOG = Logger.getLogger(SpecDocumentHandlerBuilder.class.getName());

    private static final String DOCTYPE_ATTRIBUTE_NAME = "doctype";
    private final CallbackContentHandler handler;
    private final InputFile file;
    private final ImportEventListener listener;

    /**
     * Create a new instance.
     * 
     * @param file
     *            the parsed input file.
     * @param listener
     *            the listener receiving import events.
     */
    public SpecDocumentHandlerBuilder(final InputFile file, final ImportEventListener listener)
    {
        this.file = file;
        this.listener = listener;
        this.handler = new CallbackContentHandler();
    }

    /**
     * Build the handler.
     * 
     * @return the content handler.
     */
    public TreeContentHandler build()
    {
        this.handler.setDefaultStartElementListener(startElement -> {
            if (startElement.isRootElement())
            {
                LOG.info(() -> "Found unknown root element " + startElement + ": skip file");
                this.handler.stopParsing();
            }
            LOG.warning(() -> "Found unknown element " + startElement);
            return;
        });

        this.handler.addElementListener("specdocument", elem -> {
            LOG.finest(() -> "Found specdocument element " + elem);
            if (!elem.isRootElement())
            {
                throw new IllegalStateException("Element specdocument must be root element");
            }
        });
        this.handler.addElementListener("specobjects", elem -> {
            final Attribute doctypeAttribute = elem.getAttributeValueByName(DOCTYPE_ATTRIBUTE_NAME);
            if (doctypeAttribute == null)
            {
                throw new ImporterException("Element " + elem + " does not have an attribute '"
                        + DOCTYPE_ATTRIBUTE_NAME + "' at " + elem.getLocation());
            }

            final String defaultDoctype = doctypeAttribute.getValue();
            this.handler.pushDelegate(
                    new SpecObjectsHandlerBuilder(this.file, defaultDoctype, this.listener)
                            .build());
        });

        return this.handler;
    }
}
