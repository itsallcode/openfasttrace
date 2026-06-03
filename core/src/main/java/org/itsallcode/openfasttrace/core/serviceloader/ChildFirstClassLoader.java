package org.itsallcode.openfasttrace.core.serviceloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class loader will first try to load the class from the given URLs and
 * then from the parent class loader, unlike {@link URLClassLoader} which does
 * it the other way around.
 * <p>
 * This allows us to prefer external plugins over plugins on the classpath
 * included with OFT.
 * <p>
 * This is based on
 * https://medium.com/@isuru89/java-a-child-first-class-loader-cbd9c3d0305
 * <p>
 */
class ChildFirstClassLoader extends URLClassLoader
{
    private static final Logger LOGGER = Logger.getLogger(ChildFirstClassLoader.class);

    ChildFirstClassLoader(final String name, final URL[] urls, final ClassLoader parent)
    {
        super(name, urls, parent);
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException
    {
        final Class<?> loadedClass = findClass(name, resolve);
        if (resolve)
        {
            resolveClass(loadedClass);
        }
        return loadedClass;
    }

    private Class<?> findClass(final String name, final boolean resolve) throws ClassNotFoundException
    {
        // Has the class loaded already?
        final Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null)
        {
            return loadedClass;
        }
        return loadClassInternally(name, resolve);
    }

    private Class<?> loadClassInternally(final String name, final boolean resolve) throws ClassNotFoundException
    {
        try
        {
            // Find the class from given jar urls
            return findClass(name);
        }
        catch (final ClassNotFoundException ignore)
        {
            LOGGER.log(Level.FINEST, () -> "Unable to find class " + name + " with child logger."
                    + "Falling back to parent logger");
            // Class does not exist in the given URLs.
            // Let's try finding it in our parent classloader.
            // This will throw ClassNotFoundException on failure.
            return super.loadClass(name, resolve);
        }
    }
}
