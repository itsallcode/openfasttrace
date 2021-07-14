package org.itsallcode.openfasttrace.importer.markdown;

/*-
 * #%L
 * OpenFastTrace Markdown Importer
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;

class MarkdownTestConstants
{
    private MarkdownTestConstants()
    {
        // not instantiable
    }

    static final SpecificationItemId ID1 = SpecificationItemId.parseId("type~id~1");
    static final SpecificationItemId ID2 = SpecificationItemId.parseId("type~id~2");
    static final String TITLE = "Requirement Title";
    static final String DESCRIPTION_LINE1 = "Description";
    static final String DESCRIPTION_LINE2 = "";
    static final String DESCRIPTION_LINE3 = "More description";
    static final String RATIONALE_LINE1 = "Rationale";
    static final String RATIONALE_LINE2 = "More rationale";
    static final String COMMENT_LINE1 = "Comment";
    static final String COMMENT_LINE2 = "More comment";
    static final String COVERED_ID1 = "impl~foo1~1";
    static final String COVERED_ID2 = "impl~baz2~2";
    static final String NEEDS_ARTIFACT_TYPE1 = "artA";
    static final String NEEDS_ARTIFACT_TYPE2 = "artB";
    static final String DEPENDS_ON_ID1 = "configuration~blubb.blah.blah~4711";
    static final String DEPENDS_ON_ID2 = "db~blah.blubb~42";
    // Legacy Markdown format
    static final String LEGACY_ID = "type~type:legacy_id, v3";
    static final String LEGACY_COVERED_ID1 = "impl:legacy_foo1, v3";
    static final String LEGACY_COVERED_ID2 = "impl:legacy_baz2,v4";
    static final String LEGACY_DEPENDS_ON_ID1 = "configuration:legacy.blah, v4711";
    static final String LEGACY_DEPENDS_ON_ID2 = "db:legacy.blah.blubb,  v42";
    static final SpecificationItemId PARSED_LEGACY_ID = SpecificationItemId.parseId(LEGACY_ID);
    static final SpecificationItemId PARSED_LEGACY_COVERED_ID1 = SpecificationItemId
            .parseId(LEGACY_COVERED_ID1);
    static final SpecificationItemId PARSED_LEGACY_COVERED_ID2 = SpecificationItemId
            .parseId(LEGACY_COVERED_ID2);
    static final SpecificationItemId PARSED_LEGACY_DEPENDS_ON_ID1 = SpecificationItemId
            .parseId(LEGACY_DEPENDS_ON_ID1);
    static final SpecificationItemId PARSED_LEGACY_DEPENDS_ON_ID2 = SpecificationItemId
            .parseId(LEGACY_DEPENDS_ON_ID2);

}
