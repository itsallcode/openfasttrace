package org.itsallcode.openfasttrace.core.serviceloader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.itsallcode.openfasttrace.api.report.ReporterFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.opentest4j.MultipleFailuresError;

// [utest->dsn~plugins.loading~1]
class ServiceLoaderFactoryTest
{
    @TempDir
    Path tempDir;

    @Test
    void noPluginJar()
    {
        assertThat(factory().createLoader(ReporterFactory.class).load().toList(), empty());
    }

    private ServiceLoaderFactory factory()
    {
        return new ServiceLoaderFactory(tempDir, true);
    }

    @Test
    void findServiceSkipCurrentClassLoader()
    {
        final Path missingDirectory = tempDir.resolve("missing-dir");
        assertThat(new ServiceLoaderFactory(missingDirectory, false).findServiceOrigins(), empty());
    }

    @Test
    void findServiceMissingParentDirFindsNoPlugins() throws IOException
    {
        Files.delete(tempDir);
        final List<ServiceOrigin> origins = factory().findServiceOrigins();
        assertNoPlugins(origins);
    }

    private void assertNoPlugins(final List<ServiceOrigin> origins) throws MultipleFailuresError
    {
        assertThat(origins, contains(
                hasProperty("classLoader",
                        hasProperty("name", equalTo("app")))));
    }

    @Test
    void findServiceOriginsPluginDir()
    {
        final List<ServiceOrigin> origins = factory().findServiceOrigins();
        assertNoPlugins(origins);
    }

    @Test
    void findServiceOriginsEmptyPluginDir() throws IOException
    {
        final Path pluginDir = tempDir.resolve("plugin1");
        Files.createDirectories(pluginDir);
        final List<ServiceOrigin> origins = factory().findServiceOrigins();
        assertNoPlugins(origins);
    }

    @Test
    void findServiceOriginsIgnoresNonJarFiles() throws IOException
    {
        final Path pluginDir = tempDir.resolve("plugin1");
        Files.createDirectories(pluginDir);
        Files.createFile(pluginDir.resolve("plugin1.txt"));
        final List<ServiceOrigin> origins = factory().findServiceOrigins();
        assertNoPlugins(origins);
    }

    @Test
    void findServiceOriginsIgnoresDirectories() throws IOException
    {
        final Path pluginDir = tempDir.resolve("plugin1");
        Files.createDirectories(pluginDir.resolve("ignored-directory"));
        final List<ServiceOrigin> origins = factory().findServiceOrigins();
        assertNoPlugins(origins);
    }

    @Test
    void findServiceOriginsSingleJar() throws IOException
    {
        final Path pluginDir = tempDir.resolve("plugin1");
        Files.createDirectories(pluginDir);
        Files.createFile(pluginDir.resolve("plugin1.jar"));
        final List<ServiceOrigin> origins = factory().findServiceOrigins();
        assertAll(() -> assertThat(origins, hasSize(2)),
                () -> assertThat(origins.get(0).getClassLoader().getName(), equalTo("JarClassLoader-plugin1.jar")));
    }

    @Test
    void findServiceOriginsMultiJar() throws IOException
    {
        final Path pluginDir = tempDir.resolve("plugin1");
        Files.createDirectories(pluginDir);
        Files.createFile(pluginDir.resolve("plugin1.jar"));
        Files.createFile(pluginDir.resolve("plugin2.jar"));
        final List<ServiceOrigin> origins = factory().findServiceOrigins();
        assertAll(() -> assertThat(origins, hasSize(2)),
                () -> assertThat(origins.get(0).getClassLoader().getName(),
                        equalTo("JarClassLoader-plugin1.jar,plugin2.jar")));
    }

    @Test
    void findServiceOriginsMultiPlugins() throws IOException
    {
        final Path pluginDir1 = tempDir.resolve("plugin1");
        final Path pluginDir2 = tempDir.resolve("plugin2");
        Files.createDirectories(pluginDir1);
        Files.createDirectories(pluginDir2);
        Files.createFile(pluginDir1.resolve("plugin1.jar"));
        Files.createFile(pluginDir2.resolve("plugin2.jar"));
        final List<ServiceOrigin> origins = factory().findServiceOrigins();
        assertAll(() -> assertThat(origins, hasSize(3)),
                () -> assertThat(origins.get(0).getClassLoader().getName(),
                        equalTo("JarClassLoader-plugin1.jar")),
                () -> assertThat(origins.get(1).getClassLoader().getName(),
                        equalTo("JarClassLoader-plugin2.jar")));
    }
}
