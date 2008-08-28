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
package org.apache.commons.beanutils.memoryleaktests;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.beanutils.WrapDynaClass;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.locale.LocaleBeanUtilsBean;
import org.apache.commons.beanutils.locale.LocaleConvertUtils;
import org.apache.commons.beanutils.locale.converters.IntegerLocaleConverter;

/**
 * Test BeanUtils memory leaks.
 * 
 * See https://issues.apache.org/jira/browse/BEANUTILS-291
 * 
 * @author Clebert Suconic
 */
public class MemoryLeakTestCase extends TestCase {

    /**
     * Tests that PropertyUtilsBean's descriptorsCache doesn't cause a memory leak.
     */
    public void testPropertyUtilsBean_descriptorsCache_memoryLeak() throws Exception {
        if (isPre15JVM()) {
            return;
        }

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        String className = "org.apache.commons.beanutils.memoryleaktests.pojotests.SomePojo";

        // The classLoader will go away only when these following variables are released
        ClassLoader loader = newClassLoader();
        Class beanClass    = loader.loadClass(className);
        Object bean        = beanClass.newInstance();
        // -----------------------------------------------------------------------------

        WeakReference someRef = new WeakReference(loader);

        // Sanity checks only
        assertNotNull("ClassLoader is null", loader);
        assertNotNull("BeanClass is null", beanClass);
        assertNotSame("ClassLoaders should be different..", getClass().getClassLoader(), beanClass.getClassLoader());
        assertSame("BeanClass ClassLoader incorrect", beanClass.getClassLoader(), loader);

        // if you comment the following line, the testcase will work, and the ClassLoader will be released.
        // That proves that nothing is wrong with the test, and PropertyUtils is holding a reference
        assertEquals("initialValue", PropertyUtils.getProperty(bean, "name"));

        // this should make the reference go away.
        loader    = null;
        beanClass = null;
        bean      = null;

        forceGarbageCollection(); /* Try to force the garbage collector to run by filling up memory */

        if (someRef.get() != null) {
            profilerLeakReport("PropertyUtilsBean descriptorsCache", className);
        }

        // if everything is fine, this will be null
        assertNull("PropertyUtilsBean is holding a reference to the classLoader", someRef.get());

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }

    /**
     * Tests that PropertyUtilsBean's mappedDescriptorsCache doesn't cause a memory leak.
     */
    public void testPropertyUtilsBean_mappedDescriptorsCache_memoryLeak() throws Exception {
        if (isPre15JVM()) {
            return;
        }

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        String className = "org.apache.commons.beanutils.memoryleaktests.pojotests.SomeMappedPojo";

        // The classLoader will go away only when these following variables are released
        ClassLoader loader = newClassLoader();
        Class beanClass    = loader.loadClass(className);
        Object bean        = beanClass.newInstance();
        // -----------------------------------------------------------------------------

        WeakReference someRef = new WeakReference(loader);

        // Sanity checks only
        assertNotNull("ClassLoader is null", loader);
        assertNotNull("BeanClass is null", beanClass);
        assertNotSame("ClassLoaders should be different..", getClass().getClassLoader(), beanClass.getClassLoader());
        assertSame("BeanClass ClassLoader incorrect", beanClass.getClassLoader(), loader);

        // if you comment the following three lines, the testcase will work, and the ClassLoader will be released.
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
        assertNull("PropertyUtilsBean is holding a reference to the classLoader", someRef.get());

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }

    /**
     * Tests that MappedPropertyDescriptor can re-create the Method reference after it
     * has been garbage collected.
     */
    public void testMappedPropertyDescriptor_MappedMethodReference1() throws Exception {

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        String className = "org.apache.commons.beanutils.memoryleaktests.pojotests.SomeMappedPojo";
        ClassLoader loader = newClassLoader();
        Class beanClass    = loader.loadClass(className);
        Object bean        = beanClass.newInstance();
        // -----------------------------------------------------------------------------

        // Sanity checks only
        assertNotNull("ClassLoader is null", loader);
        assertNotNull("BeanClass is null", beanClass);
        assertNotSame("ClassLoaders should be different..", getClass().getClassLoader(), beanClass.getClassLoader());
        assertSame("BeanClass ClassLoader incorrect", beanClass.getClassLoader(), loader);

        MappedPropertyDescriptor descriptor = new MappedPropertyDescriptor("mappedProperty", beanClass);
        assertNotNull("1-Read Method null", descriptor.getMappedReadMethod());
        assertNotNull("1-Write Method null", descriptor.getMappedWriteMethod());
        assertEquals("1-Read Method name", "getMappedProperty", descriptor.getMappedReadMethod().getName());
        assertEquals("1-Read Write name", "setMappedProperty", descriptor.getMappedWriteMethod().getName());

        forceGarbageCollection(); /* Try to force the garbage collector to run by filling up memory */

        // The aim of this test is to check the functinality in MappedPropertyDescriptor which
        // re-creates the Method references after they have been garbage collected. However theres no
        // way of knowing the method references were garbage collected and that code was run, except by
        // un-commeting the System.out statement in MappedPropertyDescriptor's MappedMethodReference's
        // get() method.

        assertNotNull("1-Read Method null", descriptor.getMappedReadMethod());
        assertNotNull("1-Write Method null", descriptor.getMappedWriteMethod());
        assertEquals("1-Read Method name", "getMappedProperty", descriptor.getMappedReadMethod().getName());
        assertEquals("1-Read Write name", "setMappedProperty", descriptor.getMappedWriteMethod().getName());

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }

    /**
     * Tests that MappedPropertyDescriptor can re-create the Method reference after it
     * has been garbage collected.
     */
    public void testMappedPropertyDescriptor_MappedMethodReference2() throws Exception {

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        String className = "org.apache.commons.beanutils.memoryleaktests.pojotests.SomeMappedPojo";
        ClassLoader loader = newClassLoader();
        Class beanClass    = loader.loadClass(className);
        Object bean        = beanClass.newInstance();
        // -----------------------------------------------------------------------------

        // Sanity checks only
        assertNotNull("ClassLoader is null", loader);
        assertNotNull("BeanClass is null", beanClass);
        assertNotSame("ClassLoaders should be different..", getClass().getClassLoader(), beanClass.getClassLoader());
        assertSame("BeanClass ClassLoader incorrect", beanClass.getClassLoader(), loader);

        MappedPropertyDescriptor descriptor = new MappedPropertyDescriptor("mappedProperty", beanClass);
        assertNotNull("1-Read Method null", descriptor.getMappedReadMethod());
        assertNotNull("1-Write Method null", descriptor.getMappedWriteMethod());
        assertEquals("1-Read Method name", "getMappedProperty", descriptor.getMappedReadMethod().getName());
        assertEquals("1-Read Write name", "setMappedProperty", descriptor.getMappedWriteMethod().getName());

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

        assertNotNull("1-Read Method null", descriptor.getMappedReadMethod());
        assertNotNull("1-Write Method null", descriptor.getMappedWriteMethod());
        assertEquals("1-Read Method name", "getMappedProperty", descriptor.getMappedReadMethod().getName());
        assertEquals("1-Read Write name", "setMappedProperty", descriptor.getMappedWriteMethod().getName());

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }

    /**
     * Tests that MethodUtils's cache doesn't cause a memory leak.
     */
    public void testMethodUtils_cache_memoryLeak() throws Exception {

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        String className = "org.apache.commons.beanutils.memoryleaktests.pojotests.SomePojo";

        // The classLoader will go away only when these following variables are released
        ClassLoader loader = newClassLoader();
        Class beanClass    = loader.loadClass(className);
        Object bean        = beanClass.newInstance();
        // -----------------------------------------------------------------------------

        WeakReference someRef = new WeakReference(loader);

        // Sanity checks only
        assertNotNull("ClassLoader is null", loader);
        assertNotNull("BeanClass is null", beanClass);
        assertNotSame("ClassLoaders should be different..", getClass().getClassLoader(), beanClass.getClassLoader());
        assertSame("BeanClass ClassLoader incorrect", beanClass.getClassLoader(), loader);

        // if you comment the following line, the testcase will work, and the ClassLoader will be released.
        // That proves that nothing is wrong with the test, and MethodUtils is holding a reference
        assertEquals("initialValue", MethodUtils.invokeExactMethod(bean, "getName", new Object[0]));

        // this should make the reference go away.
        loader    = null;
        beanClass = null;
        bean      = null;

        forceGarbageCollection(); /* Try to force the garbage collector to run by filling up memory */

        if (someRef.get() != null) {
            profilerLeakReport("MethodUtils cache", className);
        }

        // if everything is fine, this will be null
        assertNull("MethodUtils is holding a reference to the classLoader", someRef.get());

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }

    /**
     * Tests that WrapDynaClass's dynaClasses doesn't cause a memory leak.
     */
    public void testWrapDynaClass_dynaClasses_memoryLeak() throws Exception {
        if (isPre15JVM()) {
            return;
        }

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        String className = "org.apache.commons.beanutils.memoryleaktests.pojotests.SomePojo";

        // The classLoader will go away only when these following variables are released
        ClassLoader loader = newClassLoader();
        Class beanClass    = loader.loadClass(className);
        Object bean        = beanClass.newInstance();
        WrapDynaBean wrapDynaBean = new WrapDynaBean(bean);
        // -----------------------------------------------------------------------------

        WeakReference someRef = new WeakReference(loader);

        // Sanity checks only
        assertNotNull("ClassLoader is null", loader);
        assertNotNull("BeanClass is null", beanClass);
        assertNotSame("ClassLoaders should be different..", getClass().getClassLoader(), beanClass.getClassLoader());
        assertSame("BeanClass ClassLoader incorrect", beanClass.getClassLoader(), loader);

        // if you comment the following line, the testcase will work, and the ClassLoader will be released.
        // That proves that nothing is wrong with the test, and WrapDynaClass is holding a reference
        assertEquals("initialValue", wrapDynaBean.get("name"));

        // this should make the reference go away.
        loader       = null;
        beanClass    = null;
        bean         = null;
        wrapDynaBean = null;

        // Wrap Dyna Class uses the PropertyUtilsBean's decriptor caches.
        // Uncomment the following line to check this is not just a repeat of that memory leak.
        // BeanUtilsBean.getInstance().getPropertyUtils().clearDescriptors();

        forceGarbageCollection(); /* Try to force the garbage collector to run by filling up memory */

        if (someRef.get() != null) {
            profilerLeakReport("WrapDynaClass dynaClasses", className);
        }

        // if everything is fine, this will be null
        assertNull("WrapDynaClass is holding a reference to the classLoader", someRef.get());

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }

    /**
     * Tests that ConvertUtilsBean's converters doesn't cause a memory leak.
     */
    public void testConvertUtilsBean_converters_memoryLeak() throws Exception {

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        String className = "org.apache.commons.beanutils.memoryleaktests.pojotests.CustomInteger";

        // The classLoader will go away only when these following variables are released
        ClassLoader loader = newClassLoader();
        Class beanClass    = loader.loadClass(className);
        Object bean        = beanClass.newInstance();
        // -----------------------------------------------------------------------------

        WeakReference someRef = new WeakReference(loader);

        // Sanity checks only
        assertNotNull("ClassLoader is null", loader);
        assertNotNull("BeanClass is null", beanClass);
        assertNotSame("ClassLoaders should be different..", getClass().getClassLoader(), beanClass.getClassLoader());
        assertSame("BeanClass ClassLoader incorrect", beanClass.getClassLoader(), loader);

        // if you comment the following two lines, the testcase will work, and the ClassLoader will be released.
        // That proves that nothing is wrong with the test, and ConvertUtilsBean is holding a reference
        ConvertUtils.register(new IntegerConverter(), beanClass);
        assertEquals("12345", ConvertUtils.convert(bean, String.class));

        // this should make the reference go away.
        loader    = null;
        beanClass = null;
        bean      = null;

        forceGarbageCollection(); /* Try to force the garbage collector to run by filling up memory */

        if (someRef.get() != null) {
            profilerLeakReport("ConvertUtilsBean converters", className);
        }

        // if everything is fine, this will be null
        assertNull("ConvertUtilsBean is holding a reference to the classLoader", someRef.get());

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }

    /**
     * Tests that LocaleConvertUtilsBean's converters doesn't cause a memory leak.
     */
    public void testLocaleConvertUtilsBean_converters_memoryLeak() throws Exception {

        // Clear All BeanUtils caches before the test
        clearAllBeanUtilsCaches();

        String className = "org.apache.commons.beanutils.memoryleaktests.pojotests.CustomInteger";

        // The classLoader will go away only when these following variables are released
        ClassLoader loader = newClassLoader();
        Class beanClass    = loader.loadClass(className);
        Object bean        = beanClass.newInstance();
        // -----------------------------------------------------------------------------

        WeakReference someRef = new WeakReference(loader);

        // Sanity checks only
        assertNotNull("ClassLoader is null", loader);
        assertNotNull("BeanClass is null", beanClass);
        assertNotSame("ClassLoaders should be different..", getClass().getClassLoader(), beanClass.getClassLoader());
        assertSame("BeanClass ClassLoader incorrect", beanClass.getClassLoader(), loader);

        // if you comment the following two lines, the testcase will work, and the ClassLoader will be released.
        // That proves that nothing is wrong with the test, and LocaleConvertUtilsBean is holding a reference
        LocaleConvertUtils.register(new IntegerLocaleConverter(Locale.US, false), beanClass, Locale.US);
        assertEquals(new Integer(12345), LocaleConvertUtils.convert(bean.toString(), beanClass, Locale.US, "#,###"));

        // this should make the reference go away.
        loader    = null;
        beanClass = null;
        bean      = null;

        forceGarbageCollection(); /* Try to force the garbage collector to run by filling up memory */

        if (someRef.get() != null) {
            profilerLeakReport("LocaleConvertUtilsBean converters", className);
        }

        // if everything is fine, this will be null
        assertNull("LocaleConvertUtilsBean is holding a reference to the classLoader", someRef.get());

        // Clear All BeanUtils caches after the test
        clearAllBeanUtilsCaches();
    }

    /**
     * Clear all the BeanUtils Caches manually.
     *
     * This is probably overkill, but since we're dealing with static caches
     * it seems sensible to ensure that all test cases start with a clean sheet.
     */
    private void clearAllBeanUtilsCaches() {

        // Clear BeanUtilsBean's PropertyUtilsBean descriptor caches
        BeanUtilsBean.getInstance().getPropertyUtils().clearDescriptors();

        // Clear LocaleBeanUtilsBean's PropertyUtilsBean descriptor caches
        LocaleBeanUtilsBean.getInstance().getPropertyUtils().clearDescriptors();

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
     * Try to force the garbage collector to run by filling up memory and calling System.gc().
     */
    private void forceGarbageCollection() throws Exception {
        // Fill up memory
        SoftReference ref = new SoftReference(new Object());
        int count = 0;
        while(ref.get() != null && count++ < 5) {
            java.util.ArrayList list = new java.util.ArrayList();
            try {
                long i = 0;
                while (true && ref.get() != null) {
                    list.add("A Big String A Big String A Big String A Big String A Big String A Big String A Big String A Big String A Big String A Big String " + (i++));
                }
            } catch (Throwable ignored) {
            }
            list.clear();
            list = null;
            // System.out.println("Count " + count + " : " + getMemoryStats());
            System.gc(); 
            Thread.sleep(1000);
        }
        // System.out.println("After GC: " + getMemoryStats());
        
        if (ref.get() != null) {
            throw new IllegalStateException("Your JVM is not releasing SoftReference, try running the testcase with less memory (-Xmx)");
        }
    }

    /**
     * Create a new class loader instance.
     */
    private static URLClassLoader newClassLoader() throws MalformedURLException {

        String dataFilePath = MemoryLeakTestCase.class.getResource("pojotests").getFile();
        //System.out.println("dataFilePath: " + dataFilePath);
        String location = "file://" + dataFilePath.substring(0,dataFilePath.length()-"org.apache.commons.beanutils.memoryleaktests.pojotests".length());
        //System.out.println("location: " + location);

        StringBuffer newString = new StringBuffer();
        for (int i=0;i<location.length();i++) {
            if (location.charAt(i)=='\\') {
                newString.append("/");
            } else {
                newString.append(location.charAt(i));
            }
        }
        String classLocation = newString.toString();
        //System.out.println("classlocation: " + classLocation);

        URLClassLoader theLoader = URLClassLoader.newInstance(new URL[]{new URL(classLocation)},null);
        return theLoader;
    }

    /**
     * Produce a profiler report about where the leaks are.
     *
     * This requires JBoss's profiler be installed, see:
     *     http://labs.jboss.com/jbossprofiler/
     *
     * @param className The name of the class to profile
     */
    private void profilerLeakReport(String test, String className) {
       /*
        * If you want a report about where the leaks are... uncomment this,
        * add jboss-profiler.jvmti.jar and jboss-commons.jar (for org.jboss.loggin).
        * You will then have a report for where the references are.
        System.out.println(" ----------------" + test + " START ----------------");
        org.jboss.profiler.jvmti.JVMTIInterface jvmti = new org.jboss.profiler.jvmti.JVMTIInterface();
        System.out.println(jvmti.exploreClassReferences(className, 8, true, true, true, false, false));
        System.out.println(" ----------------" + test + " END ------------------");
        */
    }

    /**
     * Test for JDK 1.5
     */
    private boolean isPre15JVM() {
        String version = System.getProperty("java.specification.version");
        StringTokenizer tokenizer = new StringTokenizer(version,".");
        if (tokenizer.nextToken().equals("1")) {
            String minorVersion = tokenizer.nextToken();
            if (minorVersion.equals("0")) return true;
            if (minorVersion.equals("1")) return true;
            if (minorVersion.equals("2")) return true;
            if (minorVersion.equals("3")) return true;
            if (minorVersion.equals("4")) return true;
        }
        return false;
    }

    /**
     * Get the total, free, used memory stats.
     * @return the total, free, used memory stats
     */
    private String getMemoryStats() {
        java.text.DecimalFormat fmt = new java.text.DecimalFormat("#,##0");
        Runtime runtime = Runtime.getRuntime();
        long free = runtime.freeMemory() / 1024;
        long total = runtime.totalMemory() / 1024;
        long used = total - free;
        return "MEMORY - Total: " + fmt.format(total) + "k " + "Used: "
                + fmt.format(used) + "k " + "Free: "
                + fmt.format(free) + "k";
    }
}
