package org.itsallcode.openfasttrace.report.html.view.html;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
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

    private static final List<RegexReplacement> CODE_REPLACEMENTS = List.of(INDENTED_CODE, BACKTICK_QUOTED_CODE);
    private static final List<RegexReplacement> INLINE_MARKDOWN_REPLACEMENTS = List.of(LINK, BOLD_TEXT,
            EMPHASIZED_TEXT);

    // Prevent instantiation
    private MarkdownSpanConverter()
    {
    }

    // [impl->dsn~reporting.html.escape-html~1]
    static String convertLineContent(final String input)
    {
        String text = escapeHtml(input);
        final String delimiter = createUniqueDelimiter(text);
        final List<String> codeSpans = new ArrayList<>();
        for (final RegexReplacement codeReplacement : CODE_REPLACEMENTS)
        {
            text = extractCodeSpans(codeReplacement, text, codeSpans, delimiter);
        }
        for (final RegexReplacement replacement : INLINE_MARKDOWN_REPLACEMENTS)
        {
            text = replacement.apply(text);
        }
        return restoreCodeSpans(text, codeSpans, delimiter);
    }

    private static String createUniqueDelimiter(final String text)
    {
        String delimiter = "\u0000";
        while (text.contains(delimiter))
        {
            delimiter += "\u0000";
        }
        return delimiter;
    }

    private static String extractCodeSpans(final RegexReplacement codeReplacement, final String input,
            final List<String> codeSpans, final String delimiter)
    {
        final Matcher matcher = codeReplacement.pattern.matcher(input);
        final StringBuilder sb = new StringBuilder();
        while (matcher.find())
        {
            final String placeholder = delimiter + codeSpans.size() + delimiter;
            final String converted = codeReplacement.apply(matcher.group());
            codeSpans.add(converted);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(placeholder));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String restoreCodeSpans(String text, final List<String> codeSpans, final String delimiter)
    {
        for (int i = 0; i < codeSpans.size(); i++)
        {
            text = text.replace(delimiter + i + delimiter, codeSpans.get(i));
        }
        return text;
    }

    static String escapeHtml(String text)
    {
        text = text.replace("<", "&lt;");
        text = text.replace(">", "&gt;");
        return text;
    }

    private static final class RegexReplacement
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
