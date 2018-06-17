package org.itsallcode.openfasttrace.importer.tag;

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

import org.itsallcode.openfasttrace.importer.*;
import org.itsallcode.openfasttrace.importer.input.InputFile;

/**
 * {@link ImporterFactory} for tags in source code files.
 */
// [impl->dsn~import.full-coverage-tag~1]
public class TagImporterFactory extends RegexMatchingImporterFactory
{
    public TagImporterFactory()
    {
        super("(?i).*\\.java");
    }

    @Override
    public Importer createImporter(final InputFile file, final ImportEventListener listener)
    {
        return new TagImporter(file, listener);
    }
}
