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

import static org.itsallcode.openfasttrace.core.SampleArtifactTypes.IMPL;
import static org.itsallcode.openfasttrace.core.SampleArtifactTypes.ITEST;
import static org.itsallcode.openfasttrace.core.SampleArtifactTypes.UTEST;
import static org.itsallcode.openfasttrace.report.view.html.CharacterConstants.CHECKMARK;
import static org.mockito.Mockito.when;

import org.itsallcode.openfasttrace.core.*;
import org.itsallcode.openfasttrace.report.view.Viewable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestHtmlSpecificationItem extends AbstractTestHtmlRenderer
{
    private static final SpecificationItemId ITEM_A_ID = SpecificationItemId
            .parseId("dsn~name-a~1");
    private static final SpecificationItemId ITEM_B_ID = SpecificationItemId
            .parseId("impl~name-b~1");
    private static final SpecificationItemId ITEM_C_ID = SpecificationItemId
            .parseId("utest~name-c~1");

    @Mock
    private LinkedSpecificationItem itemMockB;
    @Mock
    private LinkedSpecificationItem itemMockC;

    @Override
    @Before
    public void prepareEachTest()
    {
        super.prepareEachTest();
        MockitoAnnotations.initMocks(this);
        when(this.itemMockB.getId()).thenReturn(ITEM_B_ID);
        when(this.itemMockC.getId()).thenReturn(ITEM_C_ID);
    }

    @Test
    public void testRenderMinimalItem()
    {
        final SpecificationItem item = new SpecificationItem.Builder() //
                .id(ITEM_A_ID) //
                .title("Item A title") //
                .description("Single line description") //
                .build();
        renderItemOnIndentationLevel(item, 1);
        assertOutputLines( //
                "  <section class=\"sitem\" id=\"dsn~name-a~1\">", //
                "    <details>", //
                "      <summary title=\"dsn~name-a~1\">" + CHECKMARK
                        + " <b>Item A title</b><small>, rev. 1, dsn</small></summary>", //
                "      <p class=\"id\">" + ITEM_A_ID + "</p>", //
                "      <p>Single line description</p>", //
                "    </details>", //
                "  </section>", //
                "");
    }

    @Test
    public void testRenderMultiLineItem()
    {
        final SpecificationItem item = new SpecificationItem.Builder() //
                .id(ITEM_B_ID) //
                .title("Item B title") //
                .description("Description A\n\nDescription B") //
                .rationale("Rationale A\n\nRationale B") //
                .comment("Comment A\nComment B") //
                .build();
        renderItemOnIndentationLevel(item, 0);
        assertOutputLines( //
                "<section class=\"sitem\" id=\"impl~name-b~1\">", //
                "  <details>", //
                "    <summary title=\"impl~name-b~1\">" + CHECKMARK
                        + " <b>Item B title</b><small>, rev. 1, impl</small></summary>", //
                "    <p class=\"id\">" + ITEM_B_ID + "</p>", //
                "    <p>Description A</p><p>Description B</p>", //
                "    <h6>Rationale:</h6>", //
                "    <p>Rationale A</p><p>Rationale B</p>", //
                "    <h6>Comment:</h6>", //
                "    <p>Comment A Comment B</p>", //
                "  </details>", //
                "</section>", //
                "");
    }

    protected void renderItemOnIndentationLevel(final SpecificationItem item,
            final int indentationLevel)
    {
        final LinkedSpecificationItem linkedItem = new LinkedSpecificationItem(item);
        final Viewable view = this.factory.createSpecificationItem(linkedItem);
        view.render(indentationLevel);
    }

    @Test
    public void testRenderNeeds()
    {
        final SpecificationItem item = new SpecificationItem.Builder() //
                .id(ITEM_A_ID) //
                .addNeedsArtifactType(IMPL) //
                .addNeedsArtifactType(UTEST) //
                .build();
        final LinkedSpecificationItem linkedItem = new LinkedSpecificationItem(item);
        linkedItem.addCoveredArtifactType(IMPL);
        linkedItem.addOverCoveredArtifactType(ITEST);
        final Viewable view = this.factory.createSpecificationItem(linkedItem);
        view.render(0);
        assertOutputLines( //
                "<section class=\"sitem\" id=\"dsn~name-a~1\">", //
                "  <details>", //
                "    <summary title=\"dsn~name-a~1\">" + CHECKMARK
                        + " <b>name-a</b><small>, rev. 1, dsn</small></summary>", //
                "    <p class=\"id\">" + ITEM_A_ID + "</p>", //
                "    <h6>Needs: impl, <del>itest</del>, <ins>utest</ins></h6>", //
                "  </details>", //
                "</section>", //
                "");
    }

    @Test
    public void testRenderIncomingLinks()
    {
        final SpecificationItem item = new SpecificationItem.Builder() //
                .id(ITEM_A_ID) //
                .build();
        final LinkedSpecificationItem linkedItem = new LinkedSpecificationItem(item);
        linkedItem.addLinkToItemWithStatus(this.itemMockB, LinkStatus.COVERED_SHALLOW);
        linkedItem.addLinkToItemWithStatus(this.itemMockC, LinkStatus.COVERED_UNWANTED);
        final Viewable view = this.factory.createSpecificationItem(linkedItem);
        view.render(0);
        assertOutputLines( //
                "<section class=\"sitem\" id=\"dsn~name-a~1\">", //
                "  <details>", //
                "    <summary title=\"dsn~name-a~1\">" + CHECKMARK
                        + " <b>name-a</b><small>, rev. 1, dsn</small></summary>", //
                "    <p class=\"id\">" + ITEM_A_ID + "</p>", //
                "    <div class=\"in\">", //
                "      <h6>In: 2</h6>", //
                "      <ul>", //
                "        <li><a href=\"#" + ITEM_B_ID + "\">" + ITEM_B_ID + "</a></li>", //
                "        <li><a href=\"#" + ITEM_C_ID + "\">" + ITEM_C_ID
                        + "</a> <em>(unwanted coverage)</em></li>", //
                "      </ul>", //
                "    </div>", //
                "  </details>", //
                "</section>", //
                "");
    }
}