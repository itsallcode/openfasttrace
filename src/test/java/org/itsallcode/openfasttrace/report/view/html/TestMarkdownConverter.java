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
package org.itsallcode.openfasttrace.report.view.html;

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
    public void testConvertPreformatted()
    {
        final String original = "Before preformatted" + System.lineSeparator() //
                + "    first line" + System.lineSeparator() //
                + "       second line" + System.lineSeparator() //
                + "    " + System.lineSeparator() //
                + "     third line   " + System.lineSeparator() //
                + "After preformatted";
        final String expected = "<p>Before preformatted</p>" //
                + "<pre>" //
                + "first line" + System.lineSeparator() //
                + "   second line" + System.lineSeparator() //
                + System.lineSeparator() //
                + " third line   " //
                + "</pre>" //
                + "<p>After preformatted</p>";
        assertThat(this.converter.convert(original), equalTo(expected));
    }

    @Test
    public void testConvertUnorderedList()
    {
        final String original = "Before list" + System.lineSeparator() //
                + "* first item" + System.lineSeparator() //
                + "  * second item" + System.lineSeparator() //
                + "   *  third item   " + System.lineSeparator() //
                + System.lineSeparator() //
                + "After list";
        final String expected = "<p>Before list</p>" //
                + "<ul>" //
                + "<li>first item</li>" //
                + "<li>second item</li>" //
                + "<li>third item</li>" //
                + "</ul>" //
                + "<p>After list</p>";
        assertThat(this.converter.convert(original), equalTo(expected));
    }

    @Test
    public void testConvertUnorderedListWithMultilineItems()
    {
        final String original = "Before list" + System.lineSeparator() //
                + "* first item" + System.lineSeparator() //
                + "first item continued" + System.lineSeparator() //
                + "  * second item" + System.lineSeparator() //
                + "    second item continued" + System.lineSeparator() //
                + System.lineSeparator() //
                + "After list";
        final String expected = "<p>Before list</p>" //
                + "<ul>" //
                + "<li>first item first item continued</li>" //
                + "<li>second item second item continued</li>" //
                + "</ul>" //
                + "<p>After list</p>";
        assertThat(this.converter.convert(original), equalTo(expected));

    }

    @Test
    public void testConvertOrdteredList()
    {
        final String original = "Before list" + System.lineSeparator() //
                + "1. first item" + System.lineSeparator() //
                + "  1. second item" + System.lineSeparator() //
                + "   1234.  third item   " + System.lineSeparator() //
                + System.lineSeparator() //
                + "After list";
        final String expected = "<p>Before list</p>" //
                + "<ol>" //
                + "<li>first item</li>" //
                + "<li>second item</li>" //
                + "<li>third item</li>" //
                + "</ol>" //
                + "<p>After list</p>";
        assertThat(this.converter.convert(original), equalTo(expected));
    }

    @Test
    public void testChainedParagraphs()
    {
        final String original = "First paragraph" + System.lineSeparator() //
                + "... continued" + System.lineSeparator() //
                + System.lineSeparator() //
                + "Second paragraph " + System.lineSeparator() //
                + "... also continued";
        final String expected = "<p>First paragraph ... continued</p>" //
                + "<p>Second paragraph ... also continued</p>";
        assertThat(this.converter.convert(original), equalTo(expected));

    }

    @Test
    public void testConvertCode()
    {
        assertThat(this.converter.convert("This text `contains code ` and regular text"),
                equalTo("<p>This text <code>contains code </code> and regular text</p>"));
    }

    @Test
    public void testConvertLink()
    {
        assertThat(this.converter.convert("See [link label](#link-target) for details."),
                equalTo("<p>See <a href=\"#link-target\">link label</a> for details.</p>"));
    }

    @Test
    public void testConvertEmphasis()
    {
        assertThat(this.converter.convert("This _is important_, believe me!"),
                equalTo("<p>This <em>is important</em>, believe me!</p>"));
        assertThat(this.converter.convert("This *is important*, believe me!"),
                equalTo("<p>This <em>is important</em>, believe me!</p>"));
    }

    @Test
    public void testConvertStrong()
    {
        assertThat(this.converter.convert("This __is very important__, believe me!"),
                equalTo("<p>This <strong>is very important</strong>, believe me!</p>"));
        assertThat(this.converter.convert("This **is very important**, believe me!"),
                equalTo("<p>This <strong>is very important</strong>, believe me!</p>"));
    }
}
