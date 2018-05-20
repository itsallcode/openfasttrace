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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.FilterSettings;
import org.itsallcode.openfasttrace.core.*;
import org.itsallcode.openfasttrace.importer.ImporterContext;
import org.itsallcode.openfasttrace.importer.ImporterService;
import org.itsallcode.openfasttrace.importer.legacytag.config.LegacyTagImporterConfig;

abstract class AbstractMode<T extends AbstractMode<T>>
{
    protected final List<Path> inputs = new ArrayList<>();
    protected Newline newline = Newline.UNIX;
    private FilterSettings filterSettings = FilterSettings.createAllowingEverything();
    private LegacyTagImporterConfig tagImporterConfig = LegacyTagImporterConfig.empty();

    protected abstract T self();

    public T addInputs(final Path... inputs)
    {
        this.inputs.addAll(Arrays.asList(inputs));
        return self();
    }

    public T addInputs(final List<Path> inputs)
    {
        this.inputs.addAll(inputs);
        return self();
    }

    public T setNewline(final Newline newline)
    {

        this.newline = newline;
        return self();
    }

    public T setFilters(final FilterSettings filterSettings)
    {
        this.filterSettings = filterSettings;
        return self();
    }

    public T setLegacyTagImporterPathConfig(final LegacyTagImporterConfig tagImporterConfig)
    {
        this.tagImporterConfig = tagImporterConfig;
        return self();
    }

    protected List<LinkedSpecificationItem> importLinkedSpecificationItems()
    {
        final List<LinkedSpecificationItem> linkedItems;
        final Stream<SpecificationItem> items = importItems();
        final Linker linker = new Linker(items.collect(Collectors.toList()));
        linkedItems = linker.link();
        return linkedItems;
    }

    protected Stream<SpecificationItem> importItems()
    {
        return createImporterService() //
                .setFilters(this.filterSettings) //
                .createImporter() //
                .importAny(this.inputs) //
                .getImportedItems() //
                .stream();
    }

    private ImporterService createImporterService()
    {
        return new ImporterService(new ImporterContext(this.tagImporterConfig));
    }
}
