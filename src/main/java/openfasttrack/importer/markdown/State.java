package openfasttrack.importer.markdown;

enum State
{
    START, OUTSIDE, SPEC_ITEM, DESCRIPTION, COVERS, DEPENDS, RATIONALE, COMMENT, NEEDS, EOF
}