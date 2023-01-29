package org.itsallcode.openfasttrace.report.plaintext;

import static org.itsallcode.openfasttrace.report.plaintext.AnsiSequence.*;

/**
 * Formatter that uses ANSI code sequences to color the output.
 */
// [impl->dsn~reporting.plain-text.ansi-color~1]
final class ConsoleColorFormatter implements TextFormatter {
    /**
     * Create a new instance of a {@link ConsoleColorFormatter}.
     */
    ConsoleColorFormatter() {
        // Added for JavaDoc.
    }

    @Override
    public String formatOk(final String text) {
        return GREEN + text + RESET;
    }

    @Override
    public String formatNotOk(final String text) {
        return BRIGHT_RED + text + RESET;
    }

    @Override
    public String formatStrong(final String text) {
        return AnsiSequence.combine(BOLD, CYAN) + text + RESET;
    }
}