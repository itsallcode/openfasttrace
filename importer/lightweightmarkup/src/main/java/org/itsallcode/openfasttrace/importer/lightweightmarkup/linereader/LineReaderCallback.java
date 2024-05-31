package org.itsallcode.openfasttrace.importer.lightweightmarkup.linereader;

/**
 * Callback for the {@link LineReader} to notify the caller about the lines that
 * have been read.
 */
public interface LineReaderCallback
{
    /**
     * Notify the caller about the next line that has been read.
     * 
     * @param context
     *            contains the current line and the surrounding lines
     */
    void nextLine(LineContext context);

    /**
     * Notify the caller that the file has been read completely.
     */
    void finishReading();
}
