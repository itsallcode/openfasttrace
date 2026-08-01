package org.itsallcode.openfasttrace.report.plaintext;

import org.itsallcode.openfasttrace.api.ColorScheme;

/**
 * Factory for text formatters
 */
final class TextFormatterFactory {
    private TextFormatterFactory() {
        // prevent instantiation
    }

    /**
     * Create a text formatter
     * @param colorScheme color scheme that the formatter should apply
     * @return text formatter
     */
    public static TextFormatter createFormatter(ColorScheme colorScheme) {
        return (colorScheme == null)
                ? new NullTextFormatter()
                : (switch (colorScheme) {
                    case BLACK_AND_WHITE -> new NullTextFormatter();
                    case MONOCHROME -> new MonochromeTextFormatter();
                    case COLOR -> new ConsoleColorFormatter();
                });
    }
}
