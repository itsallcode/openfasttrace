package openfasttrack.cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CliArguments
{
    private List<String> unnamedValues;
    private String inputDir;
    private String outputFile;
    private String outputFormat;

    public Path getInputDir()
    {
        if (this.inputDir == null)
        {
            throw new MissingArgumentException("inputDir");
        }
        return Paths.get(this.inputDir);
    }

    public void setInputDir(final String inputDir)
    {
        this.inputDir = inputDir;
    }

    public Path getOutputFile()
    {
        if (this.outputFile == null)
        {
            throw new MissingArgumentException("outputFile");
        }
        return Paths.get(this.outputFile);
    }

    public void setOutputFile(final String outputFile)
    {
        this.outputFile = outputFile;
    }

    public String getCommand()
    {
        if (this.unnamedValues != null && this.unnamedValues.size() > 0)
        {
            return this.unnamedValues.get(0);
        }
        return null;
    }

    public void setUnnamedValues(final List<String> unnamedValues)
    {
        this.unnamedValues = unnamedValues;
    }

    public String getOutputFormat()
    {
        if (this.outputFormat == null)
        {
            throw new MissingArgumentException("outputFormat");
        }
        return this.outputFormat;
    }

    public void setOutputFormat(final String outputFormat)
    {
        this.outputFormat = outputFormat;
    }
}
