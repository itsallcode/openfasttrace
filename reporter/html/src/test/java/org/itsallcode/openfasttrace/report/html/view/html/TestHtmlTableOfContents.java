package org.itsallcode.openfasttrace.report.html.view.html;

import org.itsallcode.openfasttrace.report.html.view.Viewable;
import org.itsallcode.openfasttrace.report.html.view.ViewableContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestHtmlTableOfContents extends AbstractTestHtmlRenderer
{
    @Override
    @BeforeEach
    public void prepareEachTest()
    {
        super.prepareEachTest();
    }

    @Test
    void testRender()
    {
        final Viewable sectionA = this.factory.createSection("a", "section a");
        final Viewable sectionB = this.factory.createSection("b", "section b");
        final Viewable unreferenceableSection = this.factory.createSection(null, null);
        final ViewableContainer container = this.factory.createView("foo", "bar");
        container.add(sectionA);
        container.add(sectionB);
        container.add(unreferenceableSection);
        final Viewable toc = this.factory.createTableOfContents(container);
        toc.render(1);
        assertOutputLines(" | <a href=\"#a\">section a</a> &middot; <a href=\"#b\">section b</a>");
    }
}
