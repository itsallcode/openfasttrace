package org.itsallcode.openfasttrace.importer.specobject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Stream;

import javax.xml.parsers.SAXParserFactory;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.xmlparser.SaxParserConfigurator;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;

class TestSpecobjectImporter
{
    private static final String PSEUDO_FILENAME = "pseudo_filename";
    private static final Location STANDARD_LOCATION = Location.create(PSEUDO_FILENAME, 2);

    @Test
    void testImportOfMinimalSpecObject()
    {
        final ImportEventListener listenerMock = importFromString("<specobjects doctype=\"req\">\n" //
                + "  <specobject>\n" //
                + "    <id>minimal</id>\n" //
                + "    <version>1</version>\n" //
                + "  </specobject>\n" //
                + "</specobjects>");
        verify(listenerMock).beginSpecificationItem();
        verify(listenerMock).setLocation(STANDARD_LOCATION);
        verify(listenerMock).setId(SpecificationItemId.parseId("req~minimal~1"));
        verify(listenerMock).endSpecificationItem();
        verifyNoMoreInteractions(listenerMock);
    }

    private ImportEventListener importFromString(final String text)
    {
        final ImportEventListener listenerMock = mock(ImportEventListener.class);
        final SAXParserFactory saxParserFactory = SaxParserConfigurator.createSaxParserFactory();
        final StringReader stringReader = new StringReader(text);
        final InputFile file = StreamInput.forReader(Paths.get(PSEUDO_FILENAME),
                new BufferedReader(stringReader));
        final SpecobjectImporter importer = new SpecobjectImporter(file, saxParserFactory,
                listenerMock);
        importer.runImport();
        return listenerMock;
    }

    @Test
    void testImportOfComplexSpecObject()
    {
        final ImportEventListener listenerMock = importFromString("<specobjects doctype=\"req\">\n" //
                + "  <specobject>\n" //
                + "    <id>complex</id>\n" //
                + "    <status>draft</status>" //
                + "    <version>2</version>\n" //
                + "    <shortdesc>my short description</shortdesc>\n" //
                + "    <description>multiline description\n" //
                + "one more line</description>\n" //
                + "    <rationale>multiline rationale\n" //
                + "and another line</rationale>" //
                + "    <comment>multiline comment\n" //
                + "yet another line</comment>\n" //
                + "  </specobject>\n" //
                + "</specobjects>");
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
        final ImportEventListener listenerMock = importFromString("<specobjects doctype=\"req\">\n" //
                + "  <specobject>\n" //
                + "    <id>complex</id>\n" //
                + "    <version>2</version>\n" //
                + "    <shortdesc>My item title</shortdesc>\n" //
                + "  </specobject>\n" //
                + "</specobjects>");
        verify(listenerMock).beginSpecificationItem();
        verify(listenerMock).setLocation(STANDARD_LOCATION);
        verify(listenerMock).setId(SpecificationItemId.parseId("req~complex~2"));
        verify(listenerMock).setTitle("My item title");
        verify(listenerMock).endSpecificationItem();
        verifyNoMoreInteractions(listenerMock);
    }

    @Test
    void testSelectedElementsAreIgnoredDuringImport()
    {
        final ImportEventListener listenerMock = importFromString("<specobjects doctype=\"feat\">\n" //
                + "  <specobject>\n" //
                + "    <id>ignore-selected-xml-elements</id>\n" //
                + "    <version>12345</version>\n" //
                + "    <creationdate>1970-01-01</creationdate>\n"
                + "    <source>john doe</source>\n" //
                + "  </specobject>\n" //
                + "</specobjects>");
        verify(listenerMock).beginSpecificationItem();
        verify(listenerMock).setLocation(STANDARD_LOCATION);
        verify(listenerMock)
                .setId(SpecificationItemId.parseId("feat~ignore-selected-xml-elements~12345"));
        verify(listenerMock).endSpecificationItem();
        verifyNoMoreInteractions(listenerMock);
    }

    @Test
    void testStripSuperfluousArtifactPrefixFromName()
    {
        final ImportEventListener listenerMock = importFromString("<specobjects doctype=\"impl\">\n" //
                + "  <specobject>\n" //
                + "    <id>impl:strip_duplicate_prefix</id>\n" //
                + "    <version>0</version>\n" //
                + "  </specobject>\n" //
                + "</specobjects>");
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
        final ImportEventListener listenerMock = importFromString(
                "<specobjects doctype=\"utest\">\n" //
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
        final ImportEventListener listenerMock = importFromString(
                "<specobjects doctype=\"itest\">\n" //
                        + "  <specobject>\n" //
                        + "    <id>with-tags</id>\n" //
                        + "    <version>1</version>\n" //
                        + "    <tags>\n" //
                        + "      <tag>tag 1</tag>\n" //
                        + "      <tag>tag 2</tag>\n" //
                        + "    </tags>\n" //
                        + "  </specobject>\n" //
                        + "</specobjects>");
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
        final ImportEventListener listenerMock = importFromString("<specobjects doctype=\"req\">\n" //
                + "  <specobject>\n" //
                + "    <id>with-needs-coverage</id>\n" //
                + "    <version>1</version>\n" //
                + "    <needscoverage>\n" //
                + "      <needsobj>impl</needsobj>\n" //
                + "      <needsobj>utest</needsobj>\n" //
                + "    </needscoverage>\n" //
                + "  </specobject>\n" //
                + "</specobjects>");
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
        final ImportEventListener listenerMock = importFromString("<specobjects doctype=\"req\">\n" //
                + "  <specobject>\n" //
                + "    <id>with-dependencies</id>\n" //
                + "    <version>1</version>\n" //
                + "    <dependencies>\n" //
                + "      <dependson>req:dep-a, v1</dependson>\n" //
                + "      <dependson>req:dep-b, v2</dependson>\n" //
                + "    </dependencies>\n" //
                + "  </specobject>\n" //
                + "</specobjects>");
        verify(listenerMock).beginSpecificationItem();
        verify(listenerMock).setLocation(STANDARD_LOCATION);
        verify(listenerMock).setId(SpecificationItemId.parseId("req~with-dependencies~1"));
        verify(listenerMock).addDependsOnId(SpecificationItemId.parseId("req~dep-a~1"));
        verify(listenerMock).addDependsOnId(SpecificationItemId.parseId("req~dep-b~2"));
        verify(listenerMock).endSpecificationItem();
        verifyNoMoreInteractions(listenerMock);
    }

    @Test
    void testImportFulfilledByIsIgnored()
    {
        final ImportEventListener listenerMock = importFromString("<specobjects doctype=\"req\">\n" //
                + "  <specobject>\n" //
                + "    <id>with-fulfilled-by</id>\n" //
                + "    <version>1</version>\n" //
                + "    <fulfilledby>\n" //
                + "      <ffbObj>\n" //
                + "        <ffbType>impl</ffbType>\n" //
                + "        <ffbId>ffb-a</ffbId>\n" //
                + "        <ffbVersion>1</ffbVersion>\n" //
                + "      </ffbObj>\n" //
                + "      <ffbObj>\n" //
                + "        <ffbType>utest</ffbType>\n" //
                + "        <ffbId>ffb-b</ffbId>\n" //
                + "        <ffbVersion>2</ffbVersion>\n" //
                + "      </ffbObj>\n" //
                + "    </fulfilledby>\n" //
                + "  </specobject>\n" //
                + "</specobjects>");
        verify(listenerMock).beginSpecificationItem();
        verify(listenerMock).setLocation(STANDARD_LOCATION);
        verify(listenerMock).setId(SpecificationItemId.parseId("req~with-fulfilled-by~1"));
        verify(listenerMock).endSpecificationItem();
        verifyNoMoreInteractions(listenerMock);
    }

    @Test
    void testImportProvidesCoverage()
    {
        final ImportEventListener listenerMock = importFromString("<specobjects doctype=\"req\">\n" //
                + "  <specobject>\n" //
                + "    <id>with-fulfilled-by</id>\n" //
                + "    <version>1</version>\n" //
                + "    <providescoverage>\n" //
                + "      <provcov>\n" //
                + "        <linksto>feat:provides-a</linksto>\n" //
                + "        <dstversion>1</dstversion>\n" //
                + "      </provcov>\n" //
                + "      <provcov>\n" //
                + "        <linksto>feat:provides-b</linksto>\n" //
                + "        <dstversion>2</dstversion>\n" //
                + "      </provcov>\n" //
                + "    </providescoverage>\n" //
                + "  </specobject>\n" //
                + "</specobjects>");
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
        try( final RecordingLogHandler logHandler = new RecordingLogHandler() )
        {
            importFromString(
                    "<specdocument>" //
                            + "  <specobjects doctype=\"req\">\n" //
                            + "    <specobject>\n" //
                            + "      <id>minimal</id>\n" //
                            + "      <version>1</version>\n" //
                            + "    </specobject>\n" //
                            + "  </specobjects>\n" //
                            + "  <extension>\n" //
                            + "  </extension>\n" //
                            + "</specdocument>");
            final Optional<LogRecord> firstRecord = logHandler.getLogRecords()
                    .filter((logRecord) -> logRecord.getLevel() == Level.WARNING)
                    .findFirst();
            assertTrue(firstRecord.isPresent());
            assertThat(firstRecord.get().getMessage(), containsString("Found unknown"));
        }
    }

    @Test
    void testCustomTagsWithNamespacesLogsNoWarning()
    {
        try( final RecordingLogHandler logHandler = new RecordingLogHandler() )
        {
            importFromString(
                    "<specdocument xmlns:x=\"http://extension\">" //
                            + "  <specobjects doctype=\"req\">\n" //
                            + "    <specobject>\n" //
                            + "      <id>minimal</id>\n" //
                            + "      <version>1</version>\n" //
                            + "      <x:extension>\n" //
                            + "      </x:extension>\n" //
                            + "    </specobject>\n" //
                            + "  </specobjects>\n" //
                            + "  <x:extension>\n" //
                            + "  </x:extension>\n" //
                            + "</specdocument>");
            assertThat(logHandler.getLogRecords().count(), equalTo(0L));
        }
    }

    @Test
    void testCustomTagsWithNamespacesPlusOftNamespaceLogsNoWarning()
    {
        try( final RecordingLogHandler logHandler = new RecordingLogHandler() )
        {
            importFromString(
                    "<specdocument xmlns=\"https://github.com/itsallcode/openfasttrace\"\n" //
                            + "         xmlns:x=\"http://extension\">" //
                            + "  <specobjects doctype=\"req\">\n" //
                            + "    <specobject>\n" //
                            + "      <id>minimal</id>\n" //
                            + "      <version>1</version>\n" //
                            + "    </specobject>\n" //
                            + "  </specobjects>\n" //
                            + "  <x:extension>\n" //
                            + "  </x:extension>\n" //
                            + "</specdocument>");
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

        @Override public void publish(final LogRecord record)
        {
            logRecords.add(record);
            super.publish(record);
        }

        public Stream<LogRecord> getLogRecords()
        {
            return logRecords.stream();
        }

        @Override public void close()
        {
            Arrays.stream(rootLogger.getHandlers())
                    .filter((handler -> handler instanceof RecordingLogHandler))
                    .forEach(handler -> rootLogger.removeHandler(handler) );
            super.close();
        }
    }
}
