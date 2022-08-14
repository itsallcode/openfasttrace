package org.itsallcode.openfasttrace.api.importer;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestImporterContext
{
    @Mock
    private ImportSettings settingsMock;
    @Mock
    private ImporterService importerServiceMock;

    private ImporterContext context;

    @BeforeEach
    void beforeEach()
    {
        this.context = new ImporterContext(this.settingsMock);
    }

    @Test
    void testGetImporterConfig()
    {
        assertThat(this.context.getImportSettings(), sameInstance(this.settingsMock));
    }

    @Test
    void testGetImporterServiceNullByDefault()
    {
        assertThrows(NullPointerException.class, () -> this.context.getImporterService());
    }

    @Test
    void testGetImporterService()
    {
        this.context.setImporterService(this.importerServiceMock);
        assertThat(this.context.getImporterService(), sameInstance(this.importerServiceMock));
    }
}