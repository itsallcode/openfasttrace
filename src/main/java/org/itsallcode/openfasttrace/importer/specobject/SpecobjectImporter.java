package org.itsallcode.openfasttrace.importer.specobject;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.itsallcode.openfasttrace.core.xml.ContentHandlerAdapter;
import org.itsallcode.openfasttrace.core.xml.IgnoringEntityResolver;
import org.itsallcode.openfasttrace.core.xml.tree.TreeBuildingContentHandler;
import org.itsallcode.openfasttrace.core.xml.tree.TreeContentHandler;
import org.itsallcode.openfasttrace.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.Importer;
import org.itsallcode.openfasttrace.importer.ImporterException;
import org.itsallcode.openfasttrace.importer.specobject.handler.SpecDocumentHandlerBuilder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

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
