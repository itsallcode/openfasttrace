package org.itsallcode.openfasttrace.view.html;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class TestMarkdownConverter
{
    private MarkdownConverter converter;

    @Before
    public void prepareEachTest()
    {
        this.converter = new MarkdownConverter();
    }

    @Test
    public void testConvertCode()
    {
        assertThat(this.converter.convert("This text `contains code ` and regular text"),
                equalTo("This text <code>contains code </code> and regular text"));
    }

    @Test
    public void testConvertLink()
    {
        assertThat(this.converter.convert("See [link label](#link-target) for details."),
                equalTo("See <a href=\"#link-target\">link label</a> for details."));
    }

    @Test
    public void testConvertEmphasis()
    {
        assertThat(this.converter.convert("This _is important_, believe me!"),
                equalTo("This <em>is important</em>, believe me!"));
        assertThat(this.converter.convert("This *is important*, believe me!"),
                equalTo("This <em>is important</em>, believe me!"));
    }

    @Test
    public void testConvertStrong()
    {
        assertThat(this.converter.convert("This __is very important__, believe me!"),
                equalTo("This <strong>is very important</strong>, believe me!"));
        assertThat(this.converter.convert("This **is very important**, believe me!"),
                equalTo("This <strong>is very important</strong>, believe me!"));
    }
}
