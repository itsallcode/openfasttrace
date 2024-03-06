package org.itsallcode.openfasttrace.report.html.view.html;

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
            "_text_ *text* __text__ **text** [text](https://example.com), <em>text</em> <em>text</em> <strong>text</strong> <strong>text</strong> <a href=\"https://example.com\">text</a>",
            // [utest->dsn~reporting.html.escape-html~1]
            "pre<tag>post, pre&lt;tag&gt;post",
            "pre</tag>post, pre&lt;/tag&gt;post",
            "pre<tag/>post, pre&lt;tag/&gt;post",
    })
    void assertConverted(final String inputLine, final String expected)
    {
        assertThat(MarkdownSpanConverter.convertLineContent(inputLine), equalTo(expected));
    }

    // [utest->dsn~reporting.html.escape-html~1]
    @ParameterizedTest(name = "Line ''{0}'' escaped as HTML ''{1}''")
    @CsvSource(
    {
            "no tag, no tag",
            "'no \t\ntag', 'no \t\ntag'",
            "pre<tag>post, pre&lt;tag&gt;post",
            "pre</tag>post, pre&lt;/tag&gt;post",
            "pre<tag/>post, pre&lt;tag/&gt;post",
            "<strong>strong</strong>, &lt;strong&gt;strong&lt;/strong&gt;",
    })
    void assertHtmlEscaped(final String inputLine, final String expected)
    {
        assertThat(MarkdownSpanConverter.escapeHtml(inputLine), equalTo(expected));
    }
}
