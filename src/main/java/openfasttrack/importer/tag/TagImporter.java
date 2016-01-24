package openfasttrack.importer.tag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import openfasttrack.core.SpecificationItemId;
import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterException;

/**
 * {@link Importer} for tags in source code files.
 */
class TagImporter implements Importer
{
    private final static Logger LOG = Logger.getLogger(TagImporter.class.getName());
    private static final String ID_PATTERN = "\\p{Alpha}+~\\p{Alpha}\\w*(?:\\.\\p{Alpha}\\w*)*~\\d+";
    private static final String TAG_PREFIX_PATTERN = "\\[";
    private static final String TAG_SUFFIX_PATTERN = "\\]";

    private final BufferedReader reader;
    private final ImportEventListener listener;
    private final Pattern tagPattern;

    public TagImporter(final Reader reader, final ImportEventListener listener)
    {
        this.reader = new BufferedReader(reader);
        this.listener = listener;
        this.tagPattern = Pattern
                .compile(TAG_PREFIX_PATTERN + "(" + ID_PATTERN + ")" + TAG_SUFFIX_PATTERN);
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
            throw new ImporterException("Error reading file after line " + lineNumber, e);
        }
    }

    private void processLine(final int lineNumber, final String line)
    {
        final Matcher matcher = this.tagPattern.matcher(line);
        while (matcher.find())
        {
            this.listener.beginSpecificationItem();
            final SpecificationItemId id = SpecificationItemId.parseId(matcher.group(1));

            LOG.finest(() -> "Line " + lineNumber + ": found id '" + id + "'");
            this.listener.setId(id);
            this.listener.endSpecificationItem();
        }
    }
}
