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
import java.util.List;

import org.itsallcode.openfasttrace.importer.LineReader.LineConsumer;

class DelegatingLineConsumer implements LineConsumer
{
    private final List<LineConsumer> delegates;

    DelegatingLineConsumer(final List<LineConsumer> delegates)
    {
        this.delegates = delegates;
    }

    @Override
    public void readLine(final int lineNumber, final String line)
    {
        this.delegates.forEach(delegate -> delegate.readLine(lineNumber, line));
    }
}
