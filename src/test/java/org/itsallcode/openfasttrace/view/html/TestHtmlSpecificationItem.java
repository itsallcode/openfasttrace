package org.itsallcode.openfasttrace.view.html;

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

import static org.itsallcode.openfasttrace.view.html.CharacterConstants.CHECKMARK;

import org.itsallcode.openfasttrace.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.itsallcode.openfasttrace.view.Viewable;
import org.junit.Before;
import org.junit.Test;

public class TestHtmlSpecificationItem extends AbstractTestHtmlRenderer
{
    private static final SpecificationItemId ITEM_A_ID = SpecificationItemId
            .parseId("dsn~name-a~1");

    @Override
    @Before
    public void prepareEachTest()
    {
        super.prepareEachTest();
    }

    @Test
    public void testRender()
    {
        final SpecificationItem item = new SpecificationItem.Builder() //
                .id(ITEM_A_ID) //
                .title("Item A title") //
                .description("Single line description") //
                .build();
        final LinkedSpecificationItem linkedItem = new LinkedSpecificationItem(item);
        final Viewable view = this.factory.createSpecificationItem(linkedItem);
        view.render(1);
        assertOutputLines( //
                "  <section class=\"sitem\" id=\"dsn~name-a~1\">", //
                "    <details>", //
                "      <summary title=\"dsn~name-a~1\">" + CHECKMARK
                        + "<b>Item A title</b><small>, rev. 1, dsn</small></summary>", //
                "      <p class=\"desc\">Single line description</p>", //
                "    </details>", //
                "  </section>", //
                "");
    }
}
