package openfasttrack.importer.markdown;

import java.util.regex.Pattern;

/**
 * Patterns that describe tokens to be recognized within Markdown-style
 * specifications.
 */
public enum MdPattern
{
    // [impl~md.specification_item_id_format~1]
    // [impl~md.specification_item_title~1]

    // @formatter:off
    COMMENT("Comment:\\s*"),
    COVERS("Covers:\\s*"),
    COVERS_REF("\\s{0,3}\\*\\s*.*\\(#("+ PatternConstants.REFERENCE + ")"),
    DEPENDS("Depends:\\s*"),
    DEPENDS_REF("\\s{0,3}\\+\\s*.*\\(#("+ PatternConstants.REFERENCE + ")"),
    EMPTY("\\s*"),
    EVERYTHING(".*"),
    ID("`?(" + PatternConstants.REFERENCE + ")`?.*"),
    NEEDS("Needs:\\s*(\\w+(?:,\\s*\\w+)+)"),
    RATIONALE("Rationale:\\s*"),
    TITLE("#+\\s*(.*)");
    // @formatter:on

    private final Pattern pattern;

    private MdPattern(final String regularExpression)
    {
        this.pattern = Pattern.compile(regularExpression);
    }

    /**
     * Get the regular expression pattern object
     *
     * @return the pattern
     */
    public Pattern getPattern()
    {
        return this.pattern;
    }

    private static class PatternConstants
    {
        public static final String REFERENCE = "\\p{Alpha}+~\\p{Alpha}\\w*(?:\\.\\p{Alpha}\\w*)*~\\d+";
    }
}