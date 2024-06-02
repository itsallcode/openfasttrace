package org.itsallcode.openfasttrace.importer.markdown;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;

class MarkdownTestConstants
{
    private MarkdownTestConstants()
    {
        // not instantiable
    }

    static final SpecificationItemId ID2 = SpecificationItemId.parseId("type~id~2");
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
