package org.itsallcode.openfasttrace.importer.specobject;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.input.RealFileInput;
import org.itsallcode.openfasttrace.testutil.importer.ImporterFactoryTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Tests for {@link SpecobjectImporterFactory}
 */
class TestSpecobjectImporterFactory
        extends ImporterFactoryTestBase<SpecobjectImporterFactory>
{

    @Override
    protected SpecobjectImporterFactory createFactory()
    {
        return new SpecobjectImporterFactory();
    }

    /**
     * Only the {@code .oreqm} extension is always supported. That is why we
     * can't simply list {@code .xml} here. Whether {@code .xml} is supported
     * depends on a successful peek that confirms the file is in ReqM2 format.
     * 
     * @return list of files that need to pass the test for supported files
     */
    @Override
    protected List<String> getSupportedFilenames()
    {
        return asList("file.oreqm", "file.OREQM");
    }

    @Override
    protected List<String> getUnsupportedFilenames()
    {
        return asList("file.md", "file.xm", "file.ml", "file.1xml", "file.xml1", "file.xml.md",
                "file_xml", "filexml", "file_oreqm", "fileoreqm");
    }

    // [utest ->dsn~import.reqm2-file-detection~1]
    @Test
    void supportsXmlIfContentLooksLikeReqM2(@TempDir final Path tempDir) throws IOException
    {
        final Path tempFile = tempDir.resolve("reqm2-specdocument.xml");
        Files.writeString(tempFile, """
                <?xml version="1.0"?>
                <specdocument>
                     <specobjects doctype="REQ"/>
                </specdocument>
                """);
        final boolean supported = createFactory().supportsFile(RealFileInput.forPath(tempFile));
        assertThat(supported, equalTo(true));
    }

    // [utest ->dsn~import.reqm2-file-detection~1]
    @Test
    void doesNotSupportXmlIfContentIsGeneric(@TempDir final Path tempDir) throws IOException
    {
        final Path tempFile = tempDir.resolve("generic.xml");
        Files.writeString(tempFile, """
                <?xml version="1.0"?>
                <root>
                    <child/>
                </root>"
                """);
        final boolean supported = createFactory().supportsFile(RealFileInput.forPath(tempFile));
        assertThat(supported, equalTo(false));
    }

    // [utest ->dsn~import.reqm2-file-detection~1]
    @Test
    void givenEmptyFileWhenCheckingReqM2HeaderThenDoesNotClaimSupport(@TempDir final Path tempDir) throws IOException
    {
        final Path tempFile = tempDir.resolve("empty.xml");
        Files.writeString(tempFile, "");
        final boolean supported = createFactory().supportsFile(RealFileInput.forPath(tempFile));
        assertThat(supported, equalTo(false));
    }

    // [utest ->dsn~import.reqm2-file-detection~1]
    @Test
    void givenFileWhenIOExceptionOccursThenDoesNotClaimSupport(@Mock InputFile mockInputFile) throws IOException
    {
        Mockito.when(mockInputFile.createReader()).thenThrow(new IOException("This is an expected test exception"));
        final boolean supported = createFactory().supportsFile(mockInputFile);
        assertThat(supported, equalTo(false));
    }
}
