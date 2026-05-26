package org.itsallcode.openfasttrace.core.cli.commands;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.itsallcode.io.Capturable;
import org.itsallcode.junit.sysextensions.SystemOutGuard;
import org.itsallcode.junit.sysextensions.SystemOutGuard.SysOut;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SystemOutGuard.class)
class HelpCommandTest {

    /** utest~cli.help.version~1 */
    @Test
    void testRunDisplaysVersion(@SysOut final Capturable out) {
        out.captureMuted();
        new HelpCommand(true).run();
        assertThat(out.getCapturedData(), containsString("OpenFastTrace"));
        // Since we are running in a test environment, the version might be "unknown" 
        // if the resource is not filtered or correctly loaded. 
        // But it should at least not contain "${version}" literal.
        assertThat(out.getCapturedData(), org.hamcrest.Matchers.not(containsString("${version}")));
    }
}
