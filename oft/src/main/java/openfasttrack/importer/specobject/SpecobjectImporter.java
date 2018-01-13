package openfasttrack.importer.specobject;

/*-
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 - 2017 hamstercommunity
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
import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import openfasttrack.core.xml.ContentHandlerAdapter;
import openfasttrack.core.xml.IgnoringEntityResolver;
import openfasttrack.core.xml.tree.TreeBuildingContentHandler;
import openfasttrack.core.xml.tree.TreeContentHandler;
import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterException;
import openfasttrack.importer.specobject.handler.SpecDocumentHandlerBuilder;

/**
 * Importer for xml files in specobject format.
 */
class SpecobjectImporter implements Importer
{
    private final Reader reader;
    private final ImportEventListener listener;
    private final String fileName;
    private final SAXParserFactory saxParserFactory;

    SpecobjectImporter(final String fileName, final Reader reader,
            final SAXParserFactory saxParserFactory, final ImportEventListener listener)
    {
        this.fileName = fileName;
        this.saxParserFactory = saxParserFactory;
        this.listener = listener;
        this.reader = new BufferedReader(reader);
    }

    @Override
    public void runImport()
    {
        try
        {
            final XMLReader xmlReader = this.saxParserFactory.newSAXParser().getXMLReader();
            xmlReader.setEntityResolver(new IgnoringEntityResolver());
            final SpecDocumentHandlerBuilder config = new SpecDocumentHandlerBuilder(this.fileName,
                    this.listener);
            final TreeContentHandler treeContentHandler = config.build();
            new ContentHandlerAdapter(this.fileName, xmlReader,
                    new TreeBuildingContentHandler(treeContentHandler)).registerListener();
            final InputSource input = new InputSource(this.reader);
            xmlReader.parse(input);
        }
        catch (SAXException | ParserConfigurationException | IOException e)
        {
            throw new ImporterException("Error importing specobjects document " + this.fileName, e);
        }
    }
}
