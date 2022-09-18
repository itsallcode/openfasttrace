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

    public static void assumeRunningOnLinux()
    {
        assumeTrue(OsDetector::runningOnLinux, "not running on linux");
    }

    public static void assumeRunningOnMacOs()
    {
        assumeTrue(OsDetector::runningOnMac, "not running on macOS");
    }

    private static boolean runningOnWindows()
    {
        return OS.contains("win");
    }

    private static boolean runningOnLinux()
    {
        return OS.contains("linux");
    }

    private static boolean runningOnMac()
    {
        return OS.contains("mac");
    }
}
