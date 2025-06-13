package org.itsallcode.openfasttrace.testutil;

import java.util.Locale;

/**
 * Helper class to check the operating system this Java VM runs in.
 * <p>
 * please keep the notes below as a pseudo-license:
 * </p>
 * <p>
 * http://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
 * </p>
 * <p>
 * compare to:
 * </p>
 * <ul>
 * <li>http://svn.terracotta.org/svn/tc/dso/tags/2.6.4/code/base/common/src/com/tc/util/runtime/Os.java</li>
 * <li>http://www.docjar.com/html/api/org/apache/commons/lang/SystemUtils.java.html</li>
 * </ul>
 */
public class OsCheck
{
    /**
     * Create a new instance of the operating system check.
     */
    public OsCheck()
    {
        // Default constructor to fix compiler warning "missing-explicit-ctor"
    }

    /**
     * Types of Operating Systems
     */
    public enum OSType
    {
        /** Windows OS */
        WINDOWS,
        /** macOS */
        MACOS,
        /** Linux */
        LINUX,
        /** Any other operating system */
        OTHER
    }

    /**
     * Detect the operating system from the {@code os.name} System property and
     * cache the result.
     * 
     * @return the operating system detected
     */
    public OSType getOperatingSystemType()
    {
        return detectOperatingSystemType();
    }

    private static OSType detectOperatingSystemType()
    {
        final String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if (os.contains("mac") || os.contains("darwin"))
        {
            return OSType.MACOS;
        }
        else if (os.contains("win"))
        {
            return OSType.WINDOWS;
        }
        else if (os.contains("linux"))
        {
            return OSType.LINUX;
        }
        return OSType.OTHER;
    }
}
