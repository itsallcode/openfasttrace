package openfasttrack.core;

import openfasttrack.core.SpecificationItem.Builder;

/**
 * Convenience class containing static factory methods for creating builders.
 */
public class SpecificationItemBuilders
{
    public static Builder prepare(final String artifactType, final String name, final int revision)
    {
        return new SpecificationItem.Builder().id(artifactType, name, revision);
    }
}