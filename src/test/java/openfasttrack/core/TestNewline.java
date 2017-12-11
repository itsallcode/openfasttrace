package openfasttrack.core;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestNewline
{
    @Test
    public void testUnix()
    {
        assertThat(Newline.fromRepresentation("\n"), equalTo(Newline.UNIX));
    }

    @Test
    public void testWindows()
    {
        assertThat(Newline.fromRepresentation("\r\n"), equalTo(Newline.WINDOWS));
    }

    @Test
    public void testOldMac()
    {
        assertThat(Newline.fromRepresentation("\r"), equalTo(Newline.OLDMAC));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknown()
    {
        Newline.fromRepresentation("unknown");
    }
}
