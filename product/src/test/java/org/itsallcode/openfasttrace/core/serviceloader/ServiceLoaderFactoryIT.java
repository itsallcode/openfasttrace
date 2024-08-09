package org.itsallcode.openfasttrace.core.serviceloader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.api.report.ReporterFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.opentest4j.TestAbortedException;

/**
 * Test for {@link ServiceLoaderFactory} from module {@code core}. This test
 * must be located in module {@code product} (which includes all plugin modules)
 * so that it can access all plugin services.
 */
// [itest->dsn~plugins.loading~1]
class ServiceLoaderFactoryIT
{
    @TempDir
    Path tempDir;

    @Test
    void loadServiceFromWrongJar() throws IOException
    {
        preparePlugin(Path.of("../reporter/plaintext/target"),
                Pattern.compile("openfasttrace-reporter-plaintext-\\d\\.\\d\\.\\d\\-javadoc.jar"));
        try (Loader<ReporterFactory> loader = createLoader())
        {
            final List<ReporterFactory> service = loader.load().toList();
            assertThat(service, empty());
        }
    }

    private Loader<ReporterFactory> createLoader()
    {
        return new ServiceLoaderFactory(tempDir, false).createLoader(ReporterFactory.class);
    }

    @Test
    void loadServiceFromJar() throws IOException
    {
        preparePlugin(Path.of("../reporter/plaintext/target"),
                Pattern.compile("openfasttrace-reporter-plaintext-\\d\\.\\d\\.\\d\\.jar"));
        try (Loader<ReporterFactory> loader = createLoader())
        {
            final List<ReporterFactory> services = loader.load().toList();
            assertThat(services, hasSize(1));
            final ReporterFactory service = services.get(0);
            final ClassLoader pluginClassLoader = service.getClass().getClassLoader();
            assertAll(
                    () -> assertThat(service.getClass().getName().toString(),
                            equalTo("org.itsallcode.openfasttrace.report.plaintext.PlaintextReporterFactory")),
                    () -> assertThat(pluginClassLoader.getName(),
                            startsWith("JarClassLoader-openfasttrace-reporter-plaintext")),
                    () -> assertThat(pluginClassLoader,
                            not(sameInstance(Thread.currentThread().getContextClassLoader()))));
        }
    }

    private void preparePlugin(final Path targetDir, final Pattern filePattern) throws TestAbortedException, IOException
    {
        final Path jar = findMatchingFile(targetDir, filePattern)
                .orElseThrow(() -> new AssertionError(
                        "Did not file matching '" + filePattern + "' in '" + targetDir
                                + "'. Ensure the module was built with 'mvn package'."));
        preparePlugin(jar);
    }

    private Optional<Path> findMatchingFile(final Path dir, final Pattern filePattern) throws IOException
    {
        return Files.list(dir).filter(file -> filePattern.matcher(file.getFileName().toString()).matches())
                .findFirst();
    }

    private void preparePlugin(final Path pluginJar) throws IOException
    {
        final Path pluginDir = tempDir.resolve("plugin");
        Files.createDirectories(pluginDir);
        Files.copy(pluginJar, pluginDir.resolve(pluginJar.getFileName()));
    }
}
