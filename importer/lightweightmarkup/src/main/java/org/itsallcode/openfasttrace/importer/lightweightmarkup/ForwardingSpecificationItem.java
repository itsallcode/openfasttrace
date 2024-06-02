package org.itsallcode.openfasttrace.importer.lightweightmarkup;

import java.util.Arrays;
import java.util.List;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;

/**
 * The {@link ForwardingSpecificationItem} splits the textual
 * representation of a specification item that forwards needed artifact coverage
 * into is components.
 */
public class ForwardingSpecificationItem
{
    // The following markers are part of the syntax of a forward statement. They are public because they are required
    // in different parsers.
    /** Marker after which we expect the original artifact type. */
    public static final String ORIGINAL_MARKER = ":";
    /** Marker after which the artifact types are listed to which we forward. */
    public static final String FORWARD_MARKER = "-->";
    private final String skippedArtifactType;
    private final SpecificationItemId originalId;
    private final SpecificationItemId skippedId;
    private final List<String> targetArtifactTypes;

    /**
     * Create an instance of {@link ForwardingSpecificationItem}
     * 
     * @param forward
     *            the textual representation
     */
    public ForwardingSpecificationItem(final String forward)
    {
        final int posForwardMarker = forward.indexOf(FORWARD_MARKER);
        final int posOriginalMarker = forward.indexOf(ORIGINAL_MARKER);
        this.skippedArtifactType = forward.substring(0, posForwardMarker).trim();
        this.targetArtifactTypes = Arrays.asList(forward //
                .substring(posForwardMarker + FORWARD_MARKER.length(), posOriginalMarker) //
                .trim() //
                .split(",\\s*"));
        this.originalId = SpecificationItemId.parseId(forward //
                .substring(posOriginalMarker + ORIGINAL_MARKER.length()) //
                .trim());
        this.skippedId = SpecificationItemId.createId(this.skippedArtifactType,
                this.originalId.getName(), this.originalId.getRevision());
    }

    /**
     * The artifact type which forwards the needed coverage (in effect the one
     * that is "skipped" during authoring)
     * 
     * @return the "skipped" artifact type
     */
    public String getSkippedArtifactType()
    {
        return this.skippedArtifactType;
    }

    /**
     * The ID of the specification item that originally required coverage
     * 
     * @return the specification item ID
     */
    public SpecificationItemId getOriginalId()
    {
        return this.originalId;
    }

    /**
     * The constructed ID of the specification item that gets "skipped" by
     * forwarding the needed coverage
     * 
     * @return ID of the "skipped" item
     */
    public SpecificationItemId getSkippedId()
    {
        return this.skippedId;
    }

    /**
     * The list of artifact types the needed coverage is forwarded to
     * 
     * @return list of artifact types
     */
    public List<String> getTargetArtifactTypes()
    {
        return this.targetArtifactTypes;
    }
}