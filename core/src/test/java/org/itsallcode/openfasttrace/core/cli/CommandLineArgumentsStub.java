package org.itsallcode.openfasttrace.core.cli;

import java.util.List;

class CommandLineArgumentsStub
{
    public enum StubEnum
    {
        VALUE1, VALUE2
    }

    private String a;
    private boolean b;
    private StubEnum c;
    private List<String> unnamedValues;
    private String theLongParameter;
    private Boolean d;

    public void setA(final String value)
    {
        this.a = value;
    }

    public void setB(final boolean value)
    {
        this.b = value;
    }

    public void setC(final StubEnum c)
    {
        this.c = c;
    }

    public void setD(final Boolean d)
    {
        this.d = d;
    }

    public void setUnnamedValues(final List<String> values)
    {
        this.unnamedValues = values;
    }

    public void setTheLongParameter(final String theLongParameter)
    {
        this.theLongParameter = theLongParameter;

    }

    public String getA()
    {
        return this.a;
    }

    public boolean isB()
    {
        return this.b;
    }

    public StubEnum getC()
    {
        return this.c;
    }

    public Boolean isD()
    {
        return this.d;
    }

    public List<String> getUnnamedValues()
    {
        return this.unnamedValues;
    }

    public String getTheLongParameter()
    {
        return this.theLongParameter;
    }
}
