package org.itsallcode.openfasttrace.core.serviceloader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import nl.jqno.equalsverifier.EqualsVerifier;

// [utest->dsn~plugins.loading.configuration~1]
class PluginTest
{
    @TempDir
    Path tempDir;

    @Test
    void testOfCreatesPluginWithNameAndJars() throws IOException
    {
        final Path jar = createJar("plugin.jar");
        final Plugin plugin = Plugin.of("my-plugin", jar);
        assertAll(
                () -> assertThat(plugin.getName(), equalTo("my-plugin")),
                () -> assertThat(plugin.getJars(), contains(jar)));
    }

    @Test
    void testOfSupportsMultipleJars() throws IOException
    {
        final Path jar1 = createJar("plugin.jar");
        final Path jar2 = createJar("dependency.jar");
        final Plugin plugin = Plugin.of("my-plugin", jar1, jar2);
        assertThat(plugin.getJars(), contains(jar1, jar2));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidPlugins")
    void testOfRejectsInvalidInput(final String description, final Function<Path, Plugin> factory,
            final Function<Path, String> expectedMessage) throws IOException
    {
        final Path jar = createJar("plugin.jar");
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.apply(jar));
        assertThat(exception.getMessage(), equalTo(expectedMessage.apply(jar)));
    }

    private static Stream<Arguments> invalidPlugins()
    {
        return Stream.of(
                Arguments.of("null name", (Function<Path, Plugin>) jar -> Plugin.of(null, jar),
                        (Function<Path, String>) jar -> "Plugin name must not be null or blank."),
                Arguments.of("blank name", (Function<Path, Plugin>) jar -> Plugin.of("  ", jar),
                        (Function<Path, String>) jar -> "Plugin name must not be null or blank."),
                Arguments.of("empty jar list", (Function<Path, Plugin>) jar -> Plugin.of("my-plugin"),
                        (Function<Path, String>) jar -> "At least one plugin JAR must be given for plugin 'my-plugin'."),
                Arguments.of("null jar", (Function<Path, Plugin>) jar -> Plugin.of("my-plugin", (Path) null),
                        (Function<Path, String>) jar -> "Plugin JAR path must not be null for plugin 'my-plugin'."),
                Arguments.of("missing jar", (Function<Path, Plugin>) jar -> Plugin.of("my-plugin",
                        jar.resolveSibling("missing.jar")),
                        (Function<Path, String>) jar -> "Plugin JAR '" + jar.resolveSibling("missing.jar")
                                + "' of plugin 'my-plugin' does not exist or is not a file."),
                Arguments.of("non-jar file", (Function<Path, Plugin>) PluginTest::ofNonJarFile,
                        (Function<Path, String>) jar -> "Plugin file '" + jar.resolveSibling("plugin.txt")
                                + "' of plugin 'my-plugin' is not a JAR file."));
    }

    private static Plugin ofNonJarFile(final Path jar)
    {
        final Path file = jar.resolveSibling("plugin.txt");
        try
        {
            Files.createFile(file);
        }
        catch (final IOException exception)
        {
            throw new UncheckedIOException(exception);
        }
        return Plugin.of("my-plugin", file);
    }

    @Test
    void testEqualsContract()
    {
        EqualsVerifier.forClass(Plugin.class).verify();
    }

    private Path createJar(final String fileName) throws IOException
    {
        final Path jar = tempDir.resolve(fileName);
        Files.createFile(jar);
        return jar;
    }
}
