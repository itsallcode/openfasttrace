package org.itsallcode.openfasttrace.importer.gherkin;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.Consumer;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.junit.jupiter.api.Test;

class TestEventBuffer {
    @Test
    void testReplaysBeginSpecificationItem() {
        assertEvent(EventBuffer::beginSpecificationItem, ImportEventListener::beginSpecificationItem);
    }

    @Test
    void testReplaysId() {
        final SpecificationItemId id = SpecificationItemId.parseId("req~login~1");
        assertEvent(buffer -> buffer.setId(id), listener -> verify(listener).setId(id));
    }

    @Test
    void testReplaysTitle() {
        assertEvent(buffer -> buffer.setTitle("title"), listener -> verify(listener).setTitle("title"));
    }

    @Test
    void testReplaysStatus() {
        assertEvent(buffer -> buffer.setStatus(ItemStatus.DRAFT),
                listener -> verify(listener).setStatus(ItemStatus.DRAFT));
    }

    @Test
    void testReplaysDescription() {
        assertEvent(buffer -> buffer.appendDescription("description"),
                listener -> verify(listener).appendDescription("description"));
    }

    @Test
    void testReplaysRationale() {
        assertEvent(buffer -> buffer.appendRationale("rationale"),
                listener -> verify(listener).appendRationale("rationale"));
    }

    @Test
    void testReplaysComment() {
        assertEvent(buffer -> buffer.appendComment("comment"),
                listener -> verify(listener).appendComment("comment"));
    }

    @Test
    void testReplaysCoveredId() {
        final SpecificationItemId id = SpecificationItemId.parseId("req~login~1");
        assertEvent(buffer -> buffer.addCoveredId(id), listener -> verify(listener).addCoveredId(id));
    }

    @Test
    void testReplaysDependencyId() {
        final SpecificationItemId id = SpecificationItemId.parseId("req~login~1");
        assertEvent(buffer -> buffer.addDependsOnId(id), listener -> verify(listener).addDependsOnId(id));
    }

    @Test
    void testReplaysNeededArtifactType() {
        assertEvent(buffer -> buffer.addNeededArtifactType("dsn"),
                listener -> verify(listener).addNeededArtifactType("dsn"));
    }

    @Test
    void testReplaysTag() {
        assertEvent(buffer -> buffer.addTag("tag"), listener -> verify(listener).addTag("tag"));
    }

    @Test
    void testReplaysPathLocation() {
        assertEvent(buffer -> buffer.setLocation("file.feature", 1),
                listener -> verify(listener).setLocation("file.feature", 1));
    }

    @Test
    void testReplaysLocation() {
        final Location location = Location.create("file.feature", 2);
        assertEvent(buffer -> buffer.setLocation(location), listener -> verify(listener).setLocation(location));
    }

    @Test
    void testReplaysForwards() {
        assertEvent(buffer -> buffer.setForwards(true), listener -> verify(listener).setForwards(true));
    }

    @Test
    void testReplaysEndSpecificationItem() {
        assertEvent(EventBuffer::endSpecificationItem, ImportEventListener::endSpecificationItem);
    }

    private static void assertEvent(final Consumer<EventBuffer> addEvent,
            final Consumer<ImportEventListener> verifyEvent) {
        final EventBuffer buffer = new EventBuffer();
        addEvent.accept(buffer);
        final ImportEventListener listener = mock(ImportEventListener.class);
        buffer.replay(listener);
        verifyEvent.accept(listener);
    }
}
