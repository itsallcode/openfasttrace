package org.itsallcode.openfasttrace.core.serviceloader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.itsallcode.openfasttrace.api.core.serviceloader.Initializable;
import org.itsallcode.openfasttrace.api.exporter.ExporterContext;
import org.itsallcode.openfasttrace.api.exporter.ExporterFactory;
import org.itsallcode.openfasttrace.api.importer.ImporterContext;
import org.itsallcode.openfasttrace.api.importer.ImporterFactory;
import org.itsallcode.openfasttrace.api.report.ReporterContext;
import org.itsallcode.openfasttrace.api.report.ReporterFactory;
import org.itsallcode.openfasttrace.exporter.specobject.SpecobjectExporterFactory;
import org.itsallcode.openfasttrace.importer.markdown.MarkdownImporterFactory;
import org.itsallcode.openfasttrace.importer.restructuredtext.RestructuredTextImporterFactory;
import org.itsallcode.openfasttrace.importer.specobject.SpecobjectImporterFactory;
import org.itsallcode.openfasttrace.importer.tag.TagImporterFactory;
import org.itsallcode.openfasttrace.importer.zip.ZipFileImporterFactory;
import org.itsallcode.openfasttrace.report.aspec.ASpecReporterFactory;
import org.itsallcode.openfasttrace.report.html.HtmlReporterFactory;
import org.itsallcode.openfasttrace.report.plaintext.PlaintextReporterFactory;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link InitializingServiceLoader} from module {@code core}. This
 * test must be located in module {@code product} (which includes all plugin
 * modules) so that it can access all plugin services.
 */
class TestInitializingServiceLoader
{
    @Test
    void testNoServicesRegistered()
    {
        final Object context = new Object();
        final Loader<InitializableServiceStub> voidServiceLoader = InitializingServiceLoader
                .load(InitializableServiceStub.class, context);
        final List<InitializableServiceStub> services = voidServiceLoader.load().toList();
        assertThat(services, emptyIterable());
        assertThat(voidServiceLoader.load().toList(), emptyIterable());
    }

    // [itest->dsn~plugins.loading.plugin_types~1]
    @Test
    void testImporterFactoriesRegistered()
    {
        final ImporterContext context = new ImporterContext(null);
        final List<ImporterFactory> services = getRegisteredServices(ImporterFactory.class,
                context);
        assertThat(services, hasSize(5));
        assertThat(services, containsInAnyOrder(
                instanceOf(MarkdownImporterFactory.class),
                instanceOf(RestructuredTextImporterFactory.class),
                instanceOf(SpecobjectImporterFactory.class),
                instanceOf(TagImporterFactory.class),
                instanceOf(ZipFileImporterFactory.class)));
        for (final ImporterFactory importerFactory : services)
        {
            assertThat(importerFactory.getContext(), sameInstance(context));
        }
    }

    // [itest->dsn~plugins.loading.plugin_types~1]
    @Test
    void testExporterFactoriesRegistered()
    {
        final ExporterContext context = new ExporterContext();
        final List<ExporterFactory> services = getRegisteredServices(ExporterFactory.class,
                context);
        assertThat(services, hasSize(1));
        assertThat(services, containsInAnyOrder(instanceOf(SpecobjectExporterFactory.class)));
        for (final ExporterFactory factory : services)
        {
            assertThat(factory.getContext(), sameInstance(context));
        }
    }

    // [itest->dsn~plugins.loading.plugin_types~1]
    @Test
    void testReporterFactoriesRegistered()
    {
        final ReporterContext context = new ReporterContext(null);
        final List<ReporterFactory> services = getRegisteredServices(ReporterFactory.class,
                context);
        assertThat(services, hasSize(3));
        assertThat(services, containsInAnyOrder(instanceOf(PlaintextReporterFactory.class),
                instanceOf(ASpecReporterFactory.class),
                instanceOf(HtmlReporterFactory.class)));
        for (final ReporterFactory factory : services)
        {
            assertThat(factory.getContext(), sameInstance(context));
        }
    }

    private <T extends Initializable<C>, C> List<T> getRegisteredServices(final Class<T> type,
            final C context)
    {
        final Loader<T> serviceLoader = InitializingServiceLoader.load(type, context);
        return serviceLoader.load().toList();
    }

    class InitializableServiceStub implements Initializable<Object>
    {
        @Override
        public void init(final Object context)
        {
        }
    }
}
