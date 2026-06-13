package org.itsallcode.openfasttrace.api.core;

import java.util.Objects;

/**
 * This class represents a link that had its status evaluated during a trace
 * run.
 */
public final class TracedLink
{
    private final LinkedSpecificationItem otherLinkEnd;
    private final LinkStatus status;

    /**
     * Create a new instance of a {@link TracedLink}
     * 
     * @param otherLinkEnd
     *            specification item the link points to or originates from
     * @param status
     *            status of the link
     */
    public TracedLink(final LinkedSpecificationItem otherLinkEnd, final LinkStatus status)
    {
        this.otherLinkEnd = otherLinkEnd;
        this.status = status;
    }

    /**
     * Get the specification item on the other end of the link
     * 
     * @return specification item the link points to or originates from
     */
    public LinkedSpecificationItem getOtherLinkEnd()
    {
        return this.otherLinkEnd;
    }

    /**
     * Get the link status
     * 
     * @return link status
     */
    public LinkStatus getStatus()
    {
        return this.status;
    }

    /**
     * Check if this is an incoming link.
     * 
     * @return {@code true} if the link originates at the other end
     */
    public boolean isIncoming()
    {
        return this.status.isIncoming();
    }

    /**
     * Check if this is an outgoing link.
     * 
     * @return {@code true} if the link points towards the other end
     */
    public boolean isOutgoing()
    {
        return this.status.isOutgoing();
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final TracedLink that)) {
            return false;
        }
        return Objects.equals(otherLinkEnd, that.otherLinkEnd) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(otherLinkEnd, status);
    }
}
