package org.itsallcode.openfasttrace.importer.gherkin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;

/** Buffers legacy coverage-tag events until the current scenario has ended. */
final class EventBuffer implements ImportEventListener
{
    private final List<Consumer<ImportEventListener>> events = new ArrayList<>();

    @Override
    public void beginSpecificationItem()
    {
        this.events.add(ImportEventListener::beginSpecificationItem);
    }

    @Override
    public void setId(final SpecificationItemId id)
    {
        this.events.add(target -> target.setId(id));
    }

    @Override
    public void setTitle(final String title)
    {
        this.events.add(target -> target.setTitle(title));
    }

    @Override
    public void setStatus(final ItemStatus status)
    {
        this.events.add(target -> target.setStatus(status));
    }

    @Override
    public void appendDescription(final String fragment)
    {
        this.events.add(target -> target.appendDescription(fragment));
    }

    @Override
    public void appendRationale(final String fragment)
    {
        this.events.add(target -> target.appendRationale(fragment));
    }

    @Override
    public void appendComment(final String fragment)
    {
        this.events.add(target -> target.appendComment(fragment));
    }

    @Override
    public void addCoveredId(final SpecificationItemId id)
    {
        this.events.add(target -> target.addCoveredId(id));
    }

    @Override
    public void addDependsOnId(final SpecificationItemId id)
    {
        this.events.add(target -> target.addDependsOnId(id));
    }

    @Override
    public void addNeededArtifactType(final String artifactType)
    {
        this.events.add(target -> target.addNeededArtifactType(artifactType));
    }

    @Override
    public void addTag(final String tag)
    {
        this.events.add(target -> target.addTag(tag));
    }

    @Override
    public void setLocation(final String path, final int line)
    {
        this.events.add(target -> target.setLocation(path, line));
    }

    @Override
    public void endSpecificationItem()
    {
        this.events.add(ImportEventListener::endSpecificationItem);
    }

    @Override
    public void setLocation(final Location location)
    {
        this.events.add(target -> target.setLocation(location));
    }

    @Override
    public void setForwards(final boolean forwards)
    {
        this.events.add(target -> target.setForwards(forwards));
    }

    void replay(final ImportEventListener listener)
    {
        this.events.forEach(event -> event.accept(listener));
        this.events.clear();
    }
}
