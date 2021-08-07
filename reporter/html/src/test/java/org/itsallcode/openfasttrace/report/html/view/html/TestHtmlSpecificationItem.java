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

import static org.itsallcode.openfasttrace.report.html.view.html.CharacterConstants.CHECK_MARK;
import static org.itsallcode.openfasttrace.report.html.view.html.CharacterConstants.CROSS_MARK;
import static org.itsallcode.openfasttrace.testutil.core.SampleArtifactTypes.IMPL;
import static org.itsallcode.openfasttrace.testutil.core.SampleArtifactTypes.ITEST;
import static org.itsallcode.openfasttrace.testutil.core.SampleArtifactTypes.UTEST;
import static org.mockito.Mockito.when;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.report.html.view.Viewable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TestHtmlSpecificationItem extends AbstractTestHtmlRenderer
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

    @Mock
    private SpecificationItem itemMock;

    @Override
    @BeforeEach
    public void prepareEachTest()
    {
        super.prepareEachTest();
        when(this.itemMockB.getId()).thenReturn(ITEM_B_ID);
        when(this.itemMockB.getItem()).thenReturn(itemMock);
        when(this.itemMockC.getId()).thenReturn(ITEM_C_ID);
        when(this.itemMockC.getItem()).thenReturn(itemMock);
    }

    @Test
    void testRenderMinimalItem()
    {
        final SpecificationItem item = SpecificationItem.builder() //
                .id(ITEM_A_ID) //
                .title("Item A title") //
                .description("Single line description") //
                .build();
        renderItemOnIndentationLevel(item, 1);
        assertOutputLines( //
                "  <section class=\"sitem\" id=\"dsn~name-a~1\">", //
                "    <details>", //
                "      <summary title=\"dsn~name-a~1\">" + CHECK_MARK
                        + " <b>Item A title</b><small>, rev. 1, dsn</small></summary>", //
                "      <p class=\"id\">" + ITEM_A_ID + "</p>", //
                "      <p>Single line description</p>", //
                "    </details>", //
                "  </section>", //
                "");
    }

    @Test
    void testRenderMultiLineItem()
    {
        final SpecificationItem item = SpecificationItem.builder() //
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
                "    <summary title=\"impl~name-b~1\">" + CHECK_MARK
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
    void testRenderNeeds()
    {
        final SpecificationItem item = SpecificationItem.builder() //
                .id(ITEM_A_ID) //
                .addNeedsArtifactType(IMPL) //
                .addNeedsArtifactType(ITEST) //
                .addNeedsArtifactType(UTEST) //
                .build();
        final LinkedSpecificationItem linkedItem = new LinkedSpecificationItem(item);
        final Viewable view = this.factory.createSpecificationItem(linkedItem);
        view.render(0);
        assertOutputLines( //
                "<section class=\"sitem\" id=\"dsn~name-a~1\">", //
                "  <details>", //
                "    <summary title=\"dsn~name-a~1\">" + CROSS_MARK
                        + " <b>name-a</b><small>, rev. 1, dsn</small></summary>", //
                "    <p class=\"id\">" + ITEM_A_ID + "</p>", //
                "    <h6>Needs: <ins>impl</ins>, <ins>itest</ins>, <ins>utest</ins></h6>", //
                "  </details>", //
                "</section>", //
                "");
    }

    @Test
    void testRenderIncomingLinks()
    {
        final SpecificationItem item = SpecificationItem.builder() //
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
                "    <summary title=\"dsn~name-a~1\">" + CROSS_MARK
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

    @Test
    void testRenderOutgoingLinks()
    {
        final SpecificationItem item = SpecificationItem.builder() //
                .id(ITEM_A_ID) //
                .build();
        final LinkedSpecificationItem linkedItem = new LinkedSpecificationItem(item);
        linkedItem.addLinkToItemWithStatus(this.itemMockB, LinkStatus.COVERS);
        linkedItem.addLinkToItemWithStatus(this.itemMockC, LinkStatus.UNWANTED);
        final Viewable view = this.factory.createSpecificationItem(linkedItem);
        view.render(0);
        assertOutputLines( //
                "<section class=\"sitem\" id=\"dsn~name-a~1\">", //
                "  <details>", //
                "    <summary title=\"dsn~name-a~1\">" + CROSS_MARK
                        + " <b>name-a</b><small>, rev. 1, dsn</small></summary>", //
                "    <p class=\"id\">" + ITEM_A_ID + "</p>", //
                "    <div class=\"out\">", //
                "      <h6>Out: 2</h6>", //
                "      <ul>", //
                "        <li><a href=\"#" + ITEM_B_ID + "\">" + ITEM_B_ID + "</a></li>", //
                "        <li><a href=\"#" + ITEM_C_ID + "\">" + ITEM_C_ID
                        + "</a> <em>(unwanted)</em></li>", //
                "      </ul>", //
                "    </div>", //
                "  </details>", //
                "</section>", //
                "");
    }

    @Test
    void testRenderOrigin()
    {
        final Location location = Location.create("foo/bar", 13);
        final SpecificationItem item = SpecificationItem.builder() //
                .id(ITEM_A_ID) //
                .location(location) //
                .build();
        final LinkedSpecificationItem linkedItem = new LinkedSpecificationItem(item);
        final SpecificationItem subItem = SpecificationItem.builder() //
                .id(ITEM_B_ID).location(Location.create("http://example.org/foo.txt", 3)).build();
        final LinkedSpecificationItem linkedSubItem = new LinkedSpecificationItem(subItem);
        linkedItem.addLinkToItemWithStatus(linkedSubItem, LinkStatus.COVERED_SHALLOW);
        final Viewable view = this.factory.createSpecificationItem(linkedItem);
        view.render(0);
        assertOutputLines("<section class=\"sitem\" id=\"dsn~name-a~1\">", //
                "  <details>", //
                "    <summary title=\"dsn~name-a~1\">" + CHECK_MARK
                        + " <b>name-a</b><small>, rev. 1, dsn</small></summary>", //
                "    <p class=\"id\">" + ITEM_A_ID + "</p>", //
                "    <p class=\"origin\">foo/bar:13</p>", //
                "    <div class=\"in\">", //
                "      <h6>In: 1</h6>", //
                "      <ul>", //
                "        <li><a href=\"#" + ITEM_B_ID + "\">" + ITEM_B_ID
                        + "</a> <span class=\"origin\"><a href=\"http://example.org/foo.txt\">http://example.org/foo.txt</a>:3</span></li>",
                //
                "      </ul>", //
                "    </div>", //
                "  </details>", //
                "</section>", //
                "");
    }
}
