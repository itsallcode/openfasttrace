package org.itsallcode.openfasttrace.importer.restructuredtext;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.importer.lightweightmarkup.ForwardingSpecificationItem;
import org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine.LinePattern;
import org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine.SimpleLinePattern;

/**
 * Patterns that describe tokens to be recognized within reStructured Text
 * specifications.
 */
enum RstPattern
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
            + ForwardingSpecificationItem.FORWARD_MARKER
            + "\\s*"
            + PatternConstants.ARTIFACT_TYPE
            + "(?:,\\s*"
            + PatternConstants.ARTIFACT_TYPE
            + ")*"
            + "\\s*"
            + ForwardingSpecificationItem.ORIGINAL_MARKER
            + "\\s*"
            + SpecificationItemId.ID_PATTERN
            + ").*?"),
    ID("`?(" + SpecificationItemId.ID_PATTERN + ")`?.*"),
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
    UNDERLINE("([-=`:.'\"~^_*+#<>]{3,})\\s*");
    // @formatter:on

    private final LinePattern pattern;

    RstPattern(final String regularExpression)
    {
        this.pattern = SimpleLinePattern.of(regularExpression);
    }

    /**
     * Get the regular expression pattern object
     *
     * @return the pattern
     */
    public LinePattern getPattern()
    {
        return this.pattern;
    }

    private static final class PatternConstants
    {
        public static final String ARTIFACT_TYPE = "[a-zA-Z]+";
        public static final String BULLETS = "[+*-]";
        private static final String UP_TO_3_WHITESPACES = "\\s{0,3}";
        // [impl->dsn~md.requirement-references~1]
        public static final String REFERENCE_AFTER_BULLET = UP_TO_3_WHITESPACES
                + PatternConstants.BULLETS + "(?:.*\\W)?" //
                + "(" + SpecificationItemId.ID_PATTERN + ")" //
                + "(?:\\W.*)?";

        private PatternConstants()
        {
            // not instantiable
        }
    }
}
