package org.itsallcode.openfasttrace.report.html.view.html;

import java.util.List;
import java.util.regex.Pattern;

final class MarkdownSpanConverter
{
    private static final RegexReplacement INDENTED_CODE = RegexReplacement.create("(    .*[\n])+", "<pre>$1</pre>");
    private static final RegexReplacement BACKTICK_QUOTED_CODE = RegexReplacement.create("`(.*?)`", "<code>$1</code>");
    private static final RegexReplacement LINK = RegexReplacement.create("\\[([^]]*?)\\]\\(([^)].*?)\\)",
            "<a href=\"$2\">$1</a>");
    private static final RegexReplacement BOLD_TEXT = RegexReplacement.create("(__|\\*\\*)(\\p{L}(?:.*\\p{L}))\\1",
            "<strong>$2</strong>");
    private static final RegexReplacement EMPHASIZED_TEXT = RegexReplacement.create("([_*])(\\p{L}(?:.*\\p{L}))\\1",
            "<em>$2</em>");

    private static final List<RegexReplacement> ALL_MARKDOWN_REPLACEMENTS = List.of(INDENTED_CODE, BACKTICK_QUOTED_CODE,
            LINK, BOLD_TEXT, EMPHASIZED_TEXT);

    // Prevent instantiation
    private MarkdownSpanConverter()
    {
    }

    // [impl->dsn~reporting.html.escape-html~1]
    static String convertLineContent(final String input)
    {
        String text = escapeHtml(input);
        for (final RegexReplacement replacement : ALL_MARKDOWN_REPLACEMENTS)
        {
            text = replacement.apply(text);
        }
        return text;
    }

    static String escapeHtml(String text)
    {
        text = text.replace("<", "&lt;");
        text = text.replace(">", "&gt;");
        return text;
    }

    private static class RegexReplacement
    {
        private final Pattern pattern;
        private final String replacement;

        private RegexReplacement(final Pattern pattern, final String replacement)
        {
            this.pattern = pattern;
            this.replacement = replacement;
        }

        private static RegexReplacement create(final String pattern, final String replacement)
        {
            return new RegexReplacement(Pattern.compile(pattern), replacement);
        }

        private String apply(final String text)
        {
            return this.pattern.matcher(text).replaceAll(replacement);
        }
    }
}
