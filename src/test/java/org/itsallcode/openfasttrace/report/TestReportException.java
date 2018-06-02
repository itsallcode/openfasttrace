package org.itsallcode.openfasttrace.report;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
