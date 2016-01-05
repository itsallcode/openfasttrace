package openfasttrack.importer.markdown;

import java.util.regex.Pattern;

/**
 * Patterns that describe tokens to be recognized within Markdown-style
 * specifications.
 */
public enum MdPattern
{
    // @formatter:off
    COMMENT("Comment:\\s*"),
    COVERS("Covers:\\s*"),
    COVERS_REF("\\s{0,3}\\*\\s*.*\\(#([^)])"),
    DEPENDS("Depends:\\s*"),
    DETAILS_REF("\\s{0,3}\\+\\s*.*\\(#([^)])"),
    DEPENDS_REF("\\s{0,3}-\\s*.*\\(#([^)])"),
    EMPTY("\\s*"),
    EVERYTHING(".*"),
    ID(".*<a +id *= *\"((?:\\w+\\.)+\\w+(?:~\\d+)?)\".*"),
    NEEDS("Needs:\\s*(\\w+(?:,\\s*\\w+)+)"),
    RATIONALE("Rationale:\\s*");
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
}