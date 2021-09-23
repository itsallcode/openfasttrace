package org.itsallcode.openfasttrace.importer.specobject.xml;

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

import java.io.StringReader;
import java.util.logging.Logger;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * An {@link EntityResolver} that ignores all entities.
 */
public class IgnoringEntityResolver implements EntityResolver
{
    private static final Logger LOG = Logger.getLogger(IgnoringEntityResolver.class.getName());

    @Override
    public InputSource resolveEntity(final String publicId, final String systemId)
    {
        LOG.warning(() -> "Ignoring entity with public id '" + publicId + "' and system id '"
                + systemId + "'.");
        return new InputSource(new StringReader(""));
    }
}
