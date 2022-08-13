package org.itsallcode.openfasttrace.importer.tag;

import static java.util.Arrays.asList;

import java.util.List;

import org.itsallcode.openfasttrace.api.importer.ImporterContext;
import org.itsallcode.openfasttrace.testutil.importer.ImporterFactoryTestBase;

/**
 * Tests for {@link TagImporterFactory}
 */
// [utest->dsn~import.full-coverage-tag~1]
class TestTagImporterFactory extends ImporterFactoryTestBase<TagImporterFactory>
{
    @Override
    protected TagImporterFactory createFactory()
    {
        final TagImporterFactory factory = new TagImporterFactory();
        factory.init(new ImporterContext(null));
        return factory;
    }

    @Override
    protected List<String> getSupportedFilenames()
    {
        return asList("file.java", "FILE.java", "file.md.java", "foo.bash", "foo.bar.bash",
                "foo.bat", "foo.java", "foo.c", "foo.C", "foo.c++", "foo.c#", "foo.cc", "foo.cfg",
                "foo.conf", "foo.cpp", "foo.cs", "foo.groovy", "foo.h", "foo.H", "foo.hh", "foo.h++",
                "foo.htm", "foo.html", "foo.ini", "foo.js", "foo.json", "foo.lua", "foo.m",
                "foo.mm", "foo.php", "foo.pl", "foo.pls", "foo.pm", "foo.py", "foo.sql", "foo.r",
                "foo.rs", "foo.sh", "foo.yaml", "foo.xhtml", "foo.zsh", "foo.clj", "foo.kt", "foo.scala",
                "foo.pu", "foo.puml", "foo.plantuml", "foo.go", "foo.robot");
    }

    @Override
    protected List<String> getUnsupportedFilenames()
    {
        return asList("file.md", "file.jav", "file.ml", "file.1java", "file.java1", "file.java.md",
                "file_java", "filejava");
    }
}
