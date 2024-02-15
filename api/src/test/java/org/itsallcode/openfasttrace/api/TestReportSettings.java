package org.itsallcode.openfasttrace.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.itsallcode.openfasttrace.api.ReportSettings.Builder;
import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.api.report.ReportVerbosity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestReportSettings
{
    private Builder builder;

    @BeforeEach
    void beforeEach()
    {
        this.builder = ReportSettings.builder();
    }

    @Test
    void testDefaultVerbosity()
    {
        assertThat(this.builder.build().getReportVerbosity(),
                equalTo(ReportVerbosity.FAILURE_DETAILS));
    }

    @Test
    void testOriginNotShownByDefault()
    {
        assertThat(this.builder.build().showOrigin(), equalTo(false));
    }

    @Test
    void testDefaultOutputFormat()
    {
        assertThat(this.builder.build().getOutputFormat(), equalTo("plain"));
    }

    @Test
    void testDefaultNewline()
    {
        assertThat(this.builder.build().getNewline(), equalTo(Newline.UNIX));
    }

    @Test
    void testBuildWithVerbosity()
    {
        assertThat(this.builder.verbosity(ReportVerbosity.ALL).build().getReportVerbosity(),
                equalTo(ReportVerbosity.ALL));
    }

    @Test
    void testBuildWithOriginShown()
    {
        assertThat(this.builder.showOrigin(true).build().showOrigin(), equalTo(true));
    }

    @Test
    void testBuildWithOutputFormat()
    {
        assertThat(this.builder.outputFormat("html").build().getOutputFormat(), equalTo("html"));
    }

    @Test
    void testBuildWithNewline()
    {
        assertThat(this.builder.newline(Newline.OLDMAC).build().getNewline(),
                equalTo(Newline.OLDMAC));
    }

    @Test
    void testBuildColorSchemeDefault()
    {
        assertThat(this.builder.build().getColorScheme(),
                equalTo(ColorScheme.BLACK_AND_WHITE));
    }

    @Test
    void testSetColorSchemeNullNotAllowed()
    {
        assertThrows(NullPointerException.class, () -> this.builder.colorScheme(null));
    }

    @Test
    void testBuildColorSchemeCustom()
    {
        assertThat(this.builder.colorScheme(ColorScheme.COLOR).build().getColorScheme(),
                equalTo(ColorScheme.COLOR));
    }

    @Test
    void testBuildDetailsFoldingStatusDefault()
    {
        assertThat(this.builder.build().getDetailsSectionFolding(),
                equalTo(DetailsSectionFolding.HIDE_DETAILS));
    }

    @Test
    void testBuildDetailsFoldingStatusNullNotAllowed()
    {
        assertThrows(NullPointerException.class, () -> this.builder.detailsSectionFolding(null));
    }

    @Test
    void testBuildDetailsFoldingStatusCustom()
    {
        assertThat(
                this.builder.detailsSectionFolding(DetailsSectionFolding.SHOW_DETAILS).build()
                        .getDetailsSectionFolding(),
                equalTo(DetailsSectionFolding.SHOW_DETAILS));
    }
}
