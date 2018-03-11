package org.itsallcode.openfasttrace;

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
import java.util.Collections;
import java.util.Set;

import javax.annotation.Generated;

/**
 * Settings for import filtering
 */
public final class FilterSettings
{
    private final Set<String> artifactTypes;

    private FilterSettings(final Builder builder)
    {
        this.artifactTypes = builder.artifactTypes;
    }

    /**
     * Get the artifact types the filter must match.
     * 
     * @return artifact types
     */
    public Set<String> getArtifactTypes()
    {
        return this.artifactTypes;
    }

    /**
     * Check if one or more filters are set.
     * 
     * @return <code>true</code> if and filter criteria is set
     */
    public boolean isArtifactTypeCriteriaSet()
    {
        return this.artifactTypes != null && !this.artifactTypes.isEmpty();
    }

    /**
     * Check if any kind of filter criteria is set.
     * 
     * @return <code>true</code> if any filter criteria is set
     */
    public boolean isAnyCriteriaSet()
    {
        return isArtifactTypeCriteriaSet();
    }

    @Generated("org.eclipse.Eclipse")
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.artifactTypes == null) ? 0 : this.artifactTypes.hashCode());
        return result;
    }

    @Generated("org.eclipse.Eclipse")
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof FilterSettings))
        {
            return false;
        }
        final FilterSettings other = (FilterSettings) obj;
        if (this.artifactTypes == null)
        {
            if (other.artifactTypes != null)
            {
                return false;
            }
        }
        else if (!this.artifactTypes.equals(other.artifactTypes))
        {
            return false;
        }
        return true;
    }

    /**
     * Create filter settings that allow everything to pass unfiltered
     * 
     * @return <code>FilterSettings</code> that allow everything to pass
     *         unfiltered
     */
    public static FilterSettings createAllowingEverything()
    {
        return new FilterSettings.Builder().build();
    }

    /**
     * Builder for {@link FilterSettings}
     */
    public static class Builder
    {
        private Set<String> artifactTypes = Collections.emptySet();

        /**
         * Set the list of artifact types that the filter matches.
         * 
         * @param artifactTypes
         *            artifact types
         * @return <code>this</code> for fluent programming
         */
        public Builder artifactTypes(final Set<String> artifactTypes)
        {
            this.artifactTypes = artifactTypes;
            return this;
        }

        /**
         * Build an instance of type {@link FilterSettings}.
         * 
         * @return the new instance.
         */
        public FilterSettings build()
        {
            return new FilterSettings(this);
        }
    }
}
