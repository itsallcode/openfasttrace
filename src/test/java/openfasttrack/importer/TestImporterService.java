package openfasttrack.importer;

import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import openfasttrack.core.SpecificationItem;

/**
 * Test for {@link ImporterService}
 */
public class TestImporterService
{
    @Mock
    private ImporterFactoryLoader factoryLoaderMock;
    @Mock
    private ImporterFactory importerFactoryMock;
    @Mock
    private Importer importerMock;

    private Path file;
    private ImporterService importerService;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        this.importerService = new ImporterService(this.factoryLoaderMock);
        this.file = Paths.get("dir", "file");

        when(this.factoryLoaderMock.getImporterFactory(same(this.file)))
                .thenReturn(this.importerFactoryMock);
        when(this.importerFactoryMock.createImporter(same(this.file), same(StandardCharsets.UTF_8),
                any())).thenReturn(this.importerMock);
    }

    @Test
    public void testImport()
    {
        final List<SpecificationItem> result = this.importerService.importFile(this.file);

        verify(this.importerMock).runImport();

        final SpecificationItemListBuilder builder = getBuilder();
        assertThat(result, sameInstance(builder.build()));
    }

    private SpecificationItemListBuilder getBuilder()
    {
        final ArgumentCaptor<SpecificationItemListBuilder> arg = ArgumentCaptor
                .forClass(SpecificationItemListBuilder.class);
        verify(this.importerFactoryMock).createImporter(same(this.file),
                same(StandardCharsets.UTF_8), arg.capture());
        return arg.getValue();
    }
}
