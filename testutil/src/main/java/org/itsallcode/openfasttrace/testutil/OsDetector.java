package org.itsallcode.openfasttrace.testutil;

import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class OsDetector
{
    private static final String OS = System.getProperty("os.name").toLowerCase();

    private OsDetector()
    {
        // not instantiable
    }

    public static void assumeRunningOnWindows()
    {
        assumeTrue(OsDetector::runningOnWindows, "not running on windows");
    }

    public static void assumeRunningOnUnix()
    {
        assumeFalse(OsDetector::runningOnWindows, "not running on unix");
    }

    private static boolean runningOnWindows()
    {
        return OS.contains("win");
    }
}
