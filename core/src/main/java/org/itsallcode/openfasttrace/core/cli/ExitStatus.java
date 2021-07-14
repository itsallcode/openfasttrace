package org.itsallcode.openfasttrace.core.cli;

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
 * Exit status of the OpenFastTrace command line interface.
 */
public enum ExitStatus
{
    /** Process finished successfully. */
    OK(0),
    /** An error occurred during processing. */
    FAILURE(1),
    /** Got invalid command line arguments. */
    CLI_ERROR(2);

    private final int code;

    ExitStatus(final int code)
    {
        this.code = code;
    }

    /**
     * Get the numeric representation of the exit status code
     * 
     * @return exit status code as integer number
     */
    public int getCode()
    {
        return this.code;
    }
}
