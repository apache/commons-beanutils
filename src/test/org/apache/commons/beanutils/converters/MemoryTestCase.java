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

package org.apache.commons.beanutils.converters;

import java.lang.ref.WeakReference;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.ConvertUtils;
import junit.framework.TestCase;

/**
 * This class provides a number of unit tests related to classloaders and
 * garbage collection, particularly in j2ee-like situations.
 */
public class MemoryTestCase extends TestCase {

    public void testWeakReference() throws Exception {
        ClassLoader origContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
        ClassReloader componentLoader = new ClassReloader(origContextClassLoader);

        Thread.currentThread().setContextClassLoader(componentLoader);
        Thread.currentThread().setContextClassLoader(origContextClassLoader);

        WeakReference ref = new WeakReference(componentLoader);
        componentLoader = null;

        forceGarbageCollection(ref);
        assertNull(ref.get());
        } finally {
            // Restore context classloader that was present before this
            // test started. It is expected to be the same as the system
            // classloader, but we handle all cases here..
            Thread.currentThread().setContextClassLoader(origContextClassLoader);

            // and restore all the standard converters
            ConvertUtils.deregister();
        }
    }

    /**
     * Test whether registering a standard Converter instance while
     * a custom context classloader is set causes a memory leak.
     *
     * <p>This test emulates a j2ee container where BeanUtils has been
     * loaded from a "common" lib location that is shared across all
     * components running within the container. The "component" registers
     * a converter object, whose class was loaded from the "common" lib
     * location. The registered converter:
     * <ul>
     * <li>should not be visible to other components; and</li>
     * <li>should not prevent the component-specific classloader from being
     *  garbage-collected when the container sets its reference to null.
     * </ul>
     *
     */
    public void testComponentRegistersStandardConverter() throws Exception {

        ClassLoader origContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            // sanity check; who's paranoid?? :-)
            assertEquals(origContextClassLoader, ConvertUtils.class.getClassLoader());

            // create a custom classloader for a "component"
            // just like a container would.
            ClassLoader componentLoader1 = new ClassLoader() {};
            ClassLoader componentLoader2 = new ClassLoader() {};

            Converter origFloatConverter = ConvertUtils.lookup(Float.TYPE);
            Converter floatConverter1 = new FloatConverter();

            // Emulate the container invoking a component #1, and the component
            // registering a custom converter instance whose class is
            // available via the "shared" classloader.
            Thread.currentThread().setContextClassLoader(componentLoader1);
            {
                // here we pretend we're running inside component #1

                // When we first do a ConvertUtils operation inside a custom
                // classloader, we get a completely fresh copy of the
                // ConvertUtilsBean, with all-new Converter objects in it..
                assertFalse(ConvertUtils.lookup(Float.TYPE) == origFloatConverter);

                // Now we register a custom converter (but of a standard class).
                // This should only affect code that runs with exactly the
                // same context classloader set.
                ConvertUtils.register(floatConverter1, Float.TYPE);
                assertTrue(ConvertUtils.lookup(Float.TYPE) == floatConverter1);
            }
            Thread.currentThread().setContextClassLoader(origContextClassLoader);

            // The converter visible outside any custom component should not
            // have been altered.
            assertTrue(ConvertUtils.lookup(Float.TYPE) == origFloatConverter);

            // Emulate the container invoking a component #2.
            Thread.currentThread().setContextClassLoader(componentLoader2);
            {
                // here we pretend we're running inside component #2

                // we should get a completely fresh ConvertUtilsBean, with
                // all-new Converter objects again.
                assertFalse(ConvertUtils.lookup(Float.TYPE) == origFloatConverter);
                assertFalse(ConvertUtils.lookup(Float.TYPE) == floatConverter1);
            }
            Thread.currentThread().setContextClassLoader(origContextClassLoader);

            // Emulate a container "undeploying" component #1. This should
            // make component loader available for garbage collection (we hope)
            WeakReference weakRefToComponent1 = new WeakReference(componentLoader1);
            componentLoader1 = null;

            // force garbage collection and  verify that the componentLoader
            // has been garbage-collected
            forceGarbageCollection(weakRefToComponent1);
            assertNull(
                "Component classloader did not release properly; memory leak present",
                weakRefToComponent1.get());
        } finally {
            // Restore context classloader that was present before this
            // test started, so that in case of a test failure we don't stuff
            // up later tests...
            Thread.currentThread().setContextClassLoader(origContextClassLoader);

            // and restore all the standard converters
            ConvertUtils.deregister();
        }
    }

    /**
     * Test whether registering a custom Converter subclass while
     * a custom context classloader is set causes a memory leak.
     *
     * <p>This test emulates a j2ee container where BeanUtils has been
     * loaded from a "common" lib location that is shared across all
     * components running within the container. The "component" registers
     * a converter object, whose class was loaded via the component-specific
     * classloader. The registered converter:
     * <ul>
     * <li>should not be visible to other components; and</li>
     * <li>should not prevent the component-specific classloader from being
     *  garbage-collected when the container sets its reference to null.
     * </ul>
     *
     */
    public void testComponentRegistersCustomConverter() throws Exception {

        ClassLoader origContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            // sanity check; who's paranoid?? :-)
            assertEquals(origContextClassLoader, ConvertUtils.class.getClassLoader());

            // create a custom classloader for a "component"
            // just like a container would.
            ClassReloader componentLoader = new ClassReloader(origContextClassLoader);

            // Load a custom Converter via component loader. This emulates what
            // would happen if a user wrote their own FloatConverter subclass
            // and deployed it via the component-specific classpath.
            Thread.currentThread().setContextClassLoader(componentLoader);
            {
              // Here we pretend we're running inside the component, and that
              // a class FloatConverter has been loaded from the component's
              // private classpath.
              Class newFloatConverterClass = componentLoader.reload(FloatConverter.class);
              Object newFloatConverter = newFloatConverterClass.newInstance();
              assertTrue(newFloatConverter.getClass().getClassLoader() == componentLoader);

              // verify that this new object does implement the Converter type
              // despite being loaded via a classloader different from the one
              // that loaded the Converter class.
              assertTrue(
                "Converter loader via child does not implement parent type",
                Converter.class.isInstance(newFloatConverter));

              // this converter registration will only apply to the
              // componentLoader classloader...
              ConvertUtils.register((Converter)newFloatConverter, Float.TYPE);

              // After registering a custom converter, lookup should return
              // it back to us. We'll try this lookup again with a different
              // context-classloader set, and shouldn't see it
              Converter componentConverter = ConvertUtils.lookup(Float.TYPE);
              assertTrue(componentConverter.getClass().getClassLoader() == componentLoader);

              newFloatConverter = null;
            }
            Thread.currentThread().setContextClassLoader(origContextClassLoader);

            // Because the context classloader has been reset, we shouldn't
            // see the custom registered converter here...
            Converter sharedConverter = ConvertUtils.lookup(Float.TYPE);
            assertFalse(sharedConverter.getClass().getClassLoader() == componentLoader);

            // and here we should see it again
            Thread.currentThread().setContextClassLoader(componentLoader);
            {
                Converter componentConverter = ConvertUtils.lookup(Float.TYPE);
                assertTrue(componentConverter.getClass().getClassLoader() == componentLoader);
            }
            Thread.currentThread().setContextClassLoader(origContextClassLoader);
            // Emulate a container "undeploying" the component. This should
            // make component loader available for garbage collection (we hope)
            WeakReference weakRefToComponent = new WeakReference(componentLoader);
            componentLoader = null;

            // force garbage collection and  verify that the componentLoader
            // has been garbage-collected
            forceGarbageCollection(weakRefToComponent);
            assertNull(
                "Component classloader did not release properly; memory leak present",
                weakRefToComponent.get());
        } finally {
            // Restore context classloader that was present before this
            // test started. It is expected to be the same as the system
            // classloader, but we handle all cases here..
            Thread.currentThread().setContextClassLoader(origContextClassLoader);

            // and restore all the standard converters
            ConvertUtils.deregister();
        }
    }

    /**
     * Attempt to force garbage collection of the specified target.
     *
     * <p>Unfortunately there is no way to force a JVM to perform
     * garbage collection; all we can do is <i>hint</i> to it that
     * garbage-collection would be a good idea, and to consume
     * memory in order to trigger it.</p>
     *
     * <p>On return, target.get() will return null if the target has
     * been garbage collected.</p>
     *
     * <p>If target.get() still returns non-null after this method has returned,
     * then either there is some reference still being held to the target, or
     * else we were not able to trigger garbage collection; there is no way
     * to tell these scenarios apart.</p>
     */
    private void forceGarbageCollection(WeakReference target) {
        int bytes = 2;

        while(target.get() != null) {
            System.gc();

            // Create increasingly-large amounts of non-referenced memory
            // in order to persuade the JVM to collect it. We are hoping
            // here that the JVM is dumb enough to run a full gc pass over
            // all data (including the target) rather than simply collecting
            // this easily-reclaimable memory!
            try {
                byte[] b =  new byte[bytes];
                bytes = bytes * 2;
            } catch(OutOfMemoryError e) {
                // well that sure should have forced a garbage collection
                // run to occur!
                break;
            }
        }

        // and let's do one more just to clean up any garbage we might have
        // created on the last pass..
        System.gc();
    }
}
