package org.itsallcode.openfasttrace.importer.specobject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.argumentSet;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.xmlparser.XmlParserFactory;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

class TestSpecobjectImporter
{
    private static final String PSEUDO_FILENAME = "pseudo_filename";
    private static final Location STANDARD_LOCATION = Location.create(PSEUDO_FILENAME, 2);

    static List<Arguments> idImportChecks = List.of(
            argumentSet("minimal import", """
                    <specobjects doctype="req">
                      <specobject>
                        <id>minimal</id>
                        <version>1</version>
                      </specobject>
                    </specobjects>""",
                    "req~minimal~1"),

            argumentSet("strip superfluous artifact type", """
                    <specobjects doctype="impl">
                      <specobject>
                        <id>impl:strip_duplicate_prefix</id>
                        <version>0</version>
                      </specobject>
                    </specobjects>""",
                    "impl~strip_duplicate_prefix~0"),

            argumentSet("ignore unknown XML elements", """
                    <specobjects doctype="feat">
                      <specobject>
                        <id>ignore-selected-xml-elements</id>
                        <version>12345</version>
                        <creationdate>1970-01-01</creationdate>
                        <source>john doe</source>
                      </specobject>
                    </specobjects>""",
                    "feat~ignore-selected-xml-elements~12345"),

            argumentSet("fullfilled-by is ignored", """
                    <specobjects doctype="req">
                      <specobject>
                        <id>with-fulfilled-by</id>
                        <version>1</version>
                        <fulfilledby>
                          <ffbObj>
                            <ffbType>impl</ffbType>
                            <ffbId>ffb-a</ffbId>
                            <ffbVersion>1</ffbVersion>
                          </ffbObj>
                          <ffbObj>
                            <ffbType>utest</ffbType>
                            <ffbId>ffb-b</ffbId>
                            <ffbVersion>2</ffbVersion>
                          </ffbObj>
                        </fulfilledby>
                      </specobject>
                    </specobjects>""", "req~with-fulfilled-by~1"));

    @ParameterizedTest
    @FieldSource("idImportChecks")
    void testImportSeesTheCorrectId(final String input, final String expectedId)
    {
        final ImportEventListener listenerMock = importFromString(input);
        verify(listenerMock).beginSpecificationItem();
        verify(listenerMock).setLocation(TestSpecobjectImporter.STANDARD_LOCATION);
        verify(listenerMock).setId(SpecificationItemId.parseId(expectedId));
        verify(listenerMock).endSpecificationItem();
        verifyNoMoreInteractions(listenerMock);
    }

    private ImportEventListener importFromString(final String text)
    {
        final ImportEventListener listenerMock = mock(ImportEventListener.class);
        final StringReader stringReader = new StringReader(text);
        final InputFile file = StreamInput.forReader(Paths.get(PSEUDO_FILENAME),
                new BufferedReader(stringReader));
        final SpecobjectImporter importer = new SpecobjectImporter(file, new XmlParserFactory(),
                listenerMock);
        importer.runImport();
        return listenerMock;
    }

    @Test
    void testImportOfComplexSpecObject()
    {
        final ImportEventListener listenerMock = importFromString("""
                <specobjects doctype="req">
                  <specobject>
                    <id>complex</id>
                    <status>draft</status>\
                    <version>2</version>
                    <shortdesc>my short description</shortdesc>
                    <description>multiline description
                one more line</description>
                    <rationale>multiline rationale
                and another line</rationale>\
                    <comment>multiline comment
                yet another line</comment>
                  </specobject>
                </specobjects>""");
        verify(listenerMock).beginSpecificationItem();
        verify(listenerMock).setLocation(STANDARD_LOCATION);
        verify(listenerMock).setStatus(ItemStatus.DRAFT);
        verify(listenerMock).setId(SpecificationItemId.parseId("req~complex~2"));
        verify(listenerMock).setTitle("my short description");
        verify(listenerMock).appendDescription("multiline description\none more line");
        verify(listenerMock).appendRationale("multiline rationale\nand another line");
        verify(listenerMock).appendComment("multiline comment\nyet another line");
        verify(listenerMock).endSpecificationItem();
        verifyNoMoreInteractions(listenerMock);
    }

    @Test
    void testImportOnlyShortDescription()
    {
        final ImportEventListener listenerMock = importFromString("""
                <specobjects doctype="req">
                  <specobject>
                    <id>complex</id>
                    <version>2</version>
                    <shortdesc>My item title</shortdesc>
                  </specobject>
                </specobjects>""");
        verify(listenerMock).beginSpecificationItem();
        verify(listenerMock).setLocation(STANDARD_LOCATION);
        verify(listenerMock).setId(SpecificationItemId.parseId("req~complex~2"));
        verify(listenerMock).setTitle("My item title");
        verify(listenerMock).endSpecificationItem();
        verifyNoMoreInteractions(listenerMock);
    }

    @Test
    void testStripSuperfluousArtifactPrefixFromName()
    {
        final ImportEventListener listenerMock = importFromString("""
                <specobjects doctype="impl">
                  <specobject>
                    <id>impl:strip_duplicate_prefix</id>
                    <version>0</version>
                  </specobject>
                </specobjects>""");
        verify(listenerMock).beginSpecificationItem();
        verify(listenerMock).setLocation(STANDARD_LOCATION);
        verify(listenerMock).setId(SpecificationItemId.parseId("impl~strip_duplicate_prefix~0"));
        verify(listenerMock).endSpecificationItem();
        verifyNoMoreInteractions(listenerMock);
    }

    @Test
    void testTakeOverLocationFromImportedFile()
    {
        final String expectedFileName = "/home/johndoe/openfasttrace/examples/specobject.xml";
        final int expectedLine = 42;
        final ImportEventListener listenerMock = importFromString("<specobjects doctype=\"utest\">\n" //
                + "  <specobject>\n" //
                + "    <id>takeOverLocation</id>\n" //
                + "    <version>99999999</version>\n" //
                + "    <sourcefile>" + expectedFileName + "</sourcefile>" //
                + "    <sourceline>" + expectedLine + "</sourceline>" // "
                + "  </specobject>\n" //
                + "</specobjects>");
        verify(listenerMock).beginSpecificationItem();
        verify(listenerMock).setLocation(Location.create(expectedFileName, expectedLine));
        verify(listenerMock).setId(SpecificationItemId.parseId("utest~takeOverLocation~99999999"));
        verify(listenerMock).endSpecificationItem();
        verifyNoMoreInteractions(listenerMock);
    }

    @Test
    void testImportWithTags()
    {
        final ImportEventListener listenerMock = importFromString("""
                <specobjects doctype="itest">
                  <specobject>
                    <id>with-tags</id>
                    <version>1</version>
                    <tags>
                      <tag>tag 1</tag>
                      <tag>tag 2</tag>
                    </tags>
                  </specobject>
                </specobjects>""");
        verify(listenerMock).beginSpecificationItem();
        verify(listenerMock).setLocation(STANDARD_LOCATION);
        verify(listenerMock).setId(SpecificationItemId.parseId("itest~with-tags~1"));
        verify(listenerMock).addTag("tag 1");
        verify(listenerMock).addTag("tag 2");
        verify(listenerMock).endSpecificationItem();
        verifyNoMoreInteractions(listenerMock);
    }

    @Test
    void testImportWithNeedsCoverage()
    {
        final ImportEventListener listenerMock = importFromString("""
                <specobjects doctype="req">
                  <specobject>
                    <id>with-needs-coverage</id>
                    <version>1</version>
                    <needscoverage>
                      <needsobj>impl</needsobj>
                      <needsobj>utest</needsobj>
                    </needscoverage>
                  </specobject>
                </specobjects>""");
        verify(listenerMock).beginSpecificationItem();
        verify(listenerMock).setLocation(STANDARD_LOCATION);
        verify(listenerMock).setId(SpecificationItemId.parseId("req~with-needs-coverage~1"));
        verify(listenerMock).addNeededArtifactType("impl");
        verify(listenerMock).addNeededArtifactType("utest");
        verify(listenerMock).endSpecificationItem();
        verifyNoMoreInteractions(listenerMock);
    }

    @Test
    void testImportWithDependencies()
    {
        final ImportEventListener listenerMock = importFromString("""
                <specobjects doctype="req">
                  <specobject>
                    <id>with-dependencies</id>
                    <version>1</version>
                    <dependencies>
                      <dependson>req:dep-a, v1</dependson>
                      <dependson>req:dep-b, v2</dependson>
                    </dependencies>
                  </specobject>
                </specobjects>""");
        verify(listenerMock).beginSpecificationItem();
        verify(listenerMock).setLocation(STANDARD_LOCATION);
        verify(listenerMock).setId(SpecificationItemId.parseId("req~with-dependencies~1"));
        verify(listenerMock).addDependsOnId(SpecificationItemId.parseId("req~dep-a~1"));
        verify(listenerMock).addDependsOnId(SpecificationItemId.parseId("req~dep-b~2"));
        verify(listenerMock).endSpecificationItem();
        verifyNoMoreInteractions(listenerMock);
    }

    @Test
    void testImportProvidesCoverage()
    {
        final ImportEventListener listenerMock = importFromString("""
                <specobjects doctype="req">
                  <specobject>
                    <id>with-fulfilled-by</id>
                    <version>1</version>
                    <providescoverage>
                      <provcov>
                        <linksto>feat:provides-a</linksto>
                        <dstversion>1</dstversion>
                      </provcov>
                      <provcov>
                        <linksto>feat:provides-b</linksto>
                        <dstversion>2</dstversion>
                      </provcov>
                    </providescoverage>
                  </specobject>
                </specobjects>""");
        verify(listenerMock).beginSpecificationItem();
        verify(listenerMock).setLocation(STANDARD_LOCATION);
        verify(listenerMock).setId(SpecificationItemId.parseId("req~with-fulfilled-by~1"));
        verify(listenerMock).addCoveredId(SpecificationItemId.parseId("feat~provides-a~1"));
        verify(listenerMock).addCoveredId(SpecificationItemId.parseId("feat~provides-b~2"));
        verify(listenerMock).endSpecificationItem();
        verifyNoMoreInteractions(listenerMock);
    }

    @Test
    void testCustomTagsWithoutNamespacesLogsWarning()
    {
        try (final RecordingLogHandler logHandler = new RecordingLogHandler())
        {
            importFromString("""
                    <specdocument>\
                      <specobjects doctype="req">
                        <specobject>
                          <id>minimal</id>
                          <version>1</version>
                        </specobject>
                      </specobjects>
                      <extension>
                      </extension>
                    </specdocument>""");
            final Optional<LogRecord> firstRecord = logHandler.getLogRecords()
                    .filter(logRecord -> logRecord.getLevel() == Level.WARNING)
                    .findFirst();
            assertTrue(firstRecord.isPresent());
            assertThat(firstRecord.get().getMessage(), containsString("Found unknown"));
        }
    }

    @Test
    void testCustomTagsWithNamespacesLogsNoWarning()
    {
        try (final RecordingLogHandler logHandler = new RecordingLogHandler())
        {
            importFromString("""
                    <specdocument xmlns:x="http://extension">
                      <specobjects doctype="req">
                        <specobject>
                          <id>minimal</id>
                          <version>1</version>
                          <x:extension>
                          </x:extension>
                        </specobject>
                      </specobjects>
                      <x:extension>
                      </x:extension>
                    </specdocument>""");
            assertThat(logHandler.getLogRecords().count(), equalTo(0L));
        }
    }

    @Test
    void testCustomTagsWithNamespacesPlusOftNamespaceLogsNoWarning()
    {
        try (final RecordingLogHandler logHandler = new RecordingLogHandler())
        {
            importFromString("""
                    <specdocument xmlns="https://github.com/itsallcode/openfasttrace"
                             xmlns:x="http://extension">
                      <specobjects doctype="req">
                        <specobject>
                          <id>minimal</id>
                          <version>1</version>
                        </specobject>
                      </specobjects>
                      <x:extension>
                      </x:extension>
                    </specdocument>""");
            assertThat(logHandler.getLogRecords().count(), equalTo(0L));
        }
    }

    private static class RecordingLogHandler extends ConsoleHandler implements AutoCloseable
    {

        private final Logger rootLogger;
        private final List<LogRecord> logRecords = new ArrayList<>();

        public RecordingLogHandler()
        {
            super();
            rootLogger = Logger.getLogger("");
            rootLogger.setLevel(Level.WARNING);
            rootLogger.addHandler(this);
        }

        @Override
        public void publish(final LogRecord logRecord)
        {
            logRecords.add(logRecord);
            super.publish(logRecord);
        }

        public Stream<LogRecord> getLogRecords()
        {
            return logRecords.stream();
        }

        @Override
        public void close()
        {
            Arrays.stream(rootLogger.getHandlers())
                    .filter((RecordingLogHandler.class::isInstance))
                    .forEach(rootLogger::removeHandler);
            super.close();
        }
    }
}
