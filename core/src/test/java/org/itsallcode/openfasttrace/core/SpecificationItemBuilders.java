package org.itsallcode.openfasttrace.core;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem.Builder;

/**
 * Convenience class containing static factory methods for creating builders.
 */
class SpecificationItemBuilders
{
    public static Builder prepare(final String artifactType, final String name, final int revision)
    {
        return SpecificationItem.builder().id(artifactType, name, revision);
    }
}
