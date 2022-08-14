package org.itsallcode.openfasttrace.core.report;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;

import org.itsallcode.openfasttrace.api.report.ReportException;
import org.junit.jupiter.api.Test;

class TestReportException
{
    @Test
    void testNewReportExceptionWithMessage()
    {
        final String message = "foobar";
        final ReportException exception = new ReportException(message);
        assertThat(exception.getMessage(), equalTo(message));
    }

    @Test
    void testNewReportExceptionWithMessageAndCause()
    {
        final String message = "foobar";
        final Exception cause = new Exception("barzoo");
        final ReportException exception = new ReportException(message, cause);
        assertThat(exception.getMessage(), equalTo(message));
        assertThat(exception.getCause(), equalTo(cause));
    }
}
