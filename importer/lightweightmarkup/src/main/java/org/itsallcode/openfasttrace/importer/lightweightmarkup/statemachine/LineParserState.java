package org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine;

/**
 * This enum defines the state the line parser for lightweight markup languages
 * can be in.
 */
public enum LineParserState
{
    /**
     * Parser started (at beginning of the file) or outside a specification
     * item
     */
    START,
    /** Inside a specification item */
    SPEC_ITEM,
    /** Inside a description section */
    DESCRIPTION,
    /** Inside a provided coverage section */
    COVERS,
    /** Inside a section describing dependencies */
    DEPENDS,
    /** Inside a rationale section */
    RATIONALE,
    /** Inside a comment section */
    COMMENT,
    /** Inside a section defining the required coverage (inline form) */
    NEEDS_LINE,
    /** Required coverage (list form) */
    NEEDS_LIST,
    /** Found a title */
    TITLE,
    /** Found tags */
    TAGS,
    /** Code block */
    CODE_BLOCK,
    /** Reached the end of the file */
    EOF
}
