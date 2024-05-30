package org.itsallcode.openfasttrace.importer.lightweightmarkup.linereader;

public record LineContext(int lineNumber, String previousLine, String currentLine, String nextLine)
{

}
