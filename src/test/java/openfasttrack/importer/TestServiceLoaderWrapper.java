package openfasttrack.importer;

/*-
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 - 2017 hamstercommunity
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

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.stream.StreamSupport;

import org.junit.Test;

import openfasttrack.core.ServiceLoaderWrapper;
import openfasttrack.importer.legacytag.LegacyTagImporterFactory;
import openfasttrack.importer.markdown.MarkdownImporterFactory;
import openfasttrack.importer.specobject.SpecobjectImporterFactory;
import openfasttrack.importer.tag.TagImporterFactory;

/**
 * Test for {@link ServiceLoaderWrapper}
 */
public class TestServiceLoaderWrapper
{
    @Test
    public void testNoServicesRegistered()
    {
        final ServiceLoaderWrapper<Void> voidServiceLoader = ServiceLoaderWrapper.load(Void.class);
        final List<Void> services = StreamSupport.stream(voidServiceLoader.spliterator(), false)
                .collect(toList());
        assertThat(services, emptyIterable());
        assertThat(voidServiceLoader, emptyIterable());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testServicesRegistered()
    {
        final List<ImporterFactory> services = getRegisteredServices(ImporterFactory.class);
        assertThat(services, hasSize(4));
        assertThat(services,
                contains(instanceOf(MarkdownImporterFactory.class), //
                        instanceOf(SpecobjectImporterFactory.class), //
                        instanceOf(TagImporterFactory.class),
                        instanceOf(LegacyTagImporterFactory.class)));
    }

    private <T> List<T> getRegisteredServices(final Class<T> type)
    {
        final ServiceLoaderWrapper<T> serviceLoader = ServiceLoaderWrapper.load(type);
        return StreamSupport.stream(serviceLoader.spliterator(), false).collect(toList());
    }
}
