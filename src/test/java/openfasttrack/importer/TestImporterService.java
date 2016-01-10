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
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;

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
        final Map<SpecificationItemId, SpecificationItem> result = this.importerService
                .importFile(this.file);

        verify(this.importerMock).runImport();

        final SpecificationMapListBuilder builder = getBuilder();
        assertThat(result, sameInstance(builder.build()));
    }

    private SpecificationMapListBuilder getBuilder()
    {
        final ArgumentCaptor<SpecificationMapListBuilder> arg = ArgumentCaptor
                .forClass(SpecificationMapListBuilder.class);
        verify(this.importerFactoryMock).createImporter(same(this.file),
                same(StandardCharsets.UTF_8), arg.capture());
        return arg.getValue();
    }
}
