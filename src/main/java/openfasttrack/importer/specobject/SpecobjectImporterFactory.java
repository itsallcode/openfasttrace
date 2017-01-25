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

import java.io.Reader;

import javax.xml.stream.XMLInputFactory;

import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterFactory;

/**
 * {@link ImporterFactory} for XML specobject files
 */
public class SpecobjectImporterFactory extends ImporterFactory
{
    private final XMLInputFactory xmlInputFactory;

    public SpecobjectImporterFactory()
    {
        super("(?i).*\\.xml");
        this.xmlInputFactory = XMLInputFactory.newFactory();
    }

    @Override
    public Importer createImporter(final String fileName, final Reader reader,
            final ImportEventListener listener)
    {
        return new SpecobjectImporter(fileName, reader, this.xmlInputFactory, listener);
    }
}
