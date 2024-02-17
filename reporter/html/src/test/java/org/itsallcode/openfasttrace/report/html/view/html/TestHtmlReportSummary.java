package org.itsallcode.openfasttrace.report.html.view.html;

import org.itsallcode.openfasttrace.report.html.view.Viewable;
import org.junit.jupiter.api.Test;

class TestHtmlReportSummary extends AbstractTestHtmlRenderer
{
    @Test
    void testRender()
    {
        final Viewable view = this.factory.createReportSummary();
        view.render(2);
        assertOutputLines(
                "    <nav>",
                "    </nav>",
                "");
    }
}
