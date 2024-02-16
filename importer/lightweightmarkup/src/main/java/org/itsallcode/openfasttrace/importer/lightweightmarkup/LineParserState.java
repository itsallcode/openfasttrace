package org.itsallcode.openfasttrace.importer.lightweightmarkup;

public enum LineParserState
{
    START, OUTSIDE, SPEC_ITEM, DESCRIPTION, COVERS, DEPENDS, RATIONALE, COMMENT, NEEDS, EOF, TITLE, TAGS
}