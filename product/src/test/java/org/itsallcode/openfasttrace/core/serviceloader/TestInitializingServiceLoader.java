package org.itsallcode.openfasttrace.core.serviceloader;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.StreamSupport;

import org.itsallcode.openfasttrace.api.core.serviceloader.Initializable;
import org.itsallcode.openfasttrace.api.exporter.ExporterContext;
import org.itsallcode.openfasttrace.api.exporter.ExporterFactory;
import org.itsallcode.openfasttrace.api.importer.ImporterContext;
import org.itsallcode.openfasttrace.api.importer.ImporterFactory;
import org.itsallcode.openfasttrace.exporter.specobject.SpecobjectExporterFactory;
import org.itsallcode.openfasttrace.importer.markdown.MarkdownImporterFactory;
import org.itsallcode.openfasttrace.importer.restructuredtext.RestructuredTextImporterFactory;
import org.itsallcode.openfasttrace.importer.specobject.SpecobjectImporterFactory;
import org.itsallcode.openfasttrace.importer.tag.TagImporterFactory;
import org.itsallcode.openfasttrace.importer.zip.ZipFileImporterFactory;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link InitializingServiceLoader}
 */
class TestInitializingServiceLoader
{
    @Test
    void testNoServicesRegistered()
    {
        final Object context = new Object();
        final InitializingServiceLoader<InitializableServiceStub, Object> voidServiceLoader = InitializingServiceLoader
                .load(InitializableServiceStub.class, context);
        final List<InitializableServiceStub> services = StreamSupport
                .stream(voidServiceLoader.spliterator(), false).collect(toList());
        assertThat(services, emptyIterable());
        assertThat(voidServiceLoader, emptyIterable());
    }

    @Test
    void testImporterFactoriesRegistered()
    {
        final ImporterContext context = new ImporterContext(null);
        final List<ImporterFactory> services = getRegisteredServices(ImporterFactory.class,
                context);
        assertThat(services, hasSize(5));
        assertThat(services, containsInAnyOrder(instanceOf(MarkdownImporterFactory.class), //
                instanceOf(RestructuredTextImporterFactory.class), //
                instanceOf(SpecobjectImporterFactory.class), //
                instanceOf(TagImporterFactory.class), //
                instanceOf(ZipFileImporterFactory.class)));
        for (final ImporterFactory importerFactory : services)
        {
            assertThat(importerFactory.getContext(), sameInstance(context));
        }
    }

    @Test
    void testExporterFactoriesRegistered()
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
