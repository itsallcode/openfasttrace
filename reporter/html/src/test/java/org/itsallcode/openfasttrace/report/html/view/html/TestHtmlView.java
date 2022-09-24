package org.itsallcode.openfasttrace.report.html.view.html;

import org.itsallcode.openfasttrace.report.html.view.Viewable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestHtmlView extends AbstractTestHtmlRenderer
{
    private static final String ID = "view ID";
    private static final String TITLE = "view title";

    @Override
    @BeforeEach
    public void prepareEachTest()
    {
        super.prepareEachTest();
    }

    @Test
    void testRender()
    {
        final Viewable view = this.factory.createView(ID, TITLE);
        view.render();
        assertOutputLinesWithoutCSS(//
                "<!DOCTYPE html>", //
                "<html>", //
                "  <head>", //
                "    <meta charset=\"UTF-8\">", //
                "    <style></style>", //
                "    <title>" + TITLE + "</title>", //
                "  </head>", //
                "  <body>", //
                "  </body>", //
                "</html>");
    }
}
