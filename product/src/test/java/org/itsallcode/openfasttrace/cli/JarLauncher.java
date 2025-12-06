package org.itsallcode.openfasttrace.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hamcrest.Matcher;
import org.itsallcode.process.SimpleProcess;
import org.itsallcode.process.SimpleProcessBuilder;

import com.exasol.mavenprojectversiongetter.MavenProjectVersionGetter;

/**
 * This simplifies launching the OFT executable JAR file for integration tests.
 */
public final class JarLauncher
{
    private static final Logger LOG = Logger.getLogger(JarLauncher.class.getName());
    private final SimpleProcess<String> process;
    private final Builder builder;

    private JarLauncher(final SimpleProcess<String> process, final Builder builder)
    {
        this.process = process;
        this.builder = builder;
    }

    private static JarLauncher start(final Builder builder)
    {
        final Path jarPath = getExecutableJarPath();
        if (!Files.exists(jarPath))
        {
            throw new IllegalStateException(
                    "Executable JAR not found at %s. Run 'mvn -T1C package -DskipTests' to build it."
                            .formatted(jarPath));
        }
        final List<String> command = new ArrayList<>();
        command.addAll(createJavaLaunchArgs(jarPath));
        if (builder.args != null)
        {
            command.addAll(builder.args);
        }

        LOG.info("Starting command %s in working dir %s...".formatted(command, builder.workingDir));
        final SimpleProcess<String> process = SimpleProcessBuilder.create()
                .command(command)
                .workingDir(builder.workingDir)
                .redirectErrorStream(false)
                .streamLogLevel(Level.FINE)
                .start();
        return new JarLauncher(process, builder);
    }

    private static List<String> createJavaLaunchArgs(final Path jarPath)
    {
        final String javaExecutable = getJavaExecutable().toString();
        return List.of(javaExecutable, "-jar", jarPath.toString());
    }

    private static Path getExecutableJarPath()
    {
        final String jarFileName = "openfasttrace-%s.jar".formatted(getCurrentProjectVersion());
        return Path.of("target").resolve(jarFileName).toAbsolutePath();
    }

    private static String getCurrentProjectVersion()
    {
        return MavenProjectVersionGetter.getProjectRevision(Path.of("../parent/pom.xml").toAbsolutePath());
    }

    private static Path getJavaExecutable()
    {
        return ProcessHandle.current().info().command()
                .map(Path::of)
                .orElseThrow(() -> new IllegalStateException("Java executable not found"));
    }

    private void assertExpectationsAfterTerminated(final Duration timeout)
    {
        LOG.fine("Waiting %s for process %d to terminate...".formatted(timeout, process.pid()));
        process.waitForTermination(timeout);
        final int exitValue = process.exitValue();
        LOG.fine("Process %d terminated with exit code %d".formatted(process.pid(), exitValue));
        assertAll(
                () -> assertThat(
                        "exit code (std out: %s, std err: %s)".formatted(process.getStdOut(), process.getStdErr()),
                        exitValue, equalTo(builder.expectedExitCode)),
                () -> {
                    if (builder.expectedStdOut != null)
                    {
                        assertThat("std out", process.getStdOut(), builder.expectedStdOut);
                    }
                }, () -> {
                    if (builder.expectedStdErr != null)
                    {
                        assertThat("std err", process.getStdErr(), builder.expectedStdErr);
                    }
                });
    }

    /**
     * Create a new {@link Builder} for launching the OFT JAR.
     * 
     * @return builder for launching the OFT JAR
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder for launching the OFT JAR.
     */
    public static final class Builder
    {
        private Path workingDir;
        private List<String> args;
        private int expectedExitCode = 0;
        private Matcher<String> expectedStdOut;
        private Matcher<String> expectedStdErr;
        private Duration timeout = Duration.ofSeconds(3);

        private Builder()
        {
        }

        /**
         * Set the working directory of the new process to the current working
         * directory.
         * 
         * @return {@code this} for method chaining
         */
        public Builder currentWorkingDir()
        {
            return this.workingDir(Path.of(System.getProperty("user.dir")));
        }

        /**
         * Set the working directory of the new process.
         * 
         * @param workingDir
         *            the working directory
         * @return {@code this} for method chaining
         */
        public Builder workingDir(final Path workingDir)
        {
            this.workingDir = workingDir;
            return this;
        }

        /**
         * Set the arguments for the new process.
         * 
         * @param args
         *            the arguments
         * @return {@code this} for method chaining
         */
        public Builder args(final List<String> args)
        {
            this.args = args;
            return this;
        }

        /**
         * Set the timeout for waiting for process termination.
         * 
         * @param timeout
         *            the timeout
         * @return {@code this} for method chaining
         */
        public Builder timeout(final Duration timeout)
        {
            this.timeout = timeout;
            return this;
        }

        /**
         * Expect a successful exit code (0).
         * 
         * @return {@code this} for method chaining
         */
        public Builder successExitCode()
        {
            return this.expectedExitCode(0);
        }

        /**
         * Set the expected exit code of the new process.
         * 
         * @param expectedExitCode
         *            the expected exit code
         * @return {@code this} for method chaining
         */
        public Builder expectedExitCode(final int expectedExitCode)
        {
            this.expectedExitCode = expectedExitCode;
            return this;
        }

        /**
         * Set the matcher for the expected standard output.
         * 
         * @param expectedStdOut
         *            the matcher for standard output
         * @return {@code this} for method chaining
         */
        public Builder expectStdOut(final Matcher<String> expectedStdOut)
        {
            this.expectedStdOut = expectedStdOut;
            return this;
        }

        /**
         * Set the matcher for the expected standard error.
         * 
         * @param expectedStdErr
         *            the matcher for standard error
         * @return {@code this} for method chaining
         */
        public Builder expectStdErr(final Matcher<String> expectedStdErr)
        {
            this.expectedStdErr = expectedStdErr;
            return this;
        }

        /**
         * Launch the JAR and verify the expectations.
         */
        public void verify()
        {
            JarLauncher.start(this).assertExpectationsAfterTerminated(this.timeout);
        }
    }
}
