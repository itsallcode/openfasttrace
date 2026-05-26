package org.itsallcode.openfasttrace.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class VersionProviderIT {
    
    // [itest->dsn~cli.version~1]
    @Test
    void shouldLoadVersionFromProperties() {
        final String version = new VersionProvider().getVersion();
        assertAll(() -> assertThat(version, is(not("unknown"))),
                () -> assertThat(version, is(not("${version}"))));
    }
}
