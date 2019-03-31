package org.itsallcode.openfasttrace.cli.commands;

/*-
 * #%L
 * OpenFastTrace Core
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.itsallcode.openfasttrace.cli.CliArguments;
import org.itsallcode.openfasttrace.cli.DirectoryService;
import org.itsallcode.openfasttrace.exporter.ExporterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TestConvertCommand
{
    @Mock
    private DirectoryService directoryServiceMock;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRun()
    {
        final CliArguments args = new CliArguments(directoryServiceMock);
        args.setUnnamedValues(asList("convert", "input"));
        final ExporterException exception = assertThrows(ExporterException.class,
                () -> new ConvertCommand(args).run());
        assertThat(exception.getMessage(),
                equalTo("Found no matching exporter for output format 'specobject'"));
    }
}
