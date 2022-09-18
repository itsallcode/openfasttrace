package org.itsallcode.openfasttrace.report.plaintext;

import static org.itsallcode.openfasttrace.report.plaintext.AnsiSequence.*;

/**
 * Formatter that uses ANSI code sequences to color the output.
 */
// [impl->dsn~reporting.plain-text.ansi-color~1]
final class ConsoleColorFormatter implements TextFormatter {
    @Override
    public String formatOk(final String text) {
        return GREEN + text + RESET;
    }

    @Override
    public String formatNotOk(final String text) {
        return RED + text + RESET;
    }

    @Override
    public String formatStrong(final String text) {
        return BOLD + text + RESET;
    }
}