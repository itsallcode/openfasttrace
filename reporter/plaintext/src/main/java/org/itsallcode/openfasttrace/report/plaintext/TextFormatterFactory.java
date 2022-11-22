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
        if(colorScheme == null)
        {
            return new NullTextFormatter();
        }
        switch (colorScheme) {
            case BLACK_AND_WHITE:
                return new NullTextFormatter();
            case MONOCHROME:
                return new MonochromeTextFormatter();
            case COLOR:
                return new ConsoleColorFormatter();
            default:
                throw new IllegalArgumentException("Unable to create text formatter for unknown color scheme '"
                        + colorScheme + "'.");
        }
    }
}
