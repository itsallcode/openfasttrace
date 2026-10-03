package org.itsallcode.openfasttrace.core.serviceloader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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

    @Test
    void testOfRejectsNullName() throws IOException
    {
        final Path jar = createJar("plugin.jar");
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Plugin.of(null, jar));
        assertThat(exception.getMessage(), equalTo("Plugin name must not be null or blank."));
    }

    @Test
    void testOfRejectsBlankName() throws IOException
    {
        final Path jar = createJar("plugin.jar");
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Plugin.of("  ", jar));
        assertThat(exception.getMessage(), equalTo("Plugin name must not be null or blank."));
    }

    @Test
    void testOfRejectsEmptyJarList()
    {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Plugin.of("my-plugin"));
        assertThat(exception.getMessage(), equalTo("At least one plugin JAR must be given for plugin 'my-plugin'."));
    }

    @Test
    void testOfRejectsNullJar()
    {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Plugin.of("my-plugin", (Path) null));
        assertThat(exception.getMessage(), equalTo("Plugin JAR path must not be null for plugin 'my-plugin'."));
    }

    @Test
    void testOfRejectsMissingJar()
    {
        final Path missing = tempDir.resolve("missing.jar");
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Plugin.of("my-plugin", missing));
        assertThat(exception.getMessage(),
                equalTo("Plugin JAR '" + missing + "' of plugin 'my-plugin' does not exist or is not a file."));
    }

    @Test
    void testOfRejectsNonJarFile() throws IOException
    {
        final Path file = tempDir.resolve("plugin.txt");
        Files.createFile(file);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Plugin.of("my-plugin", file));
        assertThat(exception.getMessage(),
                equalTo("Plugin file '" + file + "' of plugin 'my-plugin' is not a JAR file."));
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
