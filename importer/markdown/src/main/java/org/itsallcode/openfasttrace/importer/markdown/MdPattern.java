package org.itsallcode.openfasttrace.importer.markdown;

import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;

/**
 * Patterns that describe tokens to be recognized within Markdown-style
 * specifications.
 */
enum MdPattern
{
    // [impl->dsn~md.specification-item-title~1]
    // [impl->dsn~md.artifact-forwarding-notation~1]

    // @formatter:off
    COMMENT("Comment:\\s*"),
    COVERS("Covers:\\s*"),
    COVERS_REF(PatternConstants.REFERENCE_AFTER_BULLET),
    DEPENDS("Depends:\\s*"),
    DEPENDS_REF(PatternConstants.REFERENCE_AFTER_BULLET),
    DESCRIPTION("Description:\\s*"),
    EMPTY("(\\s*)"),
    EVERYTHING("(.*)"),
    FORWARD(".*?("
            + PatternConstants.ARTIFACT_TYPE
            + "\\s*"
            + MarkdownForwardingSpecificationItem.FORWARD_MARKER
            + "\\s*"
            + PatternConstants.ARTIFACT_TYPE
            + "(?:,\\s*"
            + PatternConstants.ARTIFACT_TYPE
            + ")*"
            + "\\s*"
            + MarkdownForwardingSpecificationItem.ORIGINAL_MARKER
            + "\\s*"
            + SpecificationItemId.ID_PATTERN
            + ").*?"),
    ID("`?((?:" + SpecificationItemId.ID_PATTERN + ")|(?:" + SpecificationItemId.LEGACY_ID_PATTERN + "))`?.*"),
    NEEDS_INT("Needs:(\\s*\\w+\\s*(?:,\\s*\\w+\\s*)*)"),
    NEEDS("Needs:\\s*"),
    NEEDS_REF(PatternConstants.UP_TO_3_WHITESPACES + PatternConstants.BULLETS
            + "(?:.*\\W)?" //
            + "(\\p{Alpha}+)" //
            + "(?:\\W.*)?"),
    NOT_EMPTY("([^\n\r]+)"),
    RATIONALE("Rationale:\\s*"),
    STATUS("Status:\\s*(approved|proposed|draft)\\s*"),
    TAGS_INT("Tags:(\\s*\\w+\\s*(?:,\\s*\\w+\\s*)*)"),
    TAGS("Tags:\\s*"),
    TAG_ENTRY(PatternConstants.UP_TO_3_WHITESPACES + PatternConstants.BULLETS
            + "\\s*" //
            + "(.*)"),
    TITLE("#+\\s*(.*)");
    // @formatter:on

    private final Pattern pattern;

    MdPattern(final String regularExpression)
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
        private PatternConstants()
        {
            // not instantiable
        }

        public static final String ARTIFACT_TYPE = "[a-zA-Z]+";
        public static final String BULLETS = "[+*-]";
        private static final String UP_TO_3_WHITESPACES = "\\s{0,3}";
        // [impl->dsn~md.requirement-references~1]
        public static final String REFERENCE_AFTER_BULLET = UP_TO_3_WHITESPACES
                + PatternConstants.BULLETS + "(?:.*\\W)?" //
                + "((?:" + SpecificationItemId.ID_PATTERN + ")|(?:"
                + SpecificationItemId.LEGACY_ID_PATTERN + "))" //
                + "(?:\\W.*)?";
    }
}
