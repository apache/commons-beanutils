/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.beanutils2.bugs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.beanutils2.LoggerUtil;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@code LoggerUtil}
 * https://issues.apache.org/jira/browse/BEANUTILS-566
 */
public class Jira566TestCase {

    private static CustomURLClassLoader CUSTOM_CLASSLOADER;

    private static Boolean IS_CUSTOM_CLASSLOADER_INVOKED = false;

    private static class CustomURLClassLoader extends URLClassLoader {

        public CustomURLClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        /*
         * Given the following default loadClass implementation steps (in order, per the JavaDoc):
         * 1)  Invoke findLoadedClass(String) to check if the class has already been loaded.
         * 2)  Invoke the loadClass method on the parent class loader. If the parent is null the class loader built-in to the virtual machine is used, instead.
         * 3)  Invoke the findClass(String) method to find the class.
         *
         * If the name matches Jira566TestCase, then we find that class with this classloader instead via findClass.
         * Calling newInstance().getClass().getClassLoader() on the returned class object would an instance of CustomURLClassLoader.
         * Otherwise, if findLoadedClass or loadClass are used instead, then the original classloader is used (i.e jdk.internal.loader.ClassLoaders$AppClassLoader)
         *
         */
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.equals("org.apache.commons.beanutils2.bugs.Jira566TestCase")) {
                // System.out.println("match: " + name); // debug
                return findClass(name);
            } else {
                // System.out.println("super: " + name); // debug
                IS_CUSTOM_CLASSLOADER_INVOKED = true;
                return super.loadClass(name);
            }
        }

    }


    public static void setup() throws Exception {
        Path targetPath = Paths.get(Jira566TestCase.class.getResource("/").toURI()).getParent();
        ClassLoader parent = Jira566TestCase.class.getClassLoader();
        CUSTOM_CLASSLOADER = new CustomURLClassLoader(new URL[] {new File(targetPath.toString(), "test-classes").toURI().toURL()}, parent);
    }

    /*
     * Tests that context classloader is used
     */
    @Test
    public void testContextClassLoaderUsed() throws Exception {

        setup();

        /*
         * Create an instance of this class (Jira566TestCase) so that it's classloader is CustomURLClassLoader
         */
        Class<?> loadedClass = CUSTOM_CLASSLOADER.loadClass("org.apache.commons.beanutils2.bugs.Jira566TestCase");
        Class<?> instance = loadedClass.newInstance().getClass();

        IS_CUSTOM_CLASSLOADER_INVOKED = false; // reset to false

        /*
         * When the logger is created, the creation will go through CustomURLClassLoader#loadClass
         */
        LoggerUtil.createLoggerWithContextClassLoader(instance);

        assertTrue("Context ClassLoader was not invoked", IS_CUSTOM_CLASSLOADER_INVOKED);
    }

    /*
     * Tests that original classloader is used
     */
    @Test
    public void testOriginalClassLoaderUsed() throws Exception {

        setup();

        IS_CUSTOM_CLASSLOADER_INVOKED = false; // reset to false
        LogFactory.getLog(Jira566TestCase.class);

        assertFalse("Context ClassLoader was invoked when it should not have been", IS_CUSTOM_CLASSLOADER_INVOKED);
    }

    /*
     * The LoggerUtil.createLoggerWithContextClassLoader method swaps out the classloaders,
     * so this test checks to make it the original is set back.
     */
    @Test
    public void testClassLoaderIsSetBackToOriginalInLoggerUtil() throws Exception {

        ClassLoader original = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(CUSTOM_CLASSLOADER);
            LoggerUtil.createLoggerWithContextClassLoader(Jira566TestCase.class);

            assertEquals("Classloader do not match!",Thread.currentThread().getContextClassLoader(), CUSTOM_CLASSLOADER);
        } finally {
            // set it back to avoid any problems in the other tests
            Thread.currentThread().setContextClassLoader(original);
        }

    }
}
