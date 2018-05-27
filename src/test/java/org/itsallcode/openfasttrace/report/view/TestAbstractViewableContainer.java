package org.itsallcode.openfasttrace.report.view;

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
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class TestAbstractViewableContainer
{
    private ViewContainerStub viewableContainer;
    @Mock
    private Viewable viewMockA;
    @Mock
    private Viewable viewMockB;

    @Before
    public void prepareEachTest()
    {
        MockitoAnnotations.initMocks(this);
        this.viewableContainer = new ViewContainerStub();
    }

    @Test
    public void testListOfChildrenIsInitiallyEmpty()
    {
        assertThat(this.viewableContainer.getChildren(), emptyCollectionOf(Viewable.class));
    }

    @Test
    public void add()
    {
        final ViewContainerStub expectedChild = new ViewContainerStub();
        this.viewableContainer.add(expectedChild);
        assertThat(this.viewableContainer.getChildren(), contains(expectedChild));
    }

    @Test
    public void testContainerCallsRenderMethodsOfChildren()
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

    private class ViewContainerStub extends AbstractViewContainer
    {
        /**
         * Get the list of contained viewable elements
         * 
         * @return list of viewable elements
         */
        public List<Viewable> getChildren()
        {
            return this.children;
        }

        @Override
        protected void renderBeforeChildren(final int level)
        {
            // TODO Auto-generated method stub
        }

        @Override
        protected void renderAfterChildren(final int level)
        {
            // TODO Auto-generated method stub
        }
    }
}