package org.itsallcode.openfasttrace.report.plaintext;

/**
 * Pseudo text formatter that returns everything exactly as it was given.
 * <p>
 * This is useful for reports that are piped into other tools or written to files.
 * </p>
 */
class NullTextFormatter implements TextFormatter {
    /**
     * Create a new instance of a {@link NullTextFormatter}.
     */
    NullTextFormatter() {
        // Added for JavaDoc.
    }

    @Override
    public String formatOk(final String text) {
        return text;
    }

    @Override
    public String formatNotOk(final String text) {
        return text;
    }

    @Override
    public String formatStrong(final String text) {
        return text;
    }
}