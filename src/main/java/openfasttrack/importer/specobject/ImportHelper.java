package openfasttrack.importer.specobject;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import openfasttrack.importer.ImportEventListener;

class ImportHelper
{
    private final static Logger LOG = Logger.getLogger(ImportHelper.class.getName());
    private final XMLEventReader xmlEventReader;
    private final ImportEventListener listener;
    private String defaultDoctype;

    public ImportHelper(final XMLEventReader xmlEventReader, final ImportEventListener listener)
    {
        this.xmlEventReader = xmlEventReader;
        this.listener = listener;
    }

    void runImport() throws XMLStreamException
    {
        while (this.xmlEventReader.hasNext())
        {
            final XMLEvent currentEvent = this.xmlEventReader.nextEvent();

            if (currentEvent.isStartElement())
            {
                foundStartElement(currentEvent.asStartElement());
            }
        }
    }

    private void foundStartElement(final StartElement element) throws XMLStreamException
    {
        switch (element.getName().getLocalPart())
        {
        case "specobjects":
            final Attribute doctypeAttribute = element.getAttributeByName(new QName("doctype"));
            if (doctypeAttribute != null)
            {
                this.defaultDoctype = doctypeAttribute.getValue();
                LOG.finest(() -> "Found specobjects with default doctype '" + this.defaultDoctype
                        + "'");
            }
            break;

        case "specobject":
            new SingleSpecobjectImportHelper(this.xmlEventReader, this.listener,
                    this.defaultDoctype).runImport();
            break;

        default:
            LOG.warning(() -> "Found unknown start element " + element.getName());
            break;
        }
    }
}
