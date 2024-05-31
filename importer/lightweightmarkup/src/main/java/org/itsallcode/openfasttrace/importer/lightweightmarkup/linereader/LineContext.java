package org.itsallcode.openfasttrace.importer.lightweightmarkup.linereader;

/**
 * State of the {@link LineReader} at a given line.
 * 
 * @param lineNumber
 *            the current line number, starting with 1 for the first line
 * @param previousLine
 *            the previous line or {@code null} if the current line is the first
 *            line
 * @param currentLine
 *            the current line, never {@code null}
 * @param nextLine
 *            the next line or {@code null} if the current line is the last line
 */
public record LineContext(int lineNumber, String previousLine, String currentLine, String nextLine)
{

}
