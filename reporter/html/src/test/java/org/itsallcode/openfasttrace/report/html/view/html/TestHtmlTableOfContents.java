package org.itsallcode.openfasttrace.report.html.view.html;

/*-
 * #%L
 * OpenFastTrace HTML Reporter
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
