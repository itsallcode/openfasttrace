package org.itsallcode.openfasttrace.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hamcrest.Matcher;
import org.itsallcode.process.SimpleProcess;
import org.itsallcode.process.SimpleProcessBuilder;

import com.exasol.mavenprojectversiongetter.MavenProjectVersionGetter;

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
        final Path jarPath = getExecutableJar(builder.jarNameTemplate);
        if (!Files.exists(jarPath))
        {
            throw new IllegalStateException(
                    "Executable JAR not found at %s. Run 'mvn -T1C package -DskipTests' to build it."
                            .formatted(jarPath));
        }
        final List<String> command = new ArrayList<>();
        command.addAll(javaLaunchArgs(jarPath, builder.mainClass));
        if (builder.args != null)
        {
            command.addAll(builder.args);
        }

        final SimpleProcess<String> process = SimpleProcessBuilder.create().command(command)
                .workingDir(builder.workingDir)
                .redirectErrorStream(false)
                .streamLogLevel(Level.INFO)
                .start();
        return new JarLauncher(process, builder);
    }

    private static List<String> javaLaunchArgs(final Path jarPath, final Class<?> mainClass)
    {
        final String javaExecutable = getJavaExecutable().toString();
        if (mainClass == null)
        {
            return List.of(javaExecutable, "-jar", jarPath.toString());
        }
        return List.of(javaExecutable, "-classpath", jarPath.toString(), mainClass.getName());
    }

    private static Path getExecutableJar(final String jarNameTemplate)
    {
        return Path.of("target")
                .resolve(Objects.requireNonNull(jarNameTemplate, "jarNameTemplate")
                        .formatted(getCurrentProjectVersion()))
                .toAbsolutePath();
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

    public void waitUntilTerminated(final Duration timeout)
    {
        process.waitForTermination(timeout);
        final int exitValue = process.exitValue();
        LOG.fine("Process %d terminated with exit code %d".formatted(process.pid(), exitValue));
        assertAll(() -> assertThat("exit code", exitValue, equalTo(builder.expectedExitCode)),
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

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String jarNameTemplate = "openfasttrace-%s.jar";
        private Path workingDir;
        private List<String> args;
        private int expectedExitCode = 0;
        private Matcher<String> expectedStdOut;
        private Matcher<String> expectedStdErr;
        private Class<?> mainClass;

        private Builder()
        {
        }

        public Builder jarNameTemplate(final String jarNameTemplate)
        {
            this.jarNameTemplate = jarNameTemplate;
            return this;
        }

        public Builder mainClass(final Class<?> mainClass)
        {
            this.mainClass = mainClass;
            return this;
        }

        public Builder currentWorkingDir()
        {
            return this.workingDir(Path.of(System.getProperty("user.dir")));
        }

        public Builder workingDir(final Path workingDir)
        {
            this.workingDir = workingDir;
            return this;
        }

        public Builder args(final List<String> args)
        {
            this.args = args;
            return this;
        }

        public Builder successExitCode()
        {
            return this.expectedExitCode(0);
        }

        public Builder expectedExitCode(final int expectedExitCode)
        {
            this.expectedExitCode = expectedExitCode;
            return this;
        }

        public Builder expectStdOut(final Matcher<String> expectedStdOut)
        {
            this.expectedStdOut = expectedStdOut;
            return this;
        }

        public Builder expectStdErr(final Matcher<String> expectedStdErr)
        {
            this.expectedStdErr = expectedStdErr;
            return this;
        }

        public JarLauncher start()
        {
            return JarLauncher.start(this);
        }
    }

}
