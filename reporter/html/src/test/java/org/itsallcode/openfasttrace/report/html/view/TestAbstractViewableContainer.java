package org.itsallcode.openfasttrace.report.html.view;

import static org.hamcrest.MatcherAssert.assertThat;

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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestAbstractViewableContainer
{
    private ViewContainerStub viewableContainer;
    @Mock
    private Viewable viewMockA;
    @Mock
    private Viewable viewMockB;

    @BeforeEach
    public void prepareEachTest()
    {
        this.viewableContainer = new ViewContainerStub();
    }

    @Test
    void testListOfChildrenIsInitiallyEmpty()
    {
        assertThat(this.viewableContainer.getChildren(), emptyCollectionOf(Viewable.class));
    }

    @Test
    void add()
    {
        final ViewContainerStub expectedChild = new ViewContainerStub();
        this.viewableContainer.add(expectedChild);
        assertThat(this.viewableContainer.getChildren(), contains(expectedChild));
    }

    @Test
    void testContainerCallsRenderMethodsOfChildren()
    {
        final int containerIndentationLevel = 42;
        final int childIndentationLevel = containerIndentationLevel + 1;
        this.viewableContainer.add(this.viewMockA);
        this.viewableContainer.add(this.viewMockB);
        this.viewableContainer.render(containerIndentationLevel);
        final InOrder inOrder = Mockito.inOrder(this.viewMockA, this.viewMockB);
        inOrder.verify(this.viewMockA).render(childIndentationLevel);
        inOrder.verify(this.viewMockB).render(childIndentationLevel);
    }

    @Test
    void testIsReferenceableFalseWithDefaultConstructor()
    {
        assertThat(new ViewContainerStub().isReferenceable(), equalTo(false));
    }

    @Test
    void testIsReferenceableTrueIfIdAssigned()
    {
        assertThat(new ViewContainerStub("id", "title").isReferenceable(), equalTo(true));
    }

    @Test
    void testGetId()
    {
        final String expectedId = "foo";
        assertThat(new ViewContainerStub(expectedId, "").getId(), equalTo(expectedId));
    }

    @Test
    void testGetTitle()
    {
        final String expectedTitle = "bar";
        assertThat(new ViewContainerStub("", expectedTitle).getTitle(), equalTo(expectedTitle));
    }

    private class ViewContainerStub extends AbstractViewContainer
    {
        public ViewContainerStub()
        {
            super();
        }

        public ViewContainerStub(final String id, final String title)
        {
            super(id, title);
        }

        @Override
        protected void renderBeforeChildren(final int level)
        {
        }

        @Override
        protected void renderAfterChildren(final int level)
        {
        }
    }
}