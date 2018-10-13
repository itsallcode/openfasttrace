package org.itsallcode.openfasttrace.core.serviceloader;

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

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.stream.StreamSupport;

import org.itsallcode.openfasttrace.exporter.ExporterContext;
import org.itsallcode.openfasttrace.exporter.ExporterFactory;
import org.itsallcode.openfasttrace.exporter.specobject.SpecobjectExporterFactory;
import org.itsallcode.openfasttrace.importer.ImporterContext;
import org.itsallcode.openfasttrace.importer.ImporterFactory;
import org.itsallcode.openfasttrace.importer.markdown.MarkdownImporterFactory;
import org.itsallcode.openfasttrace.importer.specobject.SpecobjectImporterFactory;
import org.itsallcode.openfasttrace.importer.tag.TagImporterFactory;
import org.itsallcode.openfasttrace.importer.zip.ZipFileImporterFactory;
import org.junit.Test;

/**
 * Test for {@link InitializingServiceLoader}
 */
public class TestInitializingServiceLoader
{
    @Test
    public void testNoServicesRegistered()
    {
        final Object context = new Object();
        final InitializingServiceLoader<InitializableServiceStub, Object> voidServiceLoader = InitializingServiceLoader
                .load(InitializableServiceStub.class, context);
        final List<InitializableServiceStub> services = StreamSupport
                .stream(voidServiceLoader.spliterator(), false).collect(toList());
        assertThat(services, emptyIterable());
        assertThat(voidServiceLoader, emptyIterable());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testImporterFactoriesRegistered()
    {
        final ImporterContext context = new ImporterContext(null);
        final List<ImporterFactory> services = getRegisteredServices(ImporterFactory.class,
                context);
        assertThat(services, hasSize(4));
        assertThat(services, contains(instanceOf(MarkdownImporterFactory.class), //
                instanceOf(SpecobjectImporterFactory.class), //
                instanceOf(TagImporterFactory.class), //
                instanceOf(ZipFileImporterFactory.class)));
        for (final ImporterFactory importerFactory : services)
        {
            assertThat(importerFactory.getContext(), sameInstance(context));
        }
    }

    @Test
    public void testExporterFactoriesRegistered()
    {
        final ExporterContext context = new ExporterContext();
        final List<ExporterFactory> services = getRegisteredServices(ExporterFactory.class,
                context);
        assertThat(services, hasSize(1));
        assertThat(services, contains(instanceOf(SpecobjectExporterFactory.class)));
        for (final ExporterFactory factory : services)
        {
            assertThat(factory.getContext(), sameInstance(context));
        }
    }

    private <T extends Initializable<C>, C> List<T> getRegisteredServices(final Class<T> type,
            final C context)
    {
        final InitializingServiceLoader<T, C> serviceLoader = InitializingServiceLoader.load(type,
                context);
        return StreamSupport.stream(serviceLoader.spliterator(), false).collect(toList());
    }

    class InitializableServiceStub implements Initializable<Object>
    {
        @Override
        public void init(final Object context)
        {
        }
    }
}
