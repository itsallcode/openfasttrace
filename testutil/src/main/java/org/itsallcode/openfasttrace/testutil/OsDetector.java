package org.itsallcode.openfasttrace.testutil;

import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.itsallcode.openfasttrace.testutil.OsCheck.OSType;

/**
 * Helper class to detect the operating system. Contains assumption methods for
 * JUnit.
 */
public class OsDetector
{
    private static final OsCheck OS_CHECK = new OsCheck();

    private OsDetector()
    {
        // not instantiable
    }

    /**
     * Assumes that the current operating system is Windows. If the application
     * is not running on Windows, the assumption will fail, and the test will be
     * aborted.
     */
    public static void assumeRunningOnWindows()
    {
        assumeTrue(OsDetector::runningOnWindows, "not running on windows");
    }

    /**
     * Assumes that the current operating system is Unix-like OS. If the
     * application is not running on a Unix-like OS, the assumption will fail,
     * and the test will be aborted.
     */
    public static void assumeRunningOnUnix()
    {
        assumeFalse(OsDetector::runningOnWindows, "not running on unix");
    }

    /**
     * Assumes that the current operating system is Linux. If the application
     * is not running on Linux, the assumption will fail, and the test will be
     * aborted.
     */
    public static void assumeRunningOnLinux()
    {
        assumeTrue(OsDetector::runningOnLinux, "not running on linux");
    }

    /**
     * Assumes that the current operating system is macOS. If the application
     * is not running on macOS, the assumption will fail, and the test will be
     * aborted.
     */
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
