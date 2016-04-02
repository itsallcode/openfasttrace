package openfasttrack.cli;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class TestCliStarter
{
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp()
    {
    }

    @Test
    public void testNoArg()
    {
        expectException(asList(), CliException.class, "No command given");
    }

    @Test
    public void testIllegalCommand()
    {
        expectException(asList("illegal"), CliException.class, "Invalid command 'illegal' given");
    }

    @Test
    public void testConvertNoArg()
    {
        expectException(asList("convert"), MissingArgumentException.class,
                "Argument 'inputDir' is missing");
    }

    @Test
    public void testConvertToSpecobject() throws IOException
    {
        final Path inputDir = Paths.get(".").toAbsolutePath();
        final Path outputFile = this.tempFolder.getRoot().toPath().resolve("output.xml");

        runCliStarter(asList("convert", "-inputDir", inputDir.toString(), "-outputFormat",
                "specobject", "-outputFile", outputFile.toString()));
        assertThat(Files.exists(outputFile), equalTo(true));
        assertThat(Files.size(outputFile), greaterThan(12400L));
    }

    @Test
    public void testTraceNoArg()
    {
        expectException(asList("trace"), MissingArgumentException.class,
                "Argument 'inputDir' is missing");
    }

    private void expectException(final List<String> args,
            final Class<? extends Exception> expectedExceptionType, final String expectedMessage)
    {
        this.thrown.expect(expectedExceptionType);
        this.thrown.expectMessage(expectedMessage);
        runCliStarter(args);
    }

    private void runCliStarter(final List<String> args)
    {
        CliStarter.main(args.toArray(new String[0]));
    }
}
