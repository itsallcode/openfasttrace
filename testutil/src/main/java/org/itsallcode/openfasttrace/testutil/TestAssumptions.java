package org.itsallcode.openfasttrace.testutil;

import java.lang.Runtime.Version;

import org.junit.jupiter.api.Assumptions;
import org.opentest4j.TestAbortedException;

/**
 * Assumptions for unit and integration tests, see {@link TestAbortedException}.
 */
public class TestAssumptions
{
    private TestAssumptions()
    {
        // Not instantiable
    }

    /**
     * This ensures that the current JDK supports using the
     * {@link SecurityManager}. Starting with Java 19 the security manager is
     * not supported any more.
     * 
     * @throws TestAbortedException
     *             if the JVM does not support {@link SecurityManager}.
     */
    public static void assumeSecurityManagerSupported() throws TestAbortedException
    {
        final Version version = Runtime.version();
        Assumptions.assumeTrue(version.feature() <= 18);
    }
}
