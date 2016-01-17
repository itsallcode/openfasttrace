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
        assertThat(services, hasSize(3));
        assertThat(services,
                contains(instanceOf(MarkdownImporterFactory.class), //
                        instanceOf(SpecobjectImporterFactory.class), //
                        instanceOf(TagImporterFactory.class)));
    }

    private <T> List<T> getRegisteredServices(final Class<T> type)
    {
        final ServiceLoaderWrapper<T> serviceLoader = ServiceLoaderWrapper.load(type);
        return StreamSupport.stream(serviceLoader.spliterator(), false).collect(toList());
    }
}
