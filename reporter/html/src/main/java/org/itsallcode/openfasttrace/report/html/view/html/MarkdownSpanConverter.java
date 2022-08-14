package org.itsallcode.openfasttrace.report.html.view.html;

final class MarkdownSpanConverter
{
    // Prevent instantiation
    private MarkdownSpanConverter()
    {
    }

    static String convertLineContent(final String input)
    {
        String text = input;
        text = text.replaceAll("(    .*[\n])+", "<pre>$1</pre>");
        text = text.replaceAll("`(.*?)`", "<code>$1</code>");
        text = text.replaceAll("\\[(.*?)\\]\\((.*?)\\)", "<a href=\"$2\">$1</a>");
        text = text.replaceAll("(__|\\*\\*)(\\p{L}(?:.*\\p{L}))\\1", "<strong>$2</strong>");
        text = text.replaceAll("([_*])(\\p{L}(?:.*\\p{L}))\\1", "<em>$2</em>");
        return text;
    }
}