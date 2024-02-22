package com.dpline.flink.core;

import java.net.URL;


import java.io.IOException;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * A variant of the URLClassLoader that first loads from the URLs and only after that from the
 * parent.
 * <p>
 * override that.
 */
public final class ChildFirstClassLoader extends URLClassLoader {

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public static final Consumer<Throwable> NOOP_EXCEPTION_HANDLER = classLoadingException -> {};

    private static Pattern FLINK_PATTERN = Pattern.compile(
        "flink-(.*).jar",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private static final String JAR_PROTOCOL = "jar";


    private static final List<String> PARENT_FIRST_PATTERNS = Arrays.asList(
        "java.",
        "javax.xml",
        "org.slf4j",
        "org.apache.log4j",
        "org.apache.logging",
        "org.apache.commons.logging",
        "ch.qos.logback",
        "org.xml",
        "org.w3c",
        "org.apache.hadoop"
    );

    private final Consumer<Throwable> classLoadingExceptionHandler;

    public ChildFirstClassLoader(URL[] urls, ClassLoader parent) {
        this(urls, parent, NOOP_EXCEPTION_HANDLER);
    }

    public ChildFirstClassLoader(
        URL[] urls,
        ClassLoader parent,
        Consumer<Throwable> classLoadingExceptionHandler) {
        super(urls, parent);
        this.classLoadingExceptionHandler = classLoadingExceptionHandler;
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            synchronized (getClassLoadingLock(name)) {
                return this.loadClassWithoutExceptionHandling(name, resolve);
            }
        } catch (Throwable classLoadingException) {
            classLoadingExceptionHandler.accept(classLoadingException);
            throw classLoadingException;
        }
    }

    @Override
    public URL getResource(String name) {
        // first, try and find it via the URLClassloader
        URL urlClassLoaderResource = findResource(name);

        if (urlClassLoaderResource != null) {
            return urlClassLoaderResource;
        }
        // delegate to super
        return super.getResource(name);
    }

    private List<URL> addResources(List<URL> result, Enumeration<URL> resources) {
        while (resources.hasMoreElements()) {
            URL urlClassLoaderResource = resources.nextElement();
            if (urlClassLoaderResource != null) {
                result.add(urlClassLoaderResource);
            }
        }
        return result;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        // first get resources from URLClassloader
        final List<URL> result = addResources(new ArrayList<>(), findResources(name));

        ClassLoader parent = getParent();

        if (parent != null) {
            // get parent urls
            addResources(result, parent.getResources(name));
        }

        return new Enumeration<URL>() {
            final Iterator<URL> iter = result.iterator();

            @Override
            public boolean hasMoreElements() {
                return iter.hasNext();
            }

            @Override
            public URL nextElement() {
                return iter.next();
            }
        };
    }

    private Class<?> loadClassWithoutExceptionHandling(String name, boolean resolve)
        throws ClassNotFoundException {

        // First, check if the class has already been loaded
        Class<?> c = super.findLoadedClass(name);
        if (c == null) {
            // check whether the class should go parent-first
            for (String parentFirstPattern : PARENT_FIRST_PATTERNS) {
                if (name.startsWith(parentFirstPattern)) {
                    return super.loadClass(name, resolve);
                }
            }

            try {
                // check the URLs
                c = findClass(name);
            } catch (ClassNotFoundException e) {
                // let URLClassLoader do it, which will eventually call the parent
                c = super.loadClass(name, resolve);
            }
        } else if (resolve) {
            resolveClass(c);
        }
        return c;
    }

    public void addURL(URL url) {
        super.addURL(url);
    }
}
