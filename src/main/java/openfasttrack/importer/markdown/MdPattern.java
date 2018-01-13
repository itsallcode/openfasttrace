package openfasttrack.importer.markdown;

/*-
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 - 2017 hamstercommunity
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.regex.Pattern;

import openfasttrack.core.SpecificationItemId;

/**
 * Patterns that describe tokens to be recognized within Markdown-style
 * specifications.
 */
public enum MdPattern
{
    // [impl~md.specification_item_title~1]

    // @formatter:off
    COMMENT("Comment:\\s*"),
    COVERS("Covers:\\s*"),
    COVERS_REF(PatternConstants.REFERENCE_AFTER_BULLET),
    DEPENDS("Depends:\\s*"),
    DEPENDS_REF(PatternConstants.REFERENCE_AFTER_BULLET),
    DESCRIPTION("Description:\\s*"),
    EMPTY("(\\s*)"),
    EVERYTHING("(.*)"),
    ID("`?(" + SpecificationItemId.ID_PATTERN + "|" + SpecificationItemId.LEGACY_ID_PATTERN + ")`?.*"),
    NEEDS("Needs:\\s*(\\w+(?:,\\s*\\w+)*)"),
    RATIONALE("Rationale:\\s*"),
    TITLE("#+\\s*(.*)");
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

    private static class PatternConstants
    {
        private PatternConstants()
        {
            // not instantiable
        }

        public static final String BULLETS = "[+*-]";
        // [impl->dsn~md.requirement-references~1]
        public static final String REFERENCE_AFTER_BULLET = "\\s{0,3}" + PatternConstants.BULLETS
                + "(?:.*\\W)?(" + SpecificationItemId.ID_PATTERN + ")(?:\\W.*)?";
    }
}
