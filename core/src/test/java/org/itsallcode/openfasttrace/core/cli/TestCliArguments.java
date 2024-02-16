package org.itsallcode.openfasttrace.core.cli;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.nio.file.Paths;
import java.util.List;

import org.itsallcode.openfasttrace.api.ColorScheme;
import org.itsallcode.openfasttrace.api.DetailsSectionDisplay;
import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.api.report.ReportConstants;
import org.itsallcode.openfasttrace.api.report.ReportVerbosity;
import org.itsallcode.openfasttrace.core.cli.commands.ConvertCommand;
import org.itsallcode.openfasttrace.core.cli.commands.TraceCommand;
import org.itsallcode.openfasttrace.core.exporter.ExporterConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestCliArguments
{
    private static final String AFTER_SETTER = "after setter";
    private static final String BEFORE_SETTER = "before setter";
    private CliArguments arguments;

    @BeforeEach
    void testSetUp()
    {
        this.arguments = new CliArguments(new StandardDirectoryService());
    }

    @Test
    void testGetCommandWithUnnamedValuesNull()
    {
        this.arguments.setUnnamedValues(null);
        assertThat(this.arguments.getCommand().isPresent(), is(false));
    }

    @Test
    void testGetCommandWithUnnamedValuesEmpty()
    {
        this.arguments.setUnnamedValues(emptyList());
        assertThat(this.arguments.getCommand().isPresent(), is(false));
    }

    @Test
    void testSetOutputFormat()
    {
        final String value = "foobar";
        this.arguments.setOutputFormat(value);
        assertAfterSetter(value, this.arguments.getOutputFormat());
    }

    // [utest->dsn~cli.conversion.default-output-format~1]
    @Test
    void getStandardOutputFormatForExport()
    {
        this.arguments.setUnnamedValues(List.of(ConvertCommand.COMMAND_NAME));
        assertThat(this.arguments.getOutputFormat(),
                equalTo(ExporterConstants.DEFAULT_OUTPUT_FORMAT));
    }

    // [utest->dsn~cli.tracing.default-format~1]
    @Test
    void getStandardOutputFormatForReport()
    {
        this.arguments.setUnnamedValues(List.of(TraceCommand.COMMAND_NAME));
        assertThat(this.arguments.getOutputFormat(),
                equalTo(ReportConstants.DEFAULT_REPORT_FORMAT));
    }

    @Test
    void testSetO()
    {
        final String value = "foobar";
        this.arguments.setO(value);
        assertAfterSetter(value, this.arguments.getOutputFormat());
    }

    private void assertAfterSetter(final String value, final String outputFormat)
    {
        assertThat(AFTER_SETTER, outputFormat, equalTo(value));
    }

    @Test
    void testSetOutputFile()
    {
        final String value = "/tmp/foobar";
        final String expectedPath = Paths.get(value).toString();
        assertThat(BEFORE_SETTER, this.arguments.getOutputPath(), equalTo(null));
        this.arguments.setOutputFile(value);
        assertThat(AFTER_SETTER, this.arguments.getOutputPath().toString(), equalTo(expectedPath));
    }

    @Test
    void testSetF()
    {
        final String value = "/tmp/foobar";
        final String expectedPath = Paths.get(value).toString();
        assertThat(BEFORE_SETTER, this.arguments.getOutputPath(), equalTo(null));
        this.arguments.setF(value);
        assertThat(AFTER_SETTER, this.arguments.getOutputPath().toString(), equalTo(expectedPath));
    }

    @Test
    void testSetReportVerbositiy()
    {
        final ReportVerbosity value = ReportVerbosity.QUIET;
        assertThat(BEFORE_SETTER, this.arguments.getReportVerbosity(),
                equalTo(ReportConstants.DEFAULT_REPORT_VERBOSITY));
        this.arguments.setReportVerbosity(value);
        assertThat(AFTER_SETTER, this.arguments.getReportVerbosity(), equalTo(value));
    }

    @Test
    void testSetV()
    {
        final ReportVerbosity value = ReportVerbosity.QUIET;
        assertThat(BEFORE_SETTER, this.arguments.getReportVerbosity(),
                equalTo(ReportConstants.DEFAULT_REPORT_VERBOSITY));
        this.arguments.setV(value);
        assertThat(AFTER_SETTER, this.arguments.getReportVerbosity(), equalTo(value));
    }

    @Test
    void testSetNewline()
    {
        final Newline value = Newline.OLDMAC;
        this.arguments.setNewline(value);
        assertThat(AFTER_SETTER, this.arguments.getNewline(), equalTo(value));
    }

    @Test
    void testSetN()
    {
        final Newline value = Newline.OLDMAC;
        this.arguments.setN(value);
        assertThat(AFTER_SETTER, this.arguments.getNewline(), equalTo(value));
    }

    // [utest->dsn~filtering-by-artifact-types-during-import~1]
    @Test
    void testWantedArtifactTypesEmptyByDefault()
    {
        assertThat(BEFORE_SETTER, this.arguments.getWantedArtifactTypes(), emptyIterable());
    }

    // [utest->dsn~filtering-by-artifact-types-during-import~1]
    @Test
    void testSetIgnoreArtifactTypes()
    {
        final String value = "impl,utest";
        this.arguments.setWantedArtifactTypes(value);
        assertThat(AFTER_SETTER, this.arguments.getWantedArtifactTypes(),
                containsInAnyOrder("impl", "utest"));
    }

    // [utest->dsn~filtering-by-artifact-types-during-import~1]
    @Test
    void testSetA()
    {
        final String value = "impl,utest";
        this.arguments.setA(value);
        assertThat(AFTER_SETTER, this.arguments.getWantedArtifactTypes(),
                containsInAnyOrder("impl", "utest"));
    }

    // [utest->dsn~filtering-by-tags-during-import~1]
    @Test
    void testWantedTagsEmptyByDefault()
    {
        assertThat(BEFORE_SETTER, this.arguments.getWantedTags(), emptyIterable());
    }

    // [utest->dsn~filtering-by-tags-during-import~1]
    @Test
    void testSetWantedTags()
    {
        final String value = "client,server";
        this.arguments.setWantedTags(value);
        assertThat(AFTER_SETTER, this.arguments.getWantedTags(),
                containsInAnyOrder("client", "server"));
    }

    // [utest->dsn~filtering-by-tags-during-import~1]
    @Test
    void testSetD()
    {
        final String value = "client,server";
        this.arguments.setT(value);
        assertThat(AFTER_SETTER, this.arguments.getWantedTags(),
                containsInAnyOrder("client", "server"));
    }

    // [utest->dsn~filtering-by-tags-or-no-tags-during-import~1]
    @Test
    void testSetWantedTagsIncludingNone()
    {
        final String value = "_,client,server";
        this.arguments.setWantedTags(value);
        assertThat(AFTER_SETTER, this.arguments.getWantedTags(),
                containsInAnyOrder("_", "client", "server"));
    }

    // [utest->dsn~reporting.plain-text.specification-item-origin~1]]
    // [utest->dsn~reporting.plain-text.linked-specification-item-origin~1]
    // [utest->dsn~reporting.html.specification-item-origin~1]
    // [utest->dsn~reporting.html.linked-specification-item-origin~1]
    @Test
    void testShowOriginDisabledByDefault()
    {
        assertThat(this.arguments.getShowOrigin(), is(false));
    }

    // [utest->dsn~reporting.plain-text.specification-item-origin~1]]
    // [utest->dsn~reporting.plain-text.linked-specification-item-origin~1]
    // [utest->dsn~reporting.html.specification-item-origin~1]
    // [utest->dsn~reporting.html.linked-specification-item-origin~1]
    @Test
    void testSetShowOrigin()
    {
        this.arguments.setShowOrigin(true);
        assertThat(this.arguments.getShowOrigin(), is(true));
    }

    // [utest->dsn~reporting.plain-text.specification-item-origin~1]]
    // [utest->dsn~reporting.plain-text.linked-specification-item-origin~1]
    // [utest->dsn~reporting.html.specification-item-origin~1]
    // [utest->dsn~reporting.html.linked-specification-item-origin~1]
    @Test
    void testSetS()
    {
        this.arguments.setS(true);
        assertThat(this.arguments.getShowOrigin(), is(true));
    }

    // [utest->dsn~reporting.plain-text.ansi-color~1]
    // [utest->dsn~reporting.plain-text.ansi-font-style~1]
    @Test
    void testSetColorScheme()
    {
        this.arguments.setColorScheme(ColorScheme.MONOCHROME);
        assertThat(this.arguments.getColorScheme(), is(ColorScheme.MONOCHROME));
    }

    // [utest->dsn~reporting.plain-text.ansi-color~1]
    // [utest->dsn~reporting.plain-text.ansi-font-style~1]
    @Test
    void testSetC()
    {
        this.arguments.setC(ColorScheme.MONOCHROME);
        assertThat(this.arguments.getColorScheme(), is(ColorScheme.MONOCHROME));
    }


    @Test
    void testColorSchemeDefaultsToColor()
    {
        assertThat(this.arguments.getColorScheme(), is(ColorScheme.COLOR));
    }

    @Test
    void testSetOutputFileOverridesColorSchemeSetting()
    {
        this.arguments.setColorScheme(ColorScheme.MONOCHROME);
        this.arguments.setOutputFile("something");
        assertThat(this.arguments.getColorScheme(), is(ColorScheme.BLACK_AND_WHITE));
    }

    // [utest->dsn~reporting.html.details-display~1]
    @Test
    void testDetailsSectionDisplayDefaultsToHidden()
    {
        assertThat(this.arguments.getDetailsSectionDisplay(), is(DetailsSectionDisplay.COLLAPSE));
    }

    // [utest->dsn~reporting.html.details-display~1]
    @Test
    void testDetailsSectionDisplayNullDefaultsToHidden()
    {
        this.arguments.setDetailsSectionDisplay(null);
        assertThat(this.arguments.getDetailsSectionDisplay(), is(DetailsSectionDisplay.COLLAPSE));
    }

    // [utest->dsn~reporting.html.details-display~1]
    @Test
    void testDetailsSectionDisplayCustomValue()
    {
        this.arguments.setDetailsSectionDisplay(DetailsSectionDisplay.EXPAND);
        assertThat(this.arguments.getDetailsSectionDisplay(), is(DetailsSectionDisplay.EXPAND));
    }
}
