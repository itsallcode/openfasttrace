package openfasttrack.core.xml;

public class Attribute
{
    private final String name;
    private final String value;

    public Attribute(final String name, final String value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return this.name;
    }

    public String getValue()
    {
        return this.value;
    }
}
