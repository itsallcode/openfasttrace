package org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine;

import java.util.regex.Pattern;

/**
 * Common interface for text patterns used in line parsers.
 */
public interface LinePattern
{
    /**
     * Get the regular expression pattern associated with this line pattern.
     *
     * @return regular expression pattern
     */
    public Pattern getPattern();
}
