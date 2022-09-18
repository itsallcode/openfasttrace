package org.itsallcode.openfasttrace.report.plaintext;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class TestNullTextFormatter {
    private static final TextFormatter FORMATTER = new NullTextFormatter();

    @Test
    void testFormatOk() {
        assertThat(FORMATTER.formatOk("ok"), equalTo("ok"));
    }

    @Test
    void testFormatNotOk() {
        assertThat(FORMATTER.formatNotOk("not ok"), equalTo("not ok"));
    }

    @Test
    void testFormatStrong() {
        assertThat(FORMATTER.formatStrong("strong"), equalTo("strong"));
    }
}