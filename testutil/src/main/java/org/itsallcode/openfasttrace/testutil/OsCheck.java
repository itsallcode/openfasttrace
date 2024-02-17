package org.itsallcode.openfasttrace.testutil;

import java.util.Locale;

/**
 * Helper class to check the operating system this Java VM runs in.
 *
 * please keep the notes below as a pseudo-license:
 *
 * http://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
 * compare to
 * http://svn.terracotta.org/svn/tc/dso/tags/2.6.4/code/base/common/src/com/tc/util/runtime/Os.java
 * http://www.docjar.com/html/api/org/apache/commons/lang/SystemUtils.java.html
 */
public class OsCheck
{
    public OsCheck()
    {
        // Default constructor to fix compiler warning "missing-explicit-ctor"
    }

    /**
     * Types of Operating Systems
     */
    public enum OSType
    {
        WINDOWS, MACOS, LINUX, OTHER
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
        if ((os.indexOf("mac") >= 0) || (os.indexOf("darwin") >= 0))
        {
            return OSType.MACOS;
        }
        else if (os.indexOf("win") >= 0)
        {
            return OSType.WINDOWS;
        }
        else if (os.indexOf("linux") >= 0)
        {
            return OSType.LINUX;
        }
        return OSType.OTHER;
    }
}
