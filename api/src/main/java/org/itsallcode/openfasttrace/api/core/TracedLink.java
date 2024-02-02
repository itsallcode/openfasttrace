package org.itsallcode.openfasttrace.api.core;

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
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.otherLinkEnd == null) ? 0 : this.otherLinkEnd.hashCode());
        result = prime * result + ((this.status == null) ? 0 : this.status.hashCode());
        return result;
    }

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
        if (!(obj instanceof TracedLink))
        {
            return false;
        }
        final TracedLink other = (TracedLink) obj;
        if (this.otherLinkEnd == null)
        {
            if (other.otherLinkEnd != null)
            {
                return false;
            }
        }
        else if (!this.otherLinkEnd.equals(other.otherLinkEnd))
        {
            return false;
        }
        return this.status == other.status;
    }
}
