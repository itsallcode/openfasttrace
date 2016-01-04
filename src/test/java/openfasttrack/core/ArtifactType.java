package openfasttrack.core;

import java.util.ArrayList;
import java.util.List;

public class ArtifactType
{
    private final long bitmask;
    private final String id;
    private final String name;

    private ArtifactType(final long bitmask, final String id, final String name)
    {
        this.bitmask = bitmask;
        this.id = id;
        this.name = name;
    }

    public final long getBitmask()
    {
        return bitmask;
    }

    public final String getId()
    {
        return id;
    }

    public final String getName()
    {
        return name;
    }

    private final static List<ArtifactType> artifactTypes = new ArrayList<ArtifactType>();

    static
    {
        final String[][] types = { { "feat", "feature" },
                { "req", "requirement" }, { "dsn", "design" },
                { "impl", "implementation" }, { "utest", "unit test" },
                { "itest", "integration test" }, { "stest", "system test" },
                { "ltest", "load test" }, { "uman", "user manual" },
                { "sman", "system manual" } };
        for (final String[] type : types)
        {
            artifactTypes.add(new ArtifactType(0, type[0], type[1]));
        }
    }
}