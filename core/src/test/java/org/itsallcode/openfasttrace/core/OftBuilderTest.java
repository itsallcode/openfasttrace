package org.itsallcode.openfasttrace.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// [utest->dsn~plugins.loading.configuration~1]
class OftBuilderTest
{
    @TempDir
    Path tempDir;

    @Test
    void buildCreatesOft() throws IOException
    {
        final Path jar = createJar("plugin.jar");
        final Oft oft = Oft.builder().addPlugin("my-plugin", jar).build();
        assertThat(oft, instanceOf(OftRunner.class));
    }

    @Test
    void buildWithoutPluginsCreatesOft()
    {
        assertThat(Oft.builder().build(), notNullValue());
    }

    @Test
    void addPluginRejectsInvalidJar()
    {
        final Path missing = tempDir.resolve("missing.jar");
        final OftBuilder builder = Oft.builder();
        assertThrows(IllegalArgumentException.class, () -> builder.addPlugin("my-plugin", missing));
    }

    private Path createJar(final String fileName) throws IOException
    {
        final Path jar = tempDir.resolve(fileName);
        Files.createFile(jar);
        return jar;
    }
}
