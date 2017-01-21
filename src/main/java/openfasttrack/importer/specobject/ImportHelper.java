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
import openfasttrack.importer.ImporterException;

/**
 * This class imports specobjects from a single {@link XMLEventReader} and
 * outputs found items to an {@link ImportEventListener} using a
 * {@link SingleSpecobjectImportHelper}.
 */
class ImportHelper
{
    private static final Logger LOG = Logger.getLogger(ImportHelper.class.getName());

    private static final String SPECDOCUMENT_ROOT_ELEMENT_NAME = "specdocument";
    private static final String SPECOBJECTS_ELEMENT_NAME = "specobjects";
    private static final String SPECOBJECT_ELEMENT_NAME = "specobject";
    private static final String DOCTYPE_ATTRIBUTE_NAME = "doctype";

    private final String fileName;
    private final XMLEventReader xmlEventReader;
    private final ImportEventListener listener;
    private String defaultDoctype;
    boolean validRootElementFound = false;

    public ImportHelper(final String fileName, final XMLEventReader xmlEventReader,
            final ImportEventListener listener)
    {
        this.fileName = fileName;
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
                if (isXmlRootWhitelisted(currentEvent.asStartElement()))
                {
                    return;
                }
                foundStartElement(currentEvent.asStartElement());
            }
        }
    }

    private boolean isXmlRootWhitelisted(final StartElement currentElement)
    {
        if (this.validRootElementFound)
        {
            return false;
        }
        if (isWhitelisted(currentElement))
        {
            this.validRootElementFound = true;
            return false;
        }
        logUnkownElement(currentElement, "skip file");
        return true;
    }

    private boolean isWhitelisted(final StartElement rootElement)
    {
        final String elementName = rootElement.getName().getLocalPart();
        return SPECDOCUMENT_ROOT_ELEMENT_NAME.equals(elementName);
    }

    private void foundStartElement(final StartElement element) throws XMLStreamException
    {
        switch (element.getName().getLocalPart())
        {
        case SPECDOCUMENT_ROOT_ELEMENT_NAME:
            LOG.finest(() -> "Found XML root element '" + SPECDOCUMENT_ROOT_ELEMENT_NAME + "' at "
                    + this.fileName + ":" + element.getLocation().getLineNumber());
            break;
        case SPECOBJECTS_ELEMENT_NAME:
            processSpecobjectsContainerElement(element);
            break;
        case SPECOBJECT_ELEMENT_NAME:
            processSpecobjectElement(element);
            break;
        default:
            logUnkownElement(element, "skip element");
            break;
        }
    }

    private void processSpecobjectElement(final StartElement element) throws XMLStreamException
    {
        if (this.defaultDoctype == null)
        {
            throw new ImporterException(
                    "No specobject default doctype found in file '" + this.fileName + "'");
        }
        LOG.finest(() -> "Found XML element '" + SPECOBJECT_ELEMENT_NAME
                + "': import using default doctype '" + this.defaultDoctype + "' from "
                + this.fileName + ":" + element.getLocation().getLineNumber());
        new SingleSpecobjectImportHelper(this.xmlEventReader, this.listener, this.defaultDoctype)
                .runImport();
    }

    private void processSpecobjectsContainerElement(final StartElement element)
    {
        final QName doctypeAttributeName = new QName(DOCTYPE_ATTRIBUTE_NAME);
        final Attribute doctypeAttribute = element.getAttributeByName(doctypeAttributeName);
        if (doctypeAttribute == null)
        {
            throw new ImporterException("Element " + element + " does not have an attribute '"
                    + doctypeAttributeName + "' at " + this.fileName + ":"
                    + element.getLocation().getLineNumber());
        }
        this.defaultDoctype = doctypeAttribute.getValue();
        LOG.finest(() -> "Found XML element '" + SPECOBJECTS_ELEMENT_NAME
                + "' with default doctype '" + this.defaultDoctype + "' at " + this.fileName + ":"
                + element.getLocation().getLineNumber());
    }

    private void logUnkownElement(final StartElement element, final String consequence)
    {
        LOG.warning(() -> "Found unknown XML element '" + element + "': at " + this.fileName + ":"
                + element.getLocation().getLineNumber() + ": " + consequence);
    }
}
