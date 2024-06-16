package org.itsallcode.openfasttrace.core.serviceloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * This class loader will first try to load the class from the given URLs and
 * then from the parent class loader, unlike {@link URLClassLoader} which does
 * it the other way around.
 * <p>
 * This is based on
 * https://medium.com/@isuru89/java-a-child-first-class-loader-cbd9c3d0305
 * <p>
 */
class ChildFirstClassLoader extends URLClassLoader
{
    ChildFirstClassLoader(final String name, final URL[] urls, final ClassLoader parent)
    {
        super(name, urls, parent);
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException
    {
        // has the class loaded already?
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass == null)
        {
            try
            {
                // find the class from given jar urls
                loadedClass = findClass(name);
            }
            catch (final ClassNotFoundException ignore)
            {
                // Hmmm... class does not exist in the given urls.
                // Let's try finding it in our parent classloader.
                // this'll throw ClassNotFoundException in failure.
                loadedClass = super.loadClass(name, resolve);
            }
        }

        if (resolve)
        { // marked to resolve
            resolveClass(loadedClass);
        }
        return loadedClass;
    }
}
