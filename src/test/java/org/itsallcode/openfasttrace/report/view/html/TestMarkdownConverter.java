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
    private static final String LINE_SEPARATOR = "\n";
    private MarkdownConverter converter;

    @Before
    public void prepareEachTest()
    {
        this.converter = new MarkdownConverter();
    }

    @Test
    public void testConvertPreformatted()
    {
        final String original = "Before preformatted" + LINE_SEPARATOR //
                + "    first line" + LINE_SEPARATOR //
                + "       second line" + LINE_SEPARATOR //
                + "    " + LINE_SEPARATOR //
                + "     third line   " + LINE_SEPARATOR //
                + "After preformatted";
        final String expected = "<p>Before preformatted</p>" //
                + "<pre>" //
                + "first line" + LINE_SEPARATOR //
                + "   second line" + LINE_SEPARATOR + LINE_SEPARATOR //
                + " third line   " //
                + "</pre>" //
                + "<p>After preformatted</p>";
        assertConverted(original, expected);
    }

    protected String convert(final String original)
    {
        return this.converter.convert(original);
    }

    @Test
    public void testConvertUnorderedList()
    {
        final String original = "Before list" + LINE_SEPARATOR //
                + "* first item" + LINE_SEPARATOR //
                + "  * second item" + LINE_SEPARATOR //
                + "   *  third item   " + LINE_SEPARATOR //
                + LINE_SEPARATOR //
                + "After list";
        final String expected = "<p>Before list</p>" //
                + "<ul>" //
                + "<li>first item</li>" //
                + "<li>second item</li>" //
                + "<li>third item</li>" //
                + "</ul>" //
                + "<p>After list</p>";
        assertConverted(original, expected);
    }

    protected void assertConverted(final String original, final String expected)
    {
        assertThat(convert(original), equalTo(expected));
    }

    @Test
    public void testConvertUnorderedListWithMultilineItems()
    {
        final String original = "Before list" + LINE_SEPARATOR //
                + "* first item" + LINE_SEPARATOR //
                + "first item continued" + LINE_SEPARATOR //
                + "  * second item" + LINE_SEPARATOR //
                + "    second item continued" + LINE_SEPARATOR //
                + LINE_SEPARATOR //
                + "After list";
        final String expected = "<p>Before list</p>" //
                + "<ul>" //
                + "<li>first item first item continued</li>" //
                + "<li>second item second item continued</li>" //
                + "</ul>" //
                + "<p>After list</p>";
        assertConverted(original, expected);

    }

    @Test
    public void testConvertOrderedList()
    {
        final String original = "Before list" + LINE_SEPARATOR //
                + "1. first item" + LINE_SEPARATOR //
                + "  1. second item" + LINE_SEPARATOR //
                + "   1234.  third item   " + LINE_SEPARATOR //
                + LINE_SEPARATOR //
                + "After list";
        final String expected = "<p>Before list</p>" //
                + "<ol>" //
                + "<li>first item</li>" //
                + "<li>second item</li>" //
                + "<li>third item</li>" //
                + "</ol>" //
                + "<p>After list</p>";
        assertConverted(original, expected);
    }

    @Test
    public void testCloseOrderedList()
    {
        assertConverted("5. foobar", "<ol><li>foobar</li></ol>");
    }

    @Test
    public void testCloseUnurderedList()
    {
        assertConverted("+ foobar", "<ul><li>foobar</li></ul>");
    }

    @Test
    public void testClosePreformatted()
    {
        assertConverted("     foobar  ", "<pre> foobar  </pre>");
    }

    @Test
    public void testCloseWithTerminator()
    {
        assertConverted("foobar\n\n\n", "<p>foobar</p>");
    }

    @Test
    public void testChainedParagraphs()
    {
        final String original = "First paragraph" + LINE_SEPARATOR //
                + "... continued" + LINE_SEPARATOR //
                + LINE_SEPARATOR //
                + "Second paragraph " + LINE_SEPARATOR //
                + "... also continued";
        final String expected = "<p>First paragraph ... continued</p>" //
                + "<p>Second paragraph ... also continued</p>";
        assertConverted(original, expected);

    }

    @Test
    public void testConvertCode()
    {
        assertConverted("This text `contains code ` and regular text",
                "<p>This text <code>contains code </code> and regular text</p>");
    }

    @Test
    public void testConvertLink()
    {
        assertConverted("See [link label](#link-target) for details.",
                "<p>See <a href=\"#link-target\">link label</a> for details.</p>");
    }

    @Test
    public void testConvertTwoLinksInOneLine()
    {
        assertConverted("Before [A](#to-a) between [B](#to-b) after.",
                "<p>Before <a href=\"#to-a\">A</a> between <a href=\"#to-b\">B</a> after.</p>");
    }

    @Test
    public void testConvertEmphasis()
    {
        assertConverted("This _is important_, believe me!",
                "<p>This <em>is important</em>, believe me!</p>");
        assertConverted("This *is important*, believe me!",
                "<p>This <em>is important</em>, believe me!</p>");
    }

    @Test
    public void testConvertStrong()
    {
        assertConverted("This __is very important__, believe me!",
                "<p>This <strong>is very important</strong>, believe me!</p>");
        assertConverted("This **is very important**, believe me!",
                "<p>This <strong>is very important</strong>, believe me!</p>");
    }
}
