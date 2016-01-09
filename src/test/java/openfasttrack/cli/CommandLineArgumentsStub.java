package openfasttrack.cli;

import java.util.List;

public class CommandLineArgumentsStub
{
    private String a;
    private boolean b;
    private List<String> unnamedValues;

    public void setA(final String value)
    {
        this.a = value;
    }

    public void setB(final boolean value)
    {
        this.b = value;
    }

    public void setUnnamedValues(final List<String> values)
    {
        this.unnamedValues = values;
    }

    public String getA()
    {
        return this.a;
    }

    public boolean isB()
    {
        return this.b;
    }

    public List<String> getUnnamedValues()
    {
        return this.unnamedValues;
    }
}