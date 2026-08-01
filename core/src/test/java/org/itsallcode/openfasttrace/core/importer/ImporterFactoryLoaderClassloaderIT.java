package org.itsallcode.openfasttrace.core.importer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Paths;

import org.itsallcode.openfasttrace.api.importer.ImporterContext;
import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.input.RealFileInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test to reproduce the classloader mismatch problem when discovering
 * importers.
 * <p>
 * This scenario will happen if OFT is used as a library with a custom
 * classloader. What we want in that case is a very clear exception message
 * because without an importer, neither tracing nor converting does have any
 * chance to work.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class ImporterFactoryLoaderClassloaderIT
{
    /**
     * This test demonstrates that if the Thread Context ClassLoader (TCCL) does
     * not have access to the importer factories, {@link ImporterFactoryLoader}
     * will fail to find any importers and should throw an exception.
     */
    @Test
    final void testGetImporterFactoryThrowsExceptionWhenTcclIsLimited(@Mock final ImporterContext context)
    {
        final ClassLoader originalTccl = Thread.currentThread().getContextClassLoader();
        final ClassLoader limitedClassLoader = new NoOpClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(limitedClassLoader);
            final ImporterFactoryLoader loaderWithLimitedTccl = new ImporterFactoryLoader(context);
            final InputFile file = RealFileInput.forPath(Paths.get("test.md"));
            final ImporterException exception = assertThrows(ImporterException.class,
                    () -> loaderWithLimitedTccl.getImporterFactory(file));
            assertThat(exception.getMessage().contains("No importers discovered"), is(true));
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(originalTccl);
        }
    }

    /**
     * A class loader that finds nothing.
     */
    private static class NoOpClassLoader extends ClassLoader
    {
        @Override
        public Class<?> loadClass(final String name) throws ClassNotFoundException
        {
            throw new ClassNotFoundException(name);
        }
    }
}
