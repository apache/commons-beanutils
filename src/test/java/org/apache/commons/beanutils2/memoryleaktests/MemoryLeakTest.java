/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.beanutils2.memoryleaktests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;

import org.apache.commons.beanutils2.BeanUtilsBean;
import org.apache.commons.beanutils2.ConvertUtils;
import org.apache.commons.beanutils2.MappedPropertyDescriptor;
import org.apache.commons.beanutils2.MethodUtils;
import org.apache.commons.beanutils2.PropertyUtils;
import org.apache.commons.beanutils2.WrapDynaBean;
import org.apache.commons.beanutils2.WrapDynaClass;
import org.apache.commons.beanutils2.converters.IntegerConverter;
import org.apache.commons.beanutils2.locale.LocaleBeanUtilsBean;
import org.apache.commons.beanutils2.locale.LocaleConvertUtils;
import org.apache.commons.beanutils2.locale.converters.IntegerLocaleConverter;
import org.junit.jupiter.api.Test;

/**
 * Tests BeanUtils memory leaks.
 *
 * See https://issues.apache.org/jira/browse/BEANUTILS-291
 */
public class MemoryLeakTest {

    /**
     * Creates a new class loader instance.
     */
    private static URLClassLoader newClassLoader() throws MalformedURLException {

        final String dataFilePath = MemoryLeakTest.class.getResource("pojotests").getFile();
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
     * Clears all the BeanUtils Caches manually.
     *
     * This is probably overkill, but since we're dealing with static caches it seems sensible to ensure that all test cases start with a clean sheet.
     */
    private void clearAllBeanUtilsCaches() {

        // Clear BeanUtilsBean's PropertyUtilsBean descriptor caches
        BeanUtilsBean.getInstance().getPropertyUtils().clearDescriptors();

        // Clear LocaleBeanUtilsBean's PropertyUtilsBean descriptor caches
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getPropertyUtils().clearDescriptors();

        // Clear MethodUtils's method cache
        MethodUtils.clearCache();

        // Clear WrapDynaClass cache
        WrapDynaClass.clear();

        // replace the existing BeanUtilsBean instance for the current class loader with a new, clean instance
        BeanUtilsBean.setInstance(new BeanUtilsBean());

        // replace the existing LocaleBeanUtilsBean instance for the current class loader with a new, clean instance
        LocaleBeanUtilsBean.setInstance(new LocaleBeanUtilsBean());
    }

    /**
     * Tries to force the garbage collector to run by filling up memory and calling System.gc().
     */
    private void forceGarbageCollection() throws Exception {
        // Fill up memory
        final SoftReference<Object> ref = new SoftReference<>(new Object());
        int count = 0;
        while (ref.get() != null && count++ < 5) {
            java.util.ArrayList<String> list = new java.util.ArrayList<>();
            try {
                long i = 0;
                while (ref.get() != null) {
                    list.add(
                            "A Big String A Big String A Big String A Big String A Big String A Big String A Big String A Big String A Big String A Big String "
                                    + i++);
                }
                System.out.println("Count(1) " + count + " : " + getMemoryStats());
            } catch (final OutOfMemoryError ignored) {
                // Cannot do anything here
            }
            // Trying to debug Continuum test fail: try calling GC before releasing the memory
            System.gc();
            list.clear();
            list = null;
            System.gc();
            System.out.println("After GC2: " + getMemoryStats() + " Count " + count);
            Thread.sleep(1000);
        }

        final boolean isNotNull = ref.get() != null;
        System.out.println("Count " + count + " " + isNotNull); // debug for Continuum failure
        final String message = "Your JVM is not releasing SoftReference, try running the test with less memory (-Xmx)";
        assumeFalse(isNotNull, message);
    }

    /**
     * Gets the total, free, used memory stats.
     *
     * @return the total, free, used memory stats
     */
    private String getMemoryStats() {
        final java.text.DecimalFormat fmt = new java.text.DecimalFormat("#,##0");
        final Runtime runtime = Runtime.getRuntime();
        final long free = runtime.freeMemory() / 1024;
        final long total = runtime.totalMemory() / 1024;
        final long used = total - free;
        return "MEMORY - Total: " + fmt.format(total) + "k Used: " + fmt.format(used) + "k Free: " + fmt.format(free) + "k";
    }

    /**
     * Produces a profiler report about where the leaks are.
     *
     * This requires JBoss's profiler be installed, see: https://labs.jboss.com/jbossprofiler/
     *
     * @param className The name of the class to profile
     */
    private void profilerLeakReport(final String test, final String className) {
        /*
         * If you want a report about where the leaks are... uncomment this, add jboss-profiler.jvmti.jar and jboss-commons.jar (for org.jboss.loggin). You will
         * then have a report for where the references are. System.out.println(" ----------------" + test + " START ----------------");
         * org.jboss.profiler.jvmti.JVMTIInterface jvmti = new org.jboss.profiler.jvmti.JVMTIInterface();
         * System.out.println(jvmti.exploreClassReferences(className, 8, true, true, true, false, false)); System.out.println(" ----------------" + test +
         * " END ------------------");
         */
    }

    /**
     * Tests that ConvertUtilsBean's converters doesn't cause a memory leak.
     */
    @Test
    void testConvertUtilsBean_converters_memoryLeak() throws Exception {

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        final String className = "org.apache.commons.beanutils2.memoryleaktests.pojotests.CustomInteger";

        // The classLoader will go away only when these following variables are released
        ClassLoader loader = newClassLoader();
        Class<?> beanClass = loader.loadClass(className);
        Object bean = beanClass.newInstance();

        final WeakReference<ClassLoader> someRef = new WeakReference<>(loader);

        // Sanity checks only
        assertNotNull(loader, "ClassLoader is null");
        assertNotNull(beanClass, "BeanClass is null");
        assertNotSame(getClass().getClassLoader(), beanClass.getClassLoader(), "ClassLoaders should be different..");
        assertSame(beanClass.getClassLoader(), loader, "BeanClass ClassLoader incorrect");

        // if you comment the following two lines, the test will work, and the ClassLoader will be released.
        // That proves that nothing is wrong with the test, and ConvertUtilsBean is holding a reference
        ConvertUtils.register(new IntegerConverter(), (Class<Integer>) beanClass);
        assertEquals("12345", ConvertUtils.convert(bean, String.class));

        // this should make the reference go away.
        loader = null;
        beanClass = null;
        bean = null;

        forceGarbageCollection(); /* Try to force the garbage collector to run by filling up memory */

        if (someRef.get() != null) {
            profilerLeakReport("ConvertUtilsBean converters", className);
        }

        // if everything is fine, this will be null
        assertNull(someRef.get(), "ConvertUtilsBean is holding a reference to the classLoader");

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }

    /**
     * Tests that LocaleConvertUtilsBean's converters doesn't cause a memory leak.
     */
    @Test
    void testLocaleConvertUtilsBean_converters_memoryLeak() throws Exception {

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        final String className = "org.apache.commons.beanutils2.memoryleaktests.pojotests.CustomInteger";

        // The classLoader will go away only when these following variables are released
        ClassLoader loader = newClassLoader();
        Class<?> beanClass = loader.loadClass(className);
        Object bean = beanClass.newInstance();

        final WeakReference<ClassLoader> someRef = new WeakReference<>(loader);

        // Sanity checks only
        assertNotNull(loader, "ClassLoader is null");
        assertNotNull(beanClass, "BeanClass is null");
        assertNotSame(getClass().getClassLoader(), beanClass.getClassLoader(), "ClassLoaders should be different..");
        assertSame(beanClass.getClassLoader(), loader, "BeanClass ClassLoader incorrect");

        // if you comment the following two lines, the test will work, and the ClassLoader will be released.
        // That proves that nothing is wrong with the test, and LocaleConvertUtilsBean is holding a reference
        LocaleConvertUtils.register(IntegerLocaleConverter.builder().setLocale(Locale.US).setLocalizedPattern(false).get(), (Class<Integer>) beanClass,
                Locale.US);
        assertEquals(new Integer(12345), LocaleConvertUtils.convert(bean.toString(), Integer.class, Locale.US, "#,###"));

        // this should make the reference go away.
        loader = null;
        beanClass = null;
        bean = null;

        forceGarbageCollection(); /* Try to force the garbage collector to run by filling up memory */

        if (someRef.get() != null) {
            profilerLeakReport("LocaleConvertUtilsBean converters", className);
        }

        // if everything is fine, this will be null
        assertNull(someRef.get(), "LocaleConvertUtilsBean is holding a reference to the classLoader");

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }

    /**
     * Tests that MappedPropertyDescriptor can re-create the Method reference after it has been garbage collected.
     */
    @Test
    void testMappedPropertyDescriptor_MappedMethodReference1() throws Exception {

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        final String className = "org.apache.commons.beanutils2.memoryleaktests.pojotests.SomeMappedPojo";
        final ClassLoader loader = newClassLoader();
        final Class<?> beanClass = loader.loadClass(className);
        final Object bean = beanClass.newInstance();

        // Sanity checks only
        assertNotNull(loader, "ClassLoader is null");
        assertNotNull(beanClass, "BeanClass is null");
        assertNotNull(bean, "Bean is null");
        assertNotSame(getClass().getClassLoader(), beanClass.getClassLoader(), "ClassLoaders should be different..");
        assertSame(beanClass.getClassLoader(), loader, "BeanClass ClassLoader incorrect");

        final MappedPropertyDescriptor descriptor = new MappedPropertyDescriptor("mappedProperty", beanClass);
        assertNotNull(descriptor.getMappedReadMethod(), "1-Read Method null");
        assertNotNull(descriptor.getMappedWriteMethod(), "1-Write Method null");
        assertEquals("getMappedProperty", descriptor.getMappedReadMethod().getName(), "1-Read Method name");
        assertEquals("setMappedProperty", descriptor.getMappedWriteMethod().getName(), "1-Read Write name");

        forceGarbageCollection(); /* Try to force the garbage collector to run by filling up memory */

        // The aim of this test is to check the functinality in MappedPropertyDescriptor which
        // re-creates the Method references after they have been garbage collected. However theres no
        // way of knowing the method references were garbage collected and that code was run, except by
        // un-commeting the System.out statement in MappedPropertyDescriptor's MappedMethodReference's
        // get() method.

        assertNotNull(descriptor.getMappedReadMethod(), "1-Read Method null");
        assertNotNull(descriptor.getMappedWriteMethod(), "1-Write Method null");
        assertEquals("getMappedProperty", descriptor.getMappedReadMethod().getName(), "1-Read Method name");
        assertEquals("setMappedProperty", descriptor.getMappedWriteMethod().getName(), "1-Read Write name");

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }

    /**
     * Tests that MappedPropertyDescriptor can re-create the Method reference after it has been garbage collected.
     */
    @Test
    void testMappedPropertyDescriptor_MappedMethodReference2() throws Exception {

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        final String className = "org.apache.commons.beanutils2.memoryleaktests.pojotests.SomeMappedPojo";
        ClassLoader loader = newClassLoader();
        Class<?> beanClass = loader.loadClass(className);
        Object bean = beanClass.newInstance();

        // Sanity checks only
        assertNotNull(loader, "ClassLoader is null");
        assertNotNull(beanClass, "BeanClass is null");
        assertNotNull(bean, "Bean is null");
        assertNotSame(getClass().getClassLoader(), beanClass.getClassLoader(), "ClassLoaders should be different..");
        assertSame(beanClass.getClassLoader(), loader, "BeanClass ClassLoader incorrect");

        final MappedPropertyDescriptor descriptor = new MappedPropertyDescriptor("mappedProperty", beanClass);
        assertNotNull(descriptor.getMappedReadMethod(), "1-Read Method null");
        assertNotNull(descriptor.getMappedWriteMethod(), "1-Write Method null");
        assertEquals("getMappedProperty", descriptor.getMappedReadMethod().getName(), "1-Read Method name");
        assertEquals("setMappedProperty", descriptor.getMappedWriteMethod().getName(), "1-Read Write name");

        // this should make the reference go away.
        loader = null;
        beanClass = null;
        bean = null;

        forceGarbageCollection(); /* Try to force the garbage collector to run by filling up memory */

        // The aim of this test is to check the functinality in MappedPropertyDescriptor which
        // re-creates the Method references after they have been garbage collected. However theres no
        // way of knowing the method references were garbage collected and that code was run, except by
        // un-commeting the System.out statement in MappedPropertyDescriptor's MappedMethodReference's
        // get() method.

        assertNotNull(descriptor.getMappedReadMethod(), "1-Read Method null");
        assertNotNull(descriptor.getMappedWriteMethod(), "1-Write Method null");
        assertEquals("getMappedProperty", descriptor.getMappedReadMethod().getName(), "1-Read Method name");
        assertEquals("setMappedProperty", descriptor.getMappedWriteMethod().getName(), "1-Read Write name");

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }

    /**
     * Tests that MethodUtils's cache doesn't cause a memory leak.
     */
    @Test
    void testMethodUtils_cache_memoryLeak() throws Exception {

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        final String className = "org.apache.commons.beanutils2.memoryleaktests.pojotests.SomePojo";

        // The classLoader will go away only when these following variables are released
        ClassLoader loader = newClassLoader();
        Class<?> beanClass = loader.loadClass(className);
        Object bean = beanClass.newInstance();

        final WeakReference<ClassLoader> someRef = new WeakReference<>(loader);

        // Sanity checks only
        assertNotNull(loader, "ClassLoader is null");
        assertNotNull(beanClass, "BeanClass is null");
        assertNotSame(getClass().getClassLoader(), beanClass.getClassLoader(), "ClassLoaders should be different..");
        assertSame(beanClass.getClassLoader(), loader, "BeanClass ClassLoader incorrect");

        // if you comment the following line, the test will work, and the ClassLoader will be released.
        // That proves that nothing is wrong with the test, and MethodUtils is holding a reference
        assertNotNull(MethodUtils.getAccessibleMethod(bean.getClass(), "getName", new Class[0]));

        // this should make the reference go away.
        loader = null;
        beanClass = null;
        bean = null;

        forceGarbageCollection(); /* Try to force the garbage collector to run by filling up memory */

        if (someRef.get() != null) {
            profilerLeakReport("MethodUtils cache", className);
        }

        // if everything is fine, this will be null
        assertNull(someRef.get(), "MethodUtils is holding a reference to the classLoader");

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }

    /**
     * Tests that PropertyUtilsBean's descriptorsCache doesn't cause a memory leak.
     */
    @Test
    void testPropertyUtilsBean_descriptorsCache_memoryLeak() throws Exception {

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        final String className = "org.apache.commons.beanutils2.memoryleaktests.pojotests.SomePojo";

        // The classLoader will go away only when these following variables are released
        ClassLoader loader = newClassLoader();
        Class<?> beanClass = loader.loadClass(className);
        Object bean = beanClass.newInstance();

        final WeakReference<ClassLoader> someRef = new WeakReference<>(loader);

        // Sanity checks only
        assertNotNull(loader, "ClassLoader is null");
        assertNotNull(beanClass, "BeanClass is null");
        assertNotSame(getClass().getClassLoader(), beanClass.getClassLoader(), "ClassLoaders should be different..");
        assertSame(beanClass.getClassLoader(), loader, "BeanClass ClassLoader incorrect");

        // if you comment the following line, the test will work, and the ClassLoader will be released.
        // That proves that nothing is wrong with the test, and PropertyUtils is holding a reference
        assertEquals("initialValue", PropertyUtils.getProperty(bean, "name"));

        // this should make the reference go away.
        loader = null;
        beanClass = null;
        bean = null;

        forceGarbageCollection(); /* Try to force the garbage collector to run by filling up memory */

        if (someRef.get() != null) {
            profilerLeakReport("PropertyUtilsBean descriptorsCache", className);
        }

        // if everything is fine, this will be null
        assertNull(someRef.get(), "PropertyUtilsBean is holding a reference to the classLoader");

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }

    /**
     * Tests that PropertyUtilsBean's mappedDescriptorsCache doesn't cause a memory leak.
     */
    @Test
    void testPropertyUtilsBean_mappedDescriptorsCache_memoryLeak() throws Exception {

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        final String className = "org.apache.commons.beanutils2.memoryleaktests.pojotests.SomeMappedPojo";

        // The classLoader will go away only when these following variables are released
        ClassLoader loader = newClassLoader();
        Class<?> beanClass = loader.loadClass(className);
        Object bean = beanClass.newInstance();

        final WeakReference<ClassLoader> someRef = new WeakReference<>(loader);

        // Sanity checks only
        assertNotNull(loader, "ClassLoader is null");
        assertNotNull(beanClass, "BeanClass is null");
        assertNotSame(getClass().getClassLoader(), beanClass.getClassLoader(), "ClassLoaders should be different..");
        assertSame(beanClass.getClassLoader(), loader, "BeanClass ClassLoader incorrect");

        // if you comment the following three lines, the test will work, and the ClassLoader will be released.
        // That proves that nothing is wrong with the test, and PropertyUtils is holding a reference
        assertEquals("Second Value", PropertyUtils.getProperty(bean, "mappedProperty(Second Key)"));
        PropertyUtils.setProperty(bean, "mappedProperty(Second Key)", "New Second Value");
        assertEquals("New Second Value", PropertyUtils.getProperty(bean, "mappedProperty(Second Key)"));

        // this should make the reference go away.
        loader = null;
        beanClass = null;
        bean = null;

        // PropertyUtilsBean uses the MethodUtils's method cache for mapped properties.
        // Uncomment the following line to check this is not just a repeat of that memory leak.
        // MethodUtils.clearCache();

        forceGarbageCollection(); /* Try to force the garbage collector to run by filling up memory */

        if (someRef.get() != null) {
            profilerLeakReport("PropertyUtilsBean mappedDescriptorsCache", className);
        }

        // if everything is fine, this will be null
        assertNull(someRef.get(), "PropertyUtilsBean is holding a reference to the classLoader");

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }

    /**
     * Tests that WrapDynaClass's dynaClasses doesn't cause a memory leak.
     */
    @Test
    void testWrapDynaClass_dynaClasses_memoryLeak() throws Exception {

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        final String className = "org.apache.commons.beanutils2.memoryleaktests.pojotests.SomePojo";

        // The classLoader will go away only when these following variables are released
        ClassLoader loader = newClassLoader();
        Class<?> beanClass = loader.loadClass(className);
        Object bean = beanClass.newInstance();
        WrapDynaBean wrapDynaBean = new WrapDynaBean(bean);

        final WeakReference<ClassLoader> someRef = new WeakReference<>(loader);

        // Sanity checks only
        assertNotNull(loader, "ClassLoader is null");
        assertNotNull(beanClass, "BeanClass is null");
        assertNotSame(getClass().getClassLoader(), beanClass.getClassLoader(), "ClassLoaders should be different..");
        assertSame(beanClass.getClassLoader(), loader, "BeanClass ClassLoader incorrect");

        // if you comment the following line, the test will work, and the ClassLoader will be released.
        // That proves that nothing is wrong with the test, and WrapDynaClass is holding a reference
        assertEquals("initialValue", wrapDynaBean.get("name"));

        // this should make the reference go away.
        loader = null;
        beanClass = null;
        bean = null;
        wrapDynaBean = null;

        // Wrap Dyna Class uses the PropertyUtilsBean's decriptor caches.
        // Uncomment the following line to check this is not just a repeat of that memory leak.
        // BeanUtilsBean.getInstance().getPropertyUtils().clearDescriptors();

        forceGarbageCollection(); /* Try to force the garbage collector to run by filling up memory */

        if (someRef.get() != null) {
            profilerLeakReport("WrapDynaClass dynaClasses", className);
        }

        // if everything is fine, this will be null
        assertNull(someRef.get(), "WrapDynaClass is holding a reference to the classLoader");

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }
}
