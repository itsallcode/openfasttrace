package openfasttrack.core;

import java.util.List;

public class Trace
{
    private final List<Link> links;

    public Trace(final List<Link> links)
    {
        this.links = links;
    }

    public int countAllLinks()
    {
        return this.links.size();
    }

    public long countLinksWithStatus(final LinkStatus status)
    {
        return this.links.stream().filter(link -> link.getStatus() == status).count();
    }
}
