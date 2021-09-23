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
 * This is thrown when expected command line arguments are missing.
 */
public class MissingArgumentException extends CliException
{
    private static final long serialVersionUID = 1L;

    /**
     * Create a new exception.
     * 
     * @param argumentName
     *            the name of the missing command line argument.
     */
    public MissingArgumentException(final String argumentName)
    {
        super("Argument '" + argumentName + "' is missing");
    }
}
