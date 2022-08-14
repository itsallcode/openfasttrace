package org.itsallcode.openfasttrace.report.html.view.html;

import org.itsallcode.openfasttrace.report.html.view.Viewable;
import org.junit.jupiter.api.Test;

class TestHtmlReportDetails extends AbstractTestHtmlRenderer
{
    @Test
    void testRender()
    {
        final Viewable view = this.factory.createReportDetails();
        view.render(1);
        assertOutputLines(//
                "  <main>", //
                "  </main>", //
                "");
    }
}
