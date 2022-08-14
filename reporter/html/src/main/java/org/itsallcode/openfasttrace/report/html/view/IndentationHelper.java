package org.itsallcode.openfasttrace.report.html.view;

/**
 * Contains static helper methods for indentation.
 */
public final class IndentationHelper
{
    private static final int INDENT_SPACES_PER_LEVEL = 2;

    private IndentationHelper()
    {
        // prevent instantiation.
    }

    /**
     * Create indentation prefix (i.e. white spaces)
     * 
     * @param level
     *            indentation level
     * @return <code>level</code> white spaces
     */
    public static String createIndentationPrefix(final int level)
    {
        return new String(new char[level * INDENT_SPACES_PER_LEVEL]).replace("\0", " ");
    }
}
