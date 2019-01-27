package org.itsallcode.openfasttrace.cli;

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
 * Exception thrown in case of command line validation errors.
 */
public class CliException extends Exception
{
    private static final long serialVersionUID = 3126173961917546825L;

    /**
     * Create a new {@link CliException} caused by another exception
     * 
     * @param message
     *            error message
     * @param cause
     *            causing exception
     */
    public CliException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Create a new {@link CliException}
     * 
     * @param message
     *            error message
     */
    public CliException(final String message)
    {
        super(message);
    }
}