package org.itsallcode.openfasttrace.report.plaintext;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ConsoleColorFormatterTest {
    private static final TextFormatter FORMATTER = new ConsoleColorFormatter();

    @Test
    void testFormatOk() {
        assertThat(FORMATTER.formatOk("ok"), equalTo("\u001B[32mok\u001B[0m"));
    }

    @Test
    void testFormatNotOk() {
        assertThat(FORMATTER.formatNotOk("not ok"), equalTo("\u001B[31mnot ok\u001B[0m"));
    }

    @Test
    void testFormatStrong() {
        assertThat(FORMATTER.formatStrong("strong"), equalTo("\u001B[1m\u001B[36mstrong\u001B[0m"));
    }
}