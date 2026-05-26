package org.itsallcode.openfasttrace.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link VersionProvider}.
 * <p>
 * Testing the bad weather cases requires using a custom classloader. But that
 * makes the test coverage invisible. So we only test the happy path. The tests
 * for the bad weather cases are too involved for testing a problem that is
 * unlikely to occur with a static resource.
 * </p>
 */
class VersionProviderIT
{
    // [itest->dsn~cli.version~1]
    @Test
    void testLoadVersionFromProperties()
    {
        final String version = new VersionProvider().getVersion();
        assertAll(() -> assertThat(version, is(not("unknown"))),
                () -> assertThat(version, is(not("${version}"))));
    }
}
