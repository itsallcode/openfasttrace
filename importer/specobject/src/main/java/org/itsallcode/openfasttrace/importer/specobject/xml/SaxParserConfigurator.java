package org.itsallcode.openfasttrace.importer.specobject.xml;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;

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

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * Configures a SAX parser.
 */
public class SaxParserConfigurator
{
    private SaxParserConfigurator()
    {
    }

    /**
     * Creates a new {@link SAXParserFactory} for secure processing.
     * 
     * @return the configured factory.
     */
    public static SAXParserFactory createSaxParserFactory()
    {
        final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        try
        {
            parserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        }
        catch (SAXNotRecognizedException | SAXNotSupportedException
                | ParserConfigurationException e)
        {
            throw new IllegalStateException("Error configuring sax parser factory", e);
        }
        return parserFactory;
    }
}
