package org.itsallcode.openfasttrace.mode;

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

import java.nio.file.Path;

import org.itsallcode.openfasttrace.Converter;
import org.itsallcode.openfasttrace.exporter.ExporterService;

public class ConvertMode extends AbstractMode<ConvertMode> implements Converter
{
    private final ExporterService exporterService = new ExporterService();

    @Override
    public void convertToFileInFormat(final Path output, final String format)
    {
        this.exporterService.exportFile(importItems(), format, output, this.newline);
    }

    @Override
    protected ConvertMode self()
    {
        return this;
    }
}
