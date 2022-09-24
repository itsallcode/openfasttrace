package org.itsallcode.openfasttrace.report.html.view.html;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * The <code>MarkdownLineTransition</code> is a transition in the
 * {@link MarkdownLineStateMachine}.
 */
class MarkdownLineTransition
{
    private final MarkdownLineState from;
    private final MarkdownLineState to;
    private final Pattern pattern;
    private final String prefix;
    private final String postfix;
    private final Function<String, String> conversion;

    /**
     * Create a new instance of <code>MarkdownLineTransition</code>
     * 
     * @param from
     *            origin state of the transition
     * @param to
     *            target state of the transition
     * @param pattern
     *            pattern that must be matched to cause this transition
     * @param postfix
     *            postfix for the previous line
     * @param prefix
     *            prefix for the new line
     * @param conversion
     *            conversion to be applied on the line
     */
    public MarkdownLineTransition(final MarkdownLineState from, final MarkdownLineState to,
            final String pattern, final String prefix, final String postfix,
            final UnaryOperator<String> conversion)
    {
        super();
        this.from = from;
        this.to = to;
        this.pattern = Pattern.compile(pattern);
        this.prefix = prefix;
        this.postfix = postfix;
        this.conversion = conversion;
    }

    /**
     * Get the origin state of the transition
     * 
     * @return the origin state
     */
    public MarkdownLineState getFrom()
    {
        return this.from;
    }

    /**
     * Get the target state of the transition
     * 
     * @return the target state
     */
    public MarkdownLineState getTo()
    {
        return this.to;
    }

    /**
     * Get the pattern that must be matched to cause this transition
     * 
     * @return the trigger pattern
     */
    public Pattern getPattern()
    {
        return this.pattern;
    }

    /**
     * Get the prefix for the new line
     * 
     * @return the prefix for the new line
     */
    public String getPrefix()
    {
        return this.prefix;
    }

    /**
     * Get the postfix for the previous line
     * 
     * @return the postfix for the previous line
     */
    public String getPostfix()
    {
        return this.postfix;
    }

    /**
     * Get the conversion function to be applied on the line
     * 
     * @return the conversion function
     */
    public Function<String, String> getConversion()
    {
        return this.conversion;
    }
}
