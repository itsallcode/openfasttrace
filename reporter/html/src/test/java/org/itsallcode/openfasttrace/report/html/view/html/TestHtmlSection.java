package org.itsallcode.openfasttrace.report.html.view.html;

import org.itsallcode.openfasttrace.report.html.view.Viewable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestHtmlSection extends AbstractTestHtmlRenderer
{
    private static final String ID = "section ID";
    private static final String TITLE = "section title";
    private static final int LEVEL = 1;

    @Override
    @BeforeEach
    public void prepareEachTest()
    {
        super.prepareEachTest();
    }

    @Test
    void testRender()
    {
        final Viewable view = this.factory.createSection(ID, TITLE);
        view.render(LEVEL);
        assertOutputLines(//
                "  <section id=\"" + ID + "\">", //
                "    <h2>" + TITLE + "</h2>", //
                "  </section>", //
                "");
    }
}
