package org.itsallcode.openfasttrace.testutil;

import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.itsallcode.openfasttrace.testutil.OsCheck.OSType;

public class OsDetector
{
    private static OsCheck OS_CHECK = new OsCheck();

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
        return OS_CHECK.getOperatingSystemType() == OSType.WINDOWS;
    }

    private static boolean runningOnLinux()
    {
        return OS_CHECK.getOperatingSystemType() == OSType.LINUX;
    }

    private static boolean runningOnMac()
    {
        return OS_CHECK.getOperatingSystemType() == OSType.MACOS;
    }
}
