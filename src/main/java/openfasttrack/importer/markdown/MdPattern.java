package openfasttrack.importer.markdown;

import java.util.regex.Pattern;

/**
 * Patterns that describe tokens to be recognized within Markdown-style
 * specifications.
 */
public enum MdPattern
{
    EVERYTHING(".*"), ID(".*<a +id *= *\"((?:\\w+\\.)+\\w+(?:~\\d+)?)\".*"), COVERS(
            "#*\\s*Covers:\\s*"), REFERENCE(".*\\(#([^)])");

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