package openfasttrack.core.xml;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

public class Attribute
{
    private final String qName;
    private final String value;

    private Attribute(final String qName, final String value)
    {
        this.qName = qName;
        this.value = value;
    }

    public String getQname()
    {
        return this.qName;
    }

    public String getValue()
    {
        return this.value;
    }

    public static Map<String, Attribute> buildMap(final Attributes attr)
    {
        final Map<String, Attribute> attributes = new HashMap<>();
        for (int i = 0; i < attr.getLength(); i++)
        {
            final Attribute attribute = new Attribute(attr.getQName(i), attr.getValue(i));
            attributes.put(attribute.getQname(), attribute);
        }
        return attributes;
    }

    @Override
    public String toString()
    {
        return "Attribute [qName=" + this.qName + ", value=" + this.value + "]";
    }
}
