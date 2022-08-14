package org.itsallcode.openfasttrace.importer.zip;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.itsallcode.openfasttrace.api.importer.ImporterService;
import org.itsallcode.openfasttrace.testutil.importer.ImporterFactoryTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class TestZipFileImporterFactory extends ImporterFactoryTestBase<ZipFileImporterFactory>
{
    @Mock
    private ImporterService importerServiceMock;

    @BeforeEach
    public void configureMock()
    {
        when(this.contextMock.getImporterService()).thenReturn(this.importerServiceMock);
    }

    @Test
    void testConstructor()
    {
        assertThat(createFactory(), notNullValue());
    }

    @Override
    protected ZipFileImporterFactory createFactory()
    {
        return new ZipFileImporterFactory();
    }

    @Override
    protected List<String> getSupportedFilenames()
    {
        return asList("blah.zip", "a.ZIP");
    }

    @Override
    protected List<String> getUnsupportedFilenames()
    {
        return asList("x.java", "y.text", "z.gz", "a.zp");
    }

    @Test
    void testFactoryThrowsExceptionWhenContextMissing()
    {
        assertThrows(NullPointerException.class,
                () -> new ZipFileImporterFactory().createImporter(null, null),
                "Context was not initialized");
    }
}