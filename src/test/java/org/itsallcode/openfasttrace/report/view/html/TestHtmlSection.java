package org.itsallcode.openfasttrace.report.view.html;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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

import org.itsallcode.openfasttrace.report.view.Viewable;
import org.junit.Before;
import org.junit.Test;

public class TestHtmlSection extends AbstractTestHtmlRenderer
{
    private static final String ID = "section ID";
    private static final String TITLE = "section title";
    private static final int LEVEL = 1;

    @Override
    @Before
    public void prepareEachTest()
    {
        super.prepareEachTest();
    }

    @Test
    public void testRender()
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
