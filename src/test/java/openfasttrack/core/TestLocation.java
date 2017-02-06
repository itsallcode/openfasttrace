package openfasttrack.core;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class TestLocation
{
    @Test
    public void equalsContract()
    {
        EqualsVerifier.forClass(Location.class).verify();
    }
}
