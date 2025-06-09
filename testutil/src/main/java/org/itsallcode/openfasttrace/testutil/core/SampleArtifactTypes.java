package org.itsallcode.openfasttrace.testutil.core;

/**
 * Constants for sample artifact types used in tests.
 */
public class SampleArtifactTypes
{
    private SampleArtifactTypes()
    {
        // Not instantiable
    }

    /** Architecture artifact type. */
    public static final String ARCH = "arch";
    /** Design artifact type. */
    public static final String DSN = "dsn";
    /** Implementation artifact type. */
    public static final String IMPL = "impl";
    /** Integration test artifact type. */
    public static final String ITEST = "itest";
    /** Operator manual artifact type. */
    public static final String OMAN = "oman";
    /** Requirement artifact type. */
    public static final String REQ = "req";
    /** Unit test artifact type. */
    public static final String UTEST = "utest";
    /** User manual artifact type. */
    public static final String UMAN = "uman";
}
