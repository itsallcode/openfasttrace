package openfasttrack.importer.markdown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import openfasttrack.core.SpecificationItem;
import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;

public class MarkdownImporter implements Importer
{

    private final BufferedReader reader;
    private ImportEventListener listener;

    public MarkdownImporter(final String text)
    {
        this.reader = new BufferedReader(new StringReader(text));
    }

    public MarkdownImporter(final Reader reader, final ImportEventListener listener)
    {
        this.reader = new BufferedReader(reader);
        this.listener = listener;
    }

    @Override
    public List<SpecificationItem> runImport()
    {
        final MarkdownImporterStateMachine stateMachine = new MarkdownImporterStateMachine(
                this.listener);
        String line;
        int lineNumber = 0;
        try
        {

            while ((line = this.reader.readLine()) != null)
            {
                ++lineNumber;
                stateMachine.step(line);
                System.out.println(lineNumber + " : " + line);
            }

        } catch (final IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
