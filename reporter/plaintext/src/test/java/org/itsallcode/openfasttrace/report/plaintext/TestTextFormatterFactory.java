package org.itsallcode.openfasttrace.report.plaintext;

import org.itsallcode.openfasttrace.api.ColorScheme;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.itsallcode.openfasttrace.api.ColorScheme.*;
import static org.itsallcode.openfasttrace.report.plaintext.TextFormatterFactory.createFormatter;

class TestTextFormatterFactory {
    @Test
    void testCreateNullTextFormatter() {
        assertThat(createFormatter(BLACK_AND_WHITE), instanceOf(NullTextFormatter.class));
    }

    @Test
    void testCreateMonochromeTextFormatter() {
        assertThat(createFormatter(MONOCHROME), instanceOf(MonochromeTextFormatter.class));
    }

    @Test
    void testCreateConsoleColorFormatter() {
        assertThat(createFormatter(COLOR), instanceOf(ConsoleColorFormatter.class));
    }
}