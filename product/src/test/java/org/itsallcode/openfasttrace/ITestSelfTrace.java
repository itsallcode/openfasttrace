package org.itsallcode.openfasttrace;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.ImportSettings;
import org.itsallcode.openfasttrace.core.Oft;
import org.itsallcode.openfasttrace.core.OftRunner;
import org.itsallcode.openfasttrace.core.cli.StandardDirectoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ITestSelfTrace
{
    private Oft oft;

    @BeforeEach
    void beforeEach()
    {
        this.oft = new OftRunner();
    }

    @Test
    void testSelfTrace()
    {
        final ImportSettings importSettings = buildOftSettings();
        final Trace trace = trace(importSettings);
        this.oft.reportToStdOut(trace);
        assertSelfTraceClean(trace);
    }

    private void assertSelfTraceClean(final Trace trace)
    {
        if (!trace.hasNoDefects())
        {
            final String message = createSelfTraceReport(trace);
            throw new AssertionError(message);
        }
    }

    protected String createSelfTraceReport(final Trace trace)
    {
        String message = "Self trace has " + trace.countDefects() + " / " + trace.count()
                + " defect items.\n\n";
        for (final LinkedSpecificationItem item : trace.getDefectItems())
        {
            final Location location = item.getLocation();
            message += "at " + item.getId() + " (" + location.getPath() + ":" + location.getLine()
                    + ")\n";
        }
        return message;
    }

    private ImportSettings buildOftSettings()
    {
        final Path baseDir = Paths.get(new StandardDirectoryService().getCurrent()).resolve("..");
        return ImportSettings.builder() //
                .addInputs(baseDir.resolve("doc/design.md")) //
                .addInputs(baseDir.resolve("doc/system_requirements.md")) //
                .addInputs(findInputDirectories(baseDir)) //
                .build();
    }

    private List<Path> findInputDirectories(Path rootProjectDir)
    {
        try (final Stream<Path> stream = Files.walk(rootProjectDir.normalize(), 5))
        {
            return stream //
                    .filter(containsDir("bin").negate()) //
                    .filter(path -> Files.isDirectory(path)) //
                    .filter(endsWith(Paths.get("src/main")) //
                            .or(endsWith(Paths.get("src/test/java")))) //
                    .collect(toList());
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error listing directory " + rootProjectDir, e);
        }
    }

    private Predicate<Path> containsDir(String dirName)
    {
        return path -> path.toString().contains(File.separator + dirName + File.separator);
    }

    private Predicate<Path> endsWith(Path subPath)
    {
        return path -> path.toString().endsWith(File.separator + subPath.toString());
    }

    private Trace trace(final ImportSettings importSettings)
    {
        final List<SpecificationItem> items = this.oft.importItems(importSettings);
        final List<LinkedSpecificationItem> linkedItems = this.oft.link(items);
        return this.oft.trace(linkedItems);
    }
}
