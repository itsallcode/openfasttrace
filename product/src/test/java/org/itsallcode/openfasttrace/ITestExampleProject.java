package org.itsallcode.openfasttrace;

/*-
 * #%L
 * OpenFastTrace Product
 * %%
 * Copyright (C) 2016 - 2020 itsallcode.org
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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.List;

import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.importer.ImportSettings;
import org.itsallcode.openfasttrace.core.Oft;
import org.itsallcode.openfasttrace.core.OftRunner;
import org.junit.jupiter.api.Test;

class ITestExampleProject
{

    @Test
    void test()
    {
        final Oft oft = new OftRunner();
        final List<SpecificationItem> items = oft.importItems(ImportSettings.builder()
                .addInputs(Paths.get("src/test/resources/example")).build());
        final List<LinkedSpecificationItem> linkedItems = oft.link(items);
        final Trace trace = oft.trace(linkedItems);
        oft.reportToStdOut(trace);
        assertTrue(trace.hasNoDefects());
    }
}
