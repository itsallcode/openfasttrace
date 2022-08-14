package org.itsallcode.openfasttrace.report.html.view;

import static org.hamcrest.MatcherAssert.assertThat;

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