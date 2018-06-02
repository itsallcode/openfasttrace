package org.itsallcode.openfasttrace.report;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestReportFormat
{
    @Test
    public void testParseValid()
    {
        assertThat(ReportFormat.parse("plain"), equalTo(ReportFormat.PLAIN_TEXT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInalidFormatThrowsIllegalArguemtException()
    {
        ReportFormat.parse("invalid");
    }
}
