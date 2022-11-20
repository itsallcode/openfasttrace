package org.itsallcode.openfasttrace.report.plaintext;

/**
 * ANSI console font effect sequences
 */
// [impl->dsn~reporting.plain-text.ansi-color~1]
// [impl-> dsn~reporting.plain-text.ansi-font-style~1]
enum AnsiSequence {
    /** Reset all font effects */
    RESET(0),
    /** Bold font */
    BOLD(1),
    /** Italic font */
    ITALIC(3),
    /** Underlined */
    UNDERLINE(4),
    /** Inverted foreground and background color */
    INVERSE(7),
    /** Black */
    BLACK(30),
    /** Red */
    RED(31),
    /** Green */
    GREEN(32),
    /** Yellow */
    YELLOW(33),
    /** Blue */
    BLUE(34),
    /** Magenta */
    MAGENTA(35),
    /** Cyan */
    CYAN(36),
    /** White */
    WHITE(37),
    /** Bright Red */
    BRIGHT_RED(91);

    public static final String PREFIX = "\u001B[";
    public static final String SUFFIX = "m";
    private final int id;

    private AnsiSequence(final int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return PREFIX + id + SUFFIX;
    }

    /**
     * Get the ID of the ANSI sequence.
     * @return ANSI sequence ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Combine the given ANSI sequences.
     *
     * @param ids IDs of the font effects that should be combined
     * @return sequence that represents the combined font effect.
     */
    public static String combine(AnsiSequence ... ids) {
        StringBuilder builder = new StringBuilder(PREFIX);
        for(int i = 0; i < ids.length ; ++i) {
            if (i > 0) {
                builder.append(";");
            }
            builder.append(ids[i].id);
        }
        builder.append(SUFFIX);
        return builder.toString();
    }
}
