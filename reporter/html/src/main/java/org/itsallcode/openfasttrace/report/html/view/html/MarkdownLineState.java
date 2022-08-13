package org.itsallcode.openfasttrace.report.html.view.html;

/**
 * The <code>MarkdownLineState</code> represents the Markdown states that can be
 * switched by newlines and the way the next line starts.
 */
enum MarkdownLineState
{
    START, //
    PARAGRAPH, //
    UNORDERED_LIST, //
    UNORDERED_LIST_CONTINUED, //
    ORDERED_LIST, //
    ORDERED_LIST_CONTINUED, //
    PREFORMATTED, //
    TERMINATOR
}