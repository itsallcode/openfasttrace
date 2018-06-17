package org.itsallcode.openfasttrace.importer.rif;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Paths;

import javax.xml.parsers.SAXParserFactory;

import org.itsallcode.openfasttrace.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.ImporterException;
import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.input.StreamInput;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
public class TestRifImporter
{
    private static final String PSEUDO_FILENAME = "input file name";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testImportEmptyFile()
    {
        expectImporterException(
                "Error reading file " + PSEUDO_FILENAME + ": Premature end of file.");
        importFromString("");
    }

    @Test
    public void testUnknownRootElementIgnored()
    {
        final ImportEventListener listenerMock = importFromString("<unknown/>");
        verifyZeroInteractions(listenerMock);
    }

    @Test
    public void testMissingDoctypeThrowsException()
    {
        expectImporterException(
                "does not have an attribute 'doctype' at " + PSEUDO_FILENAME + ":1:27");
        importFromString("<rifdocument><rifobjects/></rifdocument>");
    }

    private void expectImporterException(final String expectedMessage)
    {
        this.thrown.expect(ImporterException.class);
        this.thrown.expectMessage(expectedMessage);
    }

    private ImportEventListener importFromString(final String text)
    {
        final ImportEventListener listenerMock = mock(ImportEventListener.class);
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        final StringReader stringReader = new StringReader(text);
        final InputFile file = StreamInput.forReader(Paths.get(PSEUDO_FILENAME),
                new BufferedReader(stringReader));
        final RifImporter importer = new RifImporter(file, saxParserFactory, listenerMock);
        importer.runImport();
        return listenerMock;
    }
}
