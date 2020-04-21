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
