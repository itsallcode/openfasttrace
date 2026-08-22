package org.itsallcode.openfasttrace.api.core;

import java.util.Objects;

/**
 * A zero-based position in source text. The character offset is measured in
 * UTF-16 code units, matching Java strings and editor protocols.
 */
public final class SourcePosition
{
    private final int line;
    private final int column;

    /**
     * Create a source position.
     *
     * @param line
     *            zero-based line number
     * @param column
     *            zero-based UTF-16 character offset
     */
    public SourcePosition(final int line, final int column)
    {
        if (line < 0 || column < 0)
        {
            throw new IllegalArgumentException("Source positions must not be negative");
        }
        this.line = line;
        this.column = column;
    }

    /**
     * Get the zero-based line number.
     *
     * @return the zero-based line number
     */
    public int getLine()
    {
        return this.line;
    }

    /**
     * Get the zero-based UTF-16 character offset.
     *
     * @return the zero-based UTF-16 character offset
     */
    public int getColumn()
    {
        return this.column;
    }

    @Override
    public boolean equals(final Object other)
    {
        if (!(other instanceof final SourcePosition that))
        {
            return false;
        }
        return this.line == that.line && this.column == that.column;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.line, this.column);
    }

    @Override
    public String toString()
    {
        return this.line + ":" + this.column;
    }
}
