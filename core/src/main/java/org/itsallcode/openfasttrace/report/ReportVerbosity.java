package org.itsallcode.openfasttrace.report;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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

/**
 * The {@link ReportVerbosity} is used to control the detail level of coverage
 * reports.
 */
public enum ReportVerbosity
{
    //@formatter:off
    /** No output, only set exit state */           QUIET,
    /** Output "OK", "FAIL" only */                 MINIMAL,
    /** Summary only */                             SUMMARY,
    /** List of defect specification items */       FAILURES,
    /** Summaries of defect specification items */  FAILURE_SUMMARIES,
    /** Details of unclean items */                 FAILURE_DETAILS,
    /** Details for all items */                    ALL
    //@formatter:on
}
