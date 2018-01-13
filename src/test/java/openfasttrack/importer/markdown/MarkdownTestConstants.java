package openfasttrack.importer.markdown;

import openfasttrack.core.SpecificationItemId;

public class MarkdownTestConstants
{

    final static SpecificationItemId ID1 = SpecificationItemId.parseId("type~id~1");
    final static SpecificationItemId ID2 = SpecificationItemId.parseId("type~id~2");
    final static String TITLE = "Requirement Title";
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
    static final SpecificationItemId LEGACY_ID1 = SpecificationItemId.parseId("type.legacy_id, v3");
    static final String LEGACY_COVERED_ID1 = "impl.legacy_foo1, v3";
    static final String LEGACY_COVERED_ID2 = "impl.legacy_baz2,v4";
    static final String LEGACY_DEPENDS_ON_ID1 = "configuration.legacy.blah, v4711";
    static final String LEGACY_DEPENDS_ON_ID2 = "db.legacy.blah.blubb,  v42";

}
