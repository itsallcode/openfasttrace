package openfasttrack.cli;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class TestCliArguments
{
    private CliArguments arguments;

    @Before
    public void setUp()
    {
        this.arguments = new CliArguments();
    }

    @Test
    public void testGetCommandWithUnnamedValuesNull()
    {
        this.arguments.setUnnamedValues(null);
        assertThat(this.arguments.getCommand(), equalTo(null));
    }

    @Test
    public void testGetCommandWithUnnamedValuesEmpty()
    {
        this.arguments.setUnnamedValues(Collections.emptyList());
        assertThat(this.arguments.getCommand(), equalTo(null));
    }
}