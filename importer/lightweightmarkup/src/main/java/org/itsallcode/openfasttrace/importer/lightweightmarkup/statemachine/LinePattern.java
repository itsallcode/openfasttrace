package org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine;

import java.util.List;
import java.util.Optional;

/**
 * Common interface for text patterns used in line parsers.
 */
public interface LinePattern
{
    /**
     * Get the matches groups of the regular expression pattern in the given
     * line and its following line.
     * <p>
     * Implementors are free to ignore the following line if it is not needed.
     * </p>
     * 
     * @param line
     *            the current line
     * @param nextLine
     *            the following line or {@code null} if the current line is the
     *            last line
     * @return list of matching groups or an empty optional if the pattern does
     *         not match. If the pattern does not have any groups, the list will
     *         be empty.
     */
    Optional<List<String>> getMatches(final String line, final String nextLine);
}
