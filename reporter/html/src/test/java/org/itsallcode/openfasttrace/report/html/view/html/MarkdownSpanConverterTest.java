package org.itsallcode.openfasttrace.report.html.view.html;

/*-
 * #%L
 * OpenFastTrace HTML Reporter
 * %%
 * Copyright (C) 2016 - 2021 itsallcode.org
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MarkdownSpanConverterTest
{
    @ParameterizedTest(name = "Line ''{0}'' converted to HTML ''{1}''")
    @CsvSource(
    {
            "'    code\n', '<pre>    code\n</pre>'",
            "'     code\n', '<pre>     code\n</pre>'",
            "'    code', '    code'",
            "`code`, <code>code</code>",
            "[text](https://example.com), <a href=\"https://example.com\">text</a>",
            "[ text ](https://example.com), <a href=\"https://example.com\"> text </a>",
            "prefix [text](https://example.com) suffix, prefix <a href=\"https://example.com\">text</a> suffix",
            "**text**, <strong>text</strong>",
            "***text**, *<strong>text</strong>",
            "**text***, <strong>text</strong>*",
            "pre**text**, pre<strong>text</strong>",
            "__text__, <strong>text</strong>",
            "___text__, _<strong>text</strong>",
            "__text___, <strong>text</strong>_",
            "__text__more, <strong>text</strong>more",
            "pre__text__, pre<strong>text</strong>",
            "*text*, <em>text</em>",
            "**text*, *<em>text</em>",
            "*text**, <em>text</em>*",
            "pre*text*, pre<em>text</em>",
            "_text_, <em>text</em>",
            "__text_, _<em>text</em>",
            "_text__, <em>text</em>_",
            "_text_more, <em>text</em>more",
            "pre_text_, pre<em>text</em>",
            "_text_ *text* __text__ **text** [text](https://example.com), <em>text</em> <em>text</em> <strong>text</strong> <strong>text</strong> <a href=\"https://example.com\">text</a>"
    })
    void assertConverted(final String inputLine, final String expected)
    {
        assertThat(MarkdownSpanConverter.convertLineContent(inputLine), equalTo(expected));
    }
}
