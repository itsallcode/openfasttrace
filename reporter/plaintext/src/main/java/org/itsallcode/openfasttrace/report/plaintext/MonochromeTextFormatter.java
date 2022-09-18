package org.itsallcode.openfasttrace.report.plaintext;

import static org.itsallcode.openfasttrace.report.plaintext.AnsiSequence.*;

/**
 * Formatter that only uses font weight and style
 * <p>
 * Useful for people who cannot distinguish colors or terminals that don't display colors or if colors are hard to
 * discern with a console color scheme.
 * </p>
 */
// [impl->dsn~reporting.plain-text.ansi-font-style~1]
public class MonochromeTextFormatter implements TextFormatter {
    @Override
    public String formatOk(final String text) {
        return text;
    }

    @Override
    public String formatNotOk(final String text) {
        return INVERSE + text + RESET;
    }

    @Override
    public String formatStrong(final String text) {
        return BOLD + text + RESET;
    }
}
