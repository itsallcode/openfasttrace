package org.itsallcode.openfasttrace.api.core;

import java.util.Objects;

/** A start-inclusive, end-exclusive range in source text. */
public final class SourceRange
{
    private final SourcePosition start;
    private final SourcePosition end;

    /**
     * Create a source range.
     *
     * @param start
     *            inclusive start position
     * @param end
     *            exclusive end position
     */
    public SourceRange(final SourcePosition start, final SourcePosition end)
    {
        this.start = Objects.requireNonNull(start, "start must not be null");
        this.end = Objects.requireNonNull(end, "end must not be null");
        final boolean isEndBeforeStart = end.getLine() < start.getLine();
        final boolean isSameLineAndEndBeforeStart = end.getLine() == start.getLine()
                && end.getColumn() < start.getColumn();
        if (isEndBeforeStart || isSameLineAndEndBeforeStart)
        {
            throw new IllegalArgumentException("The range end " + end + " must not precede its start " + start);
        }
    }

    /**
     * Get the inclusive start position.
     *
     * @return the inclusive start position
     */
    public SourcePosition getStart()
    {
        return this.start;
    }

    /**
     * Get the exclusive end position.
     *
     * @return the exclusive end position
     */
    public SourcePosition getEnd()
    {
        return this.end;
    }

    @Override
    public boolean equals(final Object other)
    {
        if (!(other instanceof final SourceRange that))
        {
            return false;
        }
        return Objects.equals(this.start, that.start) && Objects.equals(this.end, that.end);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.start, this.end);
    }

    @Override
    public String toString()
    {
        return this.start + " … " + this.end;
    }
}
