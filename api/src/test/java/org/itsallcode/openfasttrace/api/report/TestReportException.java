package org.itsallcode.openfasttrace.api.report;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

class TestReportException
{
    @Test
    void testConstructorWithCause()
    {
        assertThat(new ReportException("message", new RuntimeException()), notNullValue());
    }

    @Test
    void testConstructorWithoutCause()
    {
        assertThat(new ReportException("message"), notNullValue());
    }
}
