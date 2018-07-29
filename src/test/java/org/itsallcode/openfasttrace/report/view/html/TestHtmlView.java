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

public class TestHtmlView extends AbstractTestHtmlRenderer
{
    private static final String ID = "view ID";
    private static final String TITLE = "view title";

    @Override
    @Before
    public void prepareEachTest()
    {
        super.prepareEachTest();
    }

    @Test
    public void testRender()
    {
        final Viewable view = this.factory.createView(ID, TITLE);
        view.render(1);
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
