package org.itsallcode.openfasttrace.testutil;

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

import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

public class OsDetector
{
    private static String OS = System.getProperty("os.name").toLowerCase();

    private OsDetector()
    {
        // not instantiable
    }

    public static void assumeRunningOnWindows()
    {
        assumeTrue("not running on windows", runningOnWindows());
    }

    public static void assumeRunningOnUnix()
    {
        assumeFalse("not running on unix", runningOnWindows());
    }

    private static boolean runningOnWindows()
    {
        return OS.indexOf("win") >= 0;
    }
}
