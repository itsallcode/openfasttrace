package org.itsallcode.openfasttrace.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.core.exporter.ExporterConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestExportSettings
{
    private ExportSettings.Builder builder;

    @BeforeEach
    void beforeEach()
    {
        this.builder = ExportSettings.builder();
    }

    @Test
    void testDefaultOutputFormat()
    {
        assertThat(this.builder.build().getOutputFormat(),
                equalTo(ExporterConstants.DEFAULT_OUTPUT_FORMAT));
    }

    @Test
    void testDefaultNewline()
    {
        assertThat(this.builder.build().getNewline(), equalTo(Newline.UNIX));
    }

    @Test
    void testBuildWithOutputFormat()
    {
        assertThat(this.builder.outputFormat("foo").build().getOutputFormat(), equalTo("foo"));
    }

    @Test
    void testBuildWithNewline()
    {
        assertThat(this.builder.newline(Newline.OLDMAC).build().getNewline(),
                equalTo(Newline.OLDMAC));
    }
}
