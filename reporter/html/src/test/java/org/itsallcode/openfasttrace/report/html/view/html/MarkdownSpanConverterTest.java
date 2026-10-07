package org.itsallcode.openfasttrace.report.html.view.html;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class MarkdownSpanConverterTest
{
    @ParameterizedTest(name = "Line ''{0}'' converted to HTML ''{1}''")
    @CsvSource(
    {
            "'    code\n', '<pre>    code\n</pre>'",
            "'    code_with_underscore\n', '<pre>    code_with_underscore\n</pre>'",
            "'    code with `backticks` and _underscores_\n', '<pre>    code with `backticks` and _underscores_\n</pre>'",
            "'     code\n', '<pre>     code\n</pre>'",
            "'    code', '    code'",
            "`code`, <code>code</code>",
            "`pull_request_target`, <code>pull_request_target</code>",
            "`GITHUB_OUTPUT`, <code>GITHUB_OUTPUT</code>",
            "`pull_request_target` and `GITHUB_OUTPUT`, <code>pull_request_target</code> and <code>GITHUB_OUTPUT</code>",
            "`_text_`, <code>_text_</code>",
            "`__text__`, <code>__text__</code>",
            "`*text*`, <code>*text*</code>",
            "`**text**`, <code>**text**</code>",
            "`$foo_bar`, <code>$foo_bar</code>",
            "`\\d+_test`, <code>\\d+_test</code>",
            "`<xml_tag>`, <code>&lt;xml_tag&gt;</code>",
            "`[text](https://example.com)`, <code>[text](https://example.com)</code>",
            "`pull_request_target` and _emphasis_ and **bold**, <code>pull_request_target</code> and <em>emphasis</em> and <strong>bold</strong>",
            "_emphasis_ `code_with_underscore` *emphasis*, <em>emphasis</em> <code>code_with_underscore</code> <em>emphasis</em>",
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

    @Test
    void testPlaceholderNoCollisionWithInputControlCharacters()
    {
        assertThat(MarkdownSpanConverter.convertLineContent("x \u00000\u0000 `y`"),
                equalTo("x \u00000\u0000 <code>y</code>"));
        assertThat(MarkdownSpanConverter.convertLineContent("`a` \u00001\u0000 `b`"),
                equalTo("<code>a</code> \u00001\u0000 <code>b</code>"));
    }

    // [utest->dsn~reporting.html.escape-html~1]
    @ParameterizedTest(name = "Line ''{0}'' converted to HTML ''{1}''")
    @MethodSource("provideSpecialTestCases")
    void assertConvertedSpecial(final String inputLine, final String expected)
    {
        assertThat(MarkdownSpanConverter.convertLineContent(inputLine), equalTo(expected));
    }

    private static java.util.stream.Stream<Arguments> provideSpecialTestCases()
    {
        return java.util.stream.Stream.of(
            Arguments.of("`a`, `b`0`c`", "<code>a</code>, <code>b</code>0<code>c</code>"),
            Arguments.of("`x` `y`0`z`", "<code>x</code> <code>y</code>0<code>z</code>"),
            Arguments.of("    indented\n0`backtick`", "<pre>    indented\n</pre>0<code>backtick</code>")
        );
    }

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
