package openfasttrack.importer;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.stream.StreamSupport;

import org.junit.Test;

import openfasttrack.importer.ImporterFactoryLoader.ServiceLoaderWrapper;
import openfasttrack.importer.markdown.MarkdownImporterFactory;
import openfasttrack.importer.specobject.SpecobjectImporterFactory;

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
        final ServiceLoaderWrapper<ImporterFactory> serviceLoader = ServiceLoaderWrapper
                .load(ImporterFactory.class);
        final List<ImporterFactory> services = StreamSupport
                .stream(serviceLoader.spliterator(), false).collect(toList());
        assertThat(services, hasSize(2));
        assertThat(services, contains(instanceOf(MarkdownImporterFactory.class),
                instanceOf(SpecobjectImporterFactory.class)));
    }
}
