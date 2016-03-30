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

import java.io.BufferedReader;
import java.io.Reader;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterException;

/**
 * Importer for xml files in specobject format.
 */
class SpecobjectImporter implements Importer
{
    private final XMLInputFactory xmlInputFactory;
    private final Reader reader;
    private final ImportEventListener listener;

    SpecobjectImporter(final Reader reader, final XMLInputFactory xmlInputFactory,
            final ImportEventListener listener)
    {
        this.xmlInputFactory = xmlInputFactory;
        this.listener = listener;
        this.reader = new BufferedReader(reader);
    }

    @Override
    public void runImport()
    {
        try
        {
            final XMLEventReader xmlEventReader = this.xmlInputFactory
                    .createXMLEventReader(this.reader);
            final ImportHelper importHelper = new ImportHelper(xmlEventReader, this.listener);
            importHelper.runImport();
        } catch (final XMLStreamException e)
        {
            throw new ImporterException("Error importing specobjects document", e);
        }
    }
}
