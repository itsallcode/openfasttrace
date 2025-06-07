package org.itsallcode.openfasttrace.testutil;

import java.lang.Runtime.Version;

import org.junit.jupiter.api.Assumptions;
import org.opentest4j.TestAbortedException;

/**
 * Assumptions for unit and integration tests.
 */
public class TestAssumptions
{
    private TestAssumptions()
    {
        // Not instantiable
    }

    /**
     * This ensures that the current JDK supports using Java's security manager.
     * Starting with Java 19, the security manager is not supported anymore.
     * 
     * @throws TestAbortedException
     *             if the JVM does not support Java's security manager.
     */
    public static void assumeSecurityManagerSupported() throws TestAbortedException
    {
        final Version version = Runtime.version();
        Assumptions.assumeTrue(version.feature() <= 18);
    }
}
