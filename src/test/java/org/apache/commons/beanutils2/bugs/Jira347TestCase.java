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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.beans.IntrospectionException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Objects;

import org.apache.commons.beanutils2.MappedPropertyDescriptor;
import org.apache.commons.beanutils2.memoryleaktests.MemoryLeakTestCase;
import org.junit.jupiter.api.Test;

/**
 * Test case for Jira issue# BEANUTILS-347.
 *
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-347">https://issues.apache.org/jira/browse/BEANUTILS-347</a>
 */
public class Jira347TestCase {

    /**
     * Create a new class loader instance.
     */
    private static URLClassLoader newClassLoader() throws MalformedURLException {

        final String dataFilePath = MemoryLeakTestCase.class.getResource("pojotests").getFile();
        // System.out.println("dataFilePath: " + dataFilePath);
        final String location = "file://"
                + dataFilePath.substring(0, dataFilePath.length() - "org.apache.commons.beanutils2.memoryleaktests.pojotests".length());
        // System.out.println("location: " + location);

        final StringBuilder newString = new StringBuilder();
        for (int i = 0; i < location.length(); i++) {
            if (location.charAt(i) == '\\') {
                newString.append("/");
            } else {
                newString.append(location.charAt(i));
            }
        }
        final String classLocation = newString.toString();
        // System.out.println("classlocation: " + classLocation);

        final URLClassLoader theLoader = URLClassLoader.newInstance(new URL[] { new URL(classLocation) }, null);
        return theLoader;
    }

    /**
     * Try to force the garbage collector to run by filling up memory and calling System.gc().
     */
    private void forceGarbageCollection() throws Exception {
        // Fill up memory
        final SoftReference<Object> ref = new SoftReference<>(new Object());
        int count = 0;
        while (ref.get() != null && count++ < 5) {
            ArrayList<Object> list = new ArrayList<>();
            try {
                long i = 0;
                while (ref.get() != null) {
                    list.add(
                            "A Big String A Big String A Big String A Big String A Big String A Big String A Big String A Big String A Big String A Big String "
                                    + i++);
                }
            } catch (final Throwable ignored) {
            }
            list.clear();
            list = null;
            // System.out.println("Count " + count + " : " + getMemoryStats());
            System.gc();
            Thread.sleep(1000);
        }
        // System.out.println("After GC: " + getMemoryStats());

        if (ref.get() != null) {
            throw new IllegalStateException("Your JVM is not releasing SoftReference, try running the test with less memory (-Xmx)");
        }
    }

    /**
     * Retrieves the string representation of the mapped write method for the given descriptor. This conversion is needed as there must not be strong reference
     * to the Method object outside of this method as otherwise the garbage collector will not clean up the soft reference within the MappedPropertyDescriptor.
     *
     * @return the string representation or null if mapped write method does not exist
     */
    private String getMappedWriteMethod(final MappedPropertyDescriptor descriptor) {
        final Method m = descriptor.getMappedWriteMethod();
        return Objects.toString(m, null);
    }

    /**
     * Tests that MappedPropertyDescriptor does not throw an exception while re-creating a Method reference after it has been garbage collected under the
     * following circumstances. - a class has a property 'mappedProperty' - there is no getter for this property - there is method
     * setMappedProperty(MappedPropertyTestBean,MappedPropertyTestBean)
     *
     * In this case getMappedWriteMethod should not throw an exception after the method reference has been garbage collected. It is essential that in both cases
     * the same method is returned or in both cases null. If the constructor of the MappedPropertyDescriptor would recognize the situation (of the first param
     * not being of type String) this would be fine as well.
     */
    @Test
    public void testMappedPropertyDescriptor_AnyArgsProperty() throws Exception {
        final String className = "org.apache.commons.beanutils2.MappedPropertyTestBean";
        try (final URLClassLoader loader = newClassLoader()) {
            final Class<?> beanClass = loader.loadClass(className);
            beanClass.newInstance();

            // Sanity checks only
            assertNotNull(loader, "ClassLoader is null");
            assertNotNull(beanClass, "BeanClass is null");
            assertNotSame(getClass().getClassLoader(), beanClass.getClassLoader(),
                                     "ClassLoaders should be different..");
            assertSame(beanClass.getClassLoader(), loader, "BeanClass ClassLoader incorrect");

            // now start the test
            MappedPropertyDescriptor descriptor = null;
            try {
                descriptor = new MappedPropertyDescriptor("anyMapped", beanClass);
            } catch (final IntrospectionException e) {
                // this would be fine as well
            }

            if (descriptor != null) {
                final String m1 = getMappedWriteMethod(descriptor);
                forceGarbageCollection();
                try {
                    final String m2 = getMappedWriteMethod(descriptor);
                    assertEquals(m1, m2,
                                            "Method returned post garbage collection differs from Method returned prior to gc");
                } catch (final RuntimeException e) {
                    fail("getMappedWriteMethod threw an exception after garbage collection " + e);
                }
            }
        }
    }
}
