package org.itsallcode.openfasttrace.report.html.view.html;

class MarkdownConverter
{
    private final MarkdownLineStateMachine machine = new MarkdownLineStateMachine();

    String convert(final String input)
    {
        return this.machine.run(input);
    }
}