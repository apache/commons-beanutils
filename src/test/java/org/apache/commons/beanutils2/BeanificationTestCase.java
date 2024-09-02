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

package org.apache.commons.beanutils2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test Case for changes made during Beanutils Beanification
 * </p>
 */
public class BeanificationTestCase {

    final class Signal {
        private Exception e;
        private int signal;
        private BeanUtilsBean bean;
        private PropertyUtilsBean propertyUtils;
        private ConvertUtilsBean convertUtils;
        private Object marker;

        public BeanUtilsBean getBean() {
            return bean;
        }

        public ConvertUtilsBean getConvertUtils() {
            return convertUtils;
        }

        public Exception getException() {
            return e;
        }

        public Object getMarkerObject() {
            return marker;
        }

        public PropertyUtilsBean getPropertyUtils() {
            return propertyUtils;
        }

        public int getSignal() {
            return signal;
        }

        public void setBean(final BeanUtilsBean bean) {
            this.bean = bean;
        }

        public void setConvertUtils(final ConvertUtilsBean convertUtils) {
            this.convertUtils = convertUtils;
        }

        public void setException(final Exception e) {
            this.e = e;
        }

        public void setMarkerObject(final Object marker) {
            this.marker = marker;
        }

        public void setPropertyUtils(final PropertyUtilsBean propertyUtils) {
            this.propertyUtils = propertyUtils;
        }

        public void setSignal(final int signal) {
            this.signal = signal;
        }
    }

    final class TestClassLoader extends ClassLoader {
        @Override
        public String toString() {
            return "TestClassLoader";
        }
    }

    /** Maximum number of iterations before our test fails */
    public static final int MAX_GC_ITERATIONS = 50;

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() {
        ConvertUtils.deregister();
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        // No action required
    }

    /** Tests whether different threads can set BeanUtils instances correctly */
    @Test
    public void testBeanUtilsBeanSetInstance() throws Exception {

        final class SetInstanceTesterThread extends Thread {

            private final Signal signal;
            private final BeanUtilsBean bean;

            SetInstanceTesterThread(final Signal signal, final BeanUtilsBean bean) {
                this.signal = signal;
                this.bean = bean;
            }

            @Override
            public void run() {
                BeanUtilsBean.setInstance(bean);
                signal.setSignal(21);
                signal.setBean(BeanUtilsBean.getInstance());
            }

            @Override
            public String toString() {
                return "SetInstanceTesterThread";
            }
        }

        final Signal signal = new Signal();
        signal.setSignal(1);

        final BeanUtilsBean beanOne = new BeanUtilsBean();
        final BeanUtilsBean beanTwo = new BeanUtilsBean();

        final SetInstanceTesterThread thread = new SetInstanceTesterThread(signal, beanTwo);
        thread.setContextClassLoader(new TestClassLoader());

        BeanUtilsBean.setInstance(beanOne);
        assertEquals(beanOne, BeanUtilsBean.getInstance(), "Start thread gets right instance");

        thread.start();
        thread.join();

        assertEquals(21, signal.getSignal(), "Signal not set by test thread");
        assertEquals(beanOne, BeanUtilsBean.getInstance(), "Second thread preserves value");
        assertEquals(beanTwo, signal.getBean(), "Second thread gets value it set");
    }

    /** Tests whether calls are independent for different class loaders */
    @Test
    public void testContextClassloaderIndependence() throws Exception {

        final class TestIndependenceThread extends Thread {
            private final Signal signal;
            private final PrimitiveBean bean;

            TestIndependenceThread(final Signal signal, final PrimitiveBean bean) {
                this.signal = signal;
                this.bean = bean;
            }

            @Override
            public void run() {
                try {
                    signal.setSignal(3);
                    final Converter c = (type, value) -> ConvertUtils.primitiveToWrapper(Integer.TYPE).cast(new Integer(9));
                    ConvertUtils.register(c, Integer.TYPE);
                    BeanUtils.setProperty(bean, "int", new Integer(1));
                } catch (final Exception e) {
                    e.printStackTrace();
                    signal.setException(e);
                }
            }

            @Override
            public String toString() {
                return "TestIndependenceThread";
            }
        }

        final PrimitiveBean bean = new PrimitiveBean();
        BeanUtils.setProperty(bean, "int", new Integer(1));
        assertEquals(1, bean.getInt(), "Wrong property value (1)");

        final Converter c = (type, value) -> ConvertUtils.primitiveToWrapper(type).cast(new Integer(5));
        ConvertUtils.register(c, Integer.TYPE);
        BeanUtils.setProperty(bean, "int", new Integer(1));
        assertEquals(5, bean.getInt(), "Wrong property value(2)");

        final Signal signal = new Signal();
        signal.setSignal(1);
        final TestIndependenceThread thread = new TestIndependenceThread(signal, bean);
        thread.setContextClassLoader(new TestClassLoader());

        thread.start();
        thread.join();

        assertNull(signal.getException(), "Exception thrown by test thread:" + signal.getException());
        assertEquals(3, signal.getSignal(), "Signal not set by test thread");
        assertEquals(9, bean.getInt(), "Wrong property value(3)");

    }

    /**
     * Tests whether difference instances are loaded by different context class loaders.
     */
    @Test
    public void testContextClassLoaderLocal() throws Exception {

        final class CCLLTesterThread extends Thread {

            private final Signal signal;
            private final ContextClassLoaderLocal<Integer> ccll;

            CCLLTesterThread(final Signal signal, final ContextClassLoaderLocal<Integer> ccll) {
                this.signal = signal;
                this.ccll = ccll;
            }

            @Override
            public void run() {
                ccll.set(new Integer(1789));
                signal.setSignal(2);
                signal.setMarkerObject(ccll.get());
            }

            @Override
            public String toString() {
                return "CCLLTesterThread";
            }
        }

        final ContextClassLoaderLocal<Integer> ccll = new ContextClassLoaderLocal<>();
        ccll.set(new Integer(1776));
        assertEquals(new Integer(1776), ccll.get(), "Start thread sets value");

        final Signal signal = new Signal();
        signal.setSignal(1);

        final CCLLTesterThread thread = new CCLLTesterThread(signal, ccll);
        thread.setContextClassLoader(new TestClassLoader());

        thread.start();
        thread.join();

        assertEquals(2, signal.getSignal(), "Signal not set by test thread");
        assertEquals(new Integer(1776), ccll.get(), "Second thread preserves value");
        assertEquals(new Integer(1789), signal.getMarkerObject(), "Second thread gets value it set");
    }

    /** Tests whether the unset method works */
    @Test
    public void testContextClassLoaderUnset() throws Exception {
        final BeanUtilsBean beanOne = new BeanUtilsBean();
        final ContextClassLoaderLocal<BeanUtilsBean> ccll = new ContextClassLoaderLocal<>();
        ccll.set(beanOne);
        assertEquals(beanOne, ccll.get(), "Start thread gets right instance");
        ccll.unset();
        assertNotEquals(beanOne, ccll.get(), "Unset works");
    }

    /**
     * Tests whether difference instances are loaded by different context class loaders.
     */
    @Test
    public void testGetByContextClassLoader() throws Exception {

        final class GetBeanUtilsBeanThread extends Thread {

            private final Signal signal;

            GetBeanUtilsBeanThread(final Signal signal) {
                this.signal = signal;
            }

            @Override
            public void run() {
                signal.setSignal(2);
                signal.setBean(BeanUtilsBean.getInstance());
                signal.setConvertUtils(ConvertUtilsBean.getInstance());
                signal.setPropertyUtils(PropertyUtilsBean.getInstance());
            }

            @Override
            public String toString() {
                return "GetBeanUtilsBeanThread";
            }
        }

        final Signal signal = new Signal();
        signal.setSignal(1);

        final GetBeanUtilsBeanThread thread = new GetBeanUtilsBeanThread(signal);
        thread.setContextClassLoader(new TestClassLoader());

        thread.start();
        thread.join();

        assertEquals(2, signal.getSignal(), "Signal not set by test thread");
        assertNotEquals(BeanUtilsBean.getInstance(), signal.getBean(), "Different BeanUtilsBean instances per context classloader");
        assertNotEquals(ConvertUtilsBean.getInstance(), signal.getConvertUtils(), "Different ConvertUtilsBean instances per context classloader");
        assertNotEquals(PropertyUtilsBean.getInstance(), signal.getPropertyUtils(), "Different PropertyUtilsBean instances per context classloader");
    }

    /** Tests whether class loaders and beans are released from memory */
    @Test
    public void testMemoryLeak() throws Exception {
        // many thanks to Juozas Baliuka for suggesting this methodology
        TestClassLoader loader = new TestClassLoader();
        final WeakReference<ClassLoader> loaderReference = new WeakReference<>(loader);
        BeanUtilsBean.getInstance();

        final class GetBeanUtilsBeanThread extends Thread {

            BeanUtilsBean beanUtils;
            ConvertUtilsBean convertUtils;
            PropertyUtilsBean propertyUtils;

            GetBeanUtilsBeanThread() {
            }

            @Override
            public void run() {
                beanUtils = BeanUtilsBean.getInstance();
                convertUtils = ConvertUtilsBean.getInstance();
                propertyUtils = PropertyUtilsBean.getInstance();
                // XXX Log keeps a reference around!
                LogFactory.releaseAll();
            }

            @Override
            public String toString() {
                return "GetBeanUtilsBeanThread";
            }
        }

        GetBeanUtilsBeanThread thread = new GetBeanUtilsBeanThread();
        @SuppressWarnings("unused")
        final WeakReference<Thread> threadWeakReference = new WeakReference<>(thread);
        thread.setContextClassLoader(loader);

        thread.start();
        thread.join();

        final WeakReference<BeanUtilsBean> beanUtilsReference = new WeakReference<>(thread.beanUtils);
        final WeakReference<PropertyUtilsBean> propertyUtilsReference = new WeakReference<>(thread.propertyUtils);
        final WeakReference<ConvertUtilsBean> convertUtilsReference = new WeakReference<>(thread.convertUtils);

        assertNotNull(loaderReference.get(), "Weak reference released early (1)");
        assertNotNull(beanUtilsReference.get(), "Weak reference released early (2)");
        assertNotNull(propertyUtilsReference.get(), "Weak reference released early (3)");
        assertNotNull(convertUtilsReference.get(), "Weak reference released early (4)");

        // dereference strong references
        loader = null;
        thread.setContextClassLoader(null);
        thread = null;

        int iterations = 0;
        int bytz = 2;
        while (true) {
            BeanUtilsBean.getInstance();
            System.gc();
            assertFalse(iterations++ > MAX_GC_ITERATIONS, "Max iterations reached before resource released.");

            if (loaderReference.get() == null && beanUtilsReference.get() == null && propertyUtilsReference.get() == null
                    && convertUtilsReference.get() == null) {
                break;

            }
            // create garbage:
            @SuppressWarnings("unused")
            final byte[] b = new byte[bytz];
            bytz *= 2;
        }
    }

    /** Tests whether class loaders and beans are released from memory by the map used by BeanUtils. */
    @Test
    public void testMemoryLeak2() throws Exception {
        // many thanks to Juozas Baliuka for suggesting this methodology
        TestClassLoader loader = new TestClassLoader();
        final ReferenceQueue<Object> queue = new ReferenceQueue<>();
        final WeakReference<ClassLoader> loaderReference = new WeakReference<>(loader, queue);
        Integer test = new Integer(1);

        final WeakReference<Integer> testReference = new WeakReference<>(test, queue);
        // Map map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.HARD, true);
        final Map<Object, Object> map = new WeakHashMap<>();
        map.put(loader, test);

        assertEquals(test, map.get(loader), "In map");
        assertNotNull(loaderReference.get(), "Weak reference released early (1)");
        assertNotNull(testReference.get(), "Weak reference released early (2)");

        // dereference strong references
        loader = null;
        test = null;

        int iterations = 0;
        int bytz = 2;
        while (true) {
            System.gc();
            assertFalse(iterations++ > MAX_GC_ITERATIONS, "Max iterations reached before resource released.");

            map.isEmpty();

            if (loaderReference.get() == null && testReference.get() == null) {
                break;

            }
            // create garbage:
            @SuppressWarnings("unused")
            final byte[] b = new byte[bytz];
            bytz *= 2;
        }
    }

    /** Test of the methodology we'll use for some of the later tests */
    @Test
    public void testMemoryTestMethodology() throws Exception {
        // test methodology
        // many thanks to Juozas Baliuka for suggesting this method
        ClassLoader loader = new ClassLoader(this.getClass().getClassLoader()) {
        };
        final WeakReference<ClassLoader> reference = new WeakReference<>(loader);
        @SuppressWarnings("unused")
        Class<?> myClass = loader.loadClass("org.apache.commons.beanutils2.BetaBean");

        assertNotNull(reference.get(), "Weak reference released early");

        // dereference class loader and class:
        loader = null;
        myClass = null;

        int iterations = 0;
        int bytz = 2;
        while (true) {
            System.gc();
            assertFalse(iterations++ > MAX_GC_ITERATIONS, "Max iterations reached before resource released.");
            if (reference.get() == null) {
                break;

            }
            // create garbage:
            @SuppressWarnings("unused")
            final byte[] b = new byte[bytz];
            bytz *= 2;
        }
    }
}
