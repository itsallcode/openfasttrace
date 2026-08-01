package org.itsallcode.openfasttrace.report.plaintext;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class MonochromeTextFormatterTest {
    private static final TextFormatter FORMATTER = new MonochromeTextFormatter();

    @Test
    void testFormatOk() {
        assertThat(FORMATTER.formatOk("ok"), equalTo("ok"));
    }

    @Test
    void testFormatNotOk() {
        assertThat(FORMATTER.formatNotOk("not ok"), equalTo("\u001B[7mnot ok\u001B[0m"));
    }

    @Test
    // [utest->dsn~reporting.plain-text.transitive-defect~1]
    void testFormatTransitiveNotOk() {
        assertThat(FORMATTER.formatTransitiveNotOk("not ok"), equalTo("\u001B[3mnot ok\u001B[0m"));
    }

    @Test
    void testFormatStrong() {
        assertThat(FORMATTER.formatStrong("strong"), equalTo("\u001B[1mstrong\u001B[0m"));
    }
}
