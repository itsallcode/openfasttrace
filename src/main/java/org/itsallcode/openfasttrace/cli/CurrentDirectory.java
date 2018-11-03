package org.itsallcode.openfasttrace.cli;

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

/**
 * This class allows users to query the current directory. For test purposes
 * this directory can be overridden.
 */
public class CurrentDirectory
{
    private final static CurrentDirectory instance = new CurrentDirectory();
    private final static String initialDirectory = System.getProperty("user.dir");
    private String overrideDirectory = null;

    /**
     * Get the current directory or the override directory if set
     * 
     * @return current directory
     */
    public static synchronized String get()
    {
        return (instance.overrideDirectory == null) ? initialDirectory : instance.overrideDirectory;
    }

    /**
     * Override the current directory. This is can be used for unit and
     * integration tests.
     * 
     * @param overrideDirectory
     *            directory to be returned by {@link #get()} instead of the
     *            actual current directory.
     */
    public static void override(final String overrideDirectory)
    {
        instance.overrideDirectory = overrideDirectory;
    }

    /**
     * Resets the class so that the actual current directory is returned by
     * {@link #get()}
     */
    public static void reset()
    {
        instance.overrideDirectory = null;
    }
}
