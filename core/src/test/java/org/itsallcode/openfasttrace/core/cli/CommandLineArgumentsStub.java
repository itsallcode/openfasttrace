package org.itsallcode.openfasttrace.core.cli;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
