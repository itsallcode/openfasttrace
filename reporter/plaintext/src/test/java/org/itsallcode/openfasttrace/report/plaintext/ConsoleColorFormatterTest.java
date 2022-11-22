package org.itsallcode.openfasttrace.report.plaintext;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ConsoleColorFormatterTest {
    private static final TextFormatter FORMATTER = new ConsoleColorFormatter();

    // [utest->dsn~reporting.plain-text.ansi-color~1]
    @Test
    void testFormatOk() {
        assertThat(FORMATTER.formatOk("ok"), equalTo("\u001B[32mok\u001B[0m"));
    }

    // [utest->dsn~reporting.plain-text.ansi-color~1]
    @Test
    void testFormatNotOk() {
        assertThat(FORMATTER.formatNotOk("not ok"), equalTo("\u001B[91mnot ok\u001B[0m"));
    }

    // [utest->dsn~reporting.plain-text.ansi-color~1]
    // [utest-> dsn~reporting.plain-text.ansi-font-style~1]
    @Test
    void testFormatStrong() {
        assertThat(FORMATTER.formatStrong("strong"), equalTo("\u001B[1;36mstrong\u001B[0m"));
    }
}