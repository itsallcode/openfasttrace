package openfasttrack.importer.legacytag;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;

import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterException;

class LegacyTagImporter implements Importer
{
    private final PathConfig pathConfig;
    private final BufferedReader reader;
    private final ImportEventListener listener;
    private final Path file;

    LegacyTagImporter(final PathConfig pathConfig, final Path file, final BufferedReader reader,
            final ImportEventListener listener)
    {
        this.pathConfig = pathConfig;
        this.file = file;
        this.reader = reader;
        this.listener = listener;
    }

    @Override
    public void runImport()
    {
        String line;
        int lineNumber = 0;
        try
        {
            while ((line = this.reader.readLine()) != null)
            {
                ++lineNumber;
                processLine(lineNumber, line);
            }
        }
        catch (final IOException e)
        {
            throw new ImporterException("Error reading file " + this.file + ":" + lineNumber, e);
        }
    }

    private void processLine(final int lineNumber, final String line)
    {

    }
}
