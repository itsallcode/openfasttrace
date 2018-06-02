package org.itsallcode.openfasttrace.report;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestReportException
{
    @Test
    public void testNewReportExceptionWithMessage()
    {
        final String message = "foobar";
        final ReportException exception = new ReportException(message);
        assertThat(exception.getMessage(), equalTo(message));
    }

    @Test
    public void testNewReportExceptionWithMessageAndCause()
    {
        final String message = "foobar";
        final Exception cause = new Exception("barzoo");
        final ReportException exception = new ReportException(message, cause);
        assertThat(exception.getMessage(), equalTo(message));
        assertThat(exception.getCause(), equalTo(cause));
    }
}