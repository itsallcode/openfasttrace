package org.itsallcode.openfasttrace.nativeimage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.itsallcode.process.SimpleProcess;
import org.itsallcode.process.SimpleProcessBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

class NativeImageIT
{
    private static final String RESOURCE_DIRECTORY = "/native-image/";
    private static final Duration PROCESS_TIMEOUT = Duration.ofSeconds(60);
    private static final String NATIVE_IMAGE_PATH_PROPERTY = "native.image.path";

    // [itest->dsn~cli.native-executable~1]
    @ParameterizedTest
    @MethodSource("builtInImporterFixtures")
    void testBuiltInImporterReadsItsFormat(final String fixtureName, final String expectedId,
            @TempDir final Path temporaryDirectory) throws IOException, InterruptedException
    {
        final Path fixture = copyFixture(fixtureName, temporaryDirectory);
        final List<String> additionalInputs = fixtureName.equals("sample.java")
                ? List.of(copyFixture("sample-tag-requirement.md", temporaryDirectory).toString())
                : List.of();
        final List<String> arguments = new ArrayList<>(List.of("trace", "--report-verbosity", "all",
                fixture.toString()));
        arguments.addAll(additionalInputs);

        final String output = runNativeImage(arguments.toArray(String[]::new));

        assertThat("imported item", output, containsString(expectedId));
    }

    static Stream<Arguments> builtInImporterFixtures()
    {
        return Stream.of(
                Arguments.of("sample.md", "req~native-image-markdown~1"),
                Arguments.of("sample.rst", "req~native-image-rst~1"),
                Arguments.of("sample.oreqm", "req~native-image-specobject~1"),
                Arguments.of("sample.feature", "scn~native-image-gherkin~1"),
                Arguments.of("sample.java", "impl~native-image-tag~1"));
    }

    // [itest->dsn~cli.native-executable~1]
    @Test
    void testBuiltInZipImporterReadsMarkdownEntry(@TempDir final Path temporaryDirectory)
            throws IOException, InterruptedException
    {
        final Path markdown = copyFixture("sample.md", temporaryDirectory);
        final Path archive = temporaryDirectory.resolve("sample.zip");
        createZipArchive(markdown, archive);

        final String output = runNativeImage("trace", "--report-verbosity", "all",
                archive.toString());

        assertThat("item imported from archive", output, containsString("req~native-image-markdown~1"));
    }

    // [itest->dsn~cli.native-executable.support-built-in-plugins~1]
    @ParameterizedTest
    @ValueSource(strings =
    { "plain", "html", "aspec" })
    void testBuiltInReporterProducesItsOutput(final String format, @TempDir final Path temporaryDirectory)
            throws IOException, InterruptedException
    {
        final Path fixture = copyFixture("sample.md", temporaryDirectory);

        final String output = runNativeImage("trace", "--report-verbosity", "all",
                "--output-format", format, fixture.toString());

        assertThat("report contains imported item", output, containsString("native-image-markdown"));
        if (format.equals("html"))
        {
            assertThat("HTML report", output, containsString("<!DOCTYPE html>"));
        }
        else if (format.equals("aspec"))
        {
            assertThat("SpecObject report", output, containsString("<specdocument"));
        }
    }

    // [itest->dsn~cli.native-executable.support-built-in-plugins~1]
    @Test
    void testBuiltInSpecObjectExporterProducesDocument(@TempDir final Path temporaryDirectory)
            throws IOException, InterruptedException
    {
        final Path fixture = copyFixture("sample.md", temporaryDirectory);

        final String output = runNativeImage("convert", "--output-format", "specobject",
                fixture.toString());

        assertThat("SpecObject output", output, containsString("<specdocument"));
    }

    private static String runNativeImage(final String... arguments)
            throws IOException, InterruptedException
    {
        final Path executable = nativeExecutablePath();
        assertThat("native executable exists at " + executable, Files.isRegularFile(executable), is(true));

        final List<String> command = new ArrayList<>();
        command.add(executable.toAbsolutePath().toString());
        command.addAll(List.of(arguments));
        final SimpleProcess<String> process = SimpleProcessBuilder.create()
                .command(command)
                .redirectErrorStream(true)
                .start();
        process.waitForTermination(PROCESS_TIMEOUT);
        final String output = process.getStdOut();
        assertThat("native executable output: " + output, process.exitValue(), equalTo(0));
        return output;
    }

    private static Path nativeExecutablePath()
    {
        final String configuredPath = System.getProperty(NATIVE_IMAGE_PATH_PROPERTY, "target/openfasttrace");
        final Path executable = Path.of(configuredPath);
        if (isWindows() && !configuredPath.toLowerCase().endsWith(".exe"))
        {
            return Path.of(configuredPath + ".exe");
        }
        return executable;
    }

    private static boolean isWindows()
    {
        return System.getProperty("os.name", "").toLowerCase().contains("windows");
    }

    private static Path copyFixture(final String fixtureName, final Path targetDirectory) throws IOException
    {
        final Path target = targetDirectory.resolve(fixtureName);
        try (InputStream fixture = Objects.requireNonNull(
                NativeImageIT.class.getResourceAsStream(RESOURCE_DIRECTORY + fixtureName)))
        {
            Files.copy(fixture, target);
        }
        return target;
    }

    private static void createZipArchive(final Path fileToArchive, final Path archive) throws IOException
    {
        try (ZipOutputStream zipOutput = new ZipOutputStream(Files.newOutputStream(archive)))
        {
            zipOutput.putNextEntry(new ZipEntry(fileToArchive.getFileName().toString()));
            Files.copy(fileToArchive, zipOutput);
            zipOutput.closeEntry();
        }
    }
}
