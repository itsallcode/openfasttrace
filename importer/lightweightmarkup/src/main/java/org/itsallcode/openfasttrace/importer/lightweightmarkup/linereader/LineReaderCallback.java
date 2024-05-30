package org.itsallcode.openfasttrace.importer.lightweightmarkup.linereader;

public interface LineReaderCallback
{
    void nextLine(LineContext context);

    void finishReading();
}
