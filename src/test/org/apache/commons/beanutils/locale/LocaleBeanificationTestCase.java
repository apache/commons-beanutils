/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.apache.commons.beanutils.locale;

import java.util.*;

import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.ReferenceMap;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.beanutils.ContextClassLoaderLocal;
import org.apache.commons.beanutils.PrimitiveBean;

/**
 * <p>
 * Test Case for changes made during LocaleBeanutils Beanification.
 * This is basically a cut-and-correct version of the beanutils beanifications tests.
 * </p>
 *
 * @author Robert Burrell Donkin
 * @author Juozas Baliuka
 * @version $Revision$ $Date$
 */

public class LocaleBeanificationTestCase extends TestCase {
    
    // ---------------------------------------------------- Constants
    
    /** Maximum number of iterations before our test fails */
    public static final int MAX_GC_ITERATIONS = 50;
    
    // ---------------------------------------------------- Instance Variables


    // ---------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public LocaleBeanificationTestCase(String name) {
        super(name);
    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() {

        LocaleConvertUtils.deregister();

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(LocaleBeanificationTestCase.class));
    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {
        ;    // No action required
    }


    // ------------------------------------------------ Individual Test Methods
    
    /** Test of the methodology we'll use for some of the later tests */
    public void testMemoryTestMethodology() throws Exception {
        // test methodology
        // many thanks to Juozas Baliuka for suggesting this method
        ClassLoader loader = new ClassLoader() {};
        WeakReference reference = new  WeakReference(loader);
        Class myClass = loader.loadClass("org.apache.commons.beanutils.BetaBean");
        
        assertNotNull("Weak reference released early", reference.get());
        
        // dereference class loader and class:
        loader = null;
        myClass = null;
        
        int iterations = 0;
        int bytz = 2;
        while(true) {
            System.gc();
            if(iterations++ > MAX_GC_ITERATIONS){
                fail("Max iterations reached before resource released.");
            }
            if( reference.get() == null ) {
                break;
                
            } else {
                // create garbage:
                byte[] b =  new byte[bytz];
                bytz = bytz * 2;
            }
        }
    }
    
    /** Tests whether classloaders and beans are released from memory by the map used by beanutils */
    public void testMemoryLeak2() throws Exception {
        // tests when the map used by beanutils has the right behaviour
        
        if (isPre14JVM()) {
            System.out.println("WARNING: CANNOT TEST MEMORY LEAK ON PRE1.4 JVM");
            return;
        }
        
        // many thanks to Juozas Baliuka for suggesting this methodology
        TestClassLoader loader = new TestClassLoader();
        ReferenceQueue queue = new ReferenceQueue();
        WeakReference loaderReference = new WeakReference(loader, queue);
        Integer test = new Integer(1);
        
        WeakReference testReference = new WeakReference(test, queue);
        //Map map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.HARD, true);
        Map map = new WeakHashMap();
        map.put(loader, test);
        
        assertEquals("In map", test, map.get(loader));
        assertNotNull("Weak reference released early (1)", loaderReference.get());
        assertNotNull("Weak reference released early (2)", testReference.get());
        
        // dereference strong references
        loader = null;
        test = null;
        
        int iterations = 0;
        int bytz = 2;
        while(true) {
            System.gc();
            if(iterations++ > MAX_GC_ITERATIONS){
                fail("Max iterations reached before resource released.");
            }
            map.isEmpty();
            
            if( 
                loaderReference.get() == null &&
                testReference.get() == null) {
                break;
                
            } else {
                // create garbage:
                byte[] b =  new byte[bytz];
                bytz = bytz * 2;
            }
        }
    }
	
    /** Tests whether classloaders and beans are released from memory */
    public void testMemoryLeak() throws Exception {
        if (isPre14JVM()) {
            System.out.println("WARNING: CANNOT TEST MEMORY LEAK ON PRE1.4 JVM");
            return;
        }
        
        // many thanks to Juozas Baliuka for suggesting this methodology
        TestClassLoader loader = new TestClassLoader();
        WeakReference loaderReference = new  WeakReference(loader);
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance();

        class GetBeanUtilsBeanThread extends Thread {
            
            LocaleBeanUtilsBean beanUtils;
            LocaleConvertUtilsBean convertUtils;
        
            GetBeanUtilsBeanThread() {}
            
            public void run() {
                beanUtils = LocaleBeanUtilsBean.getLocaleBeanUtilsInstance();
                convertUtils = LocaleConvertUtilsBean.getInstance();
                // XXX Log keeps a reference around!
                LogFactory.releaseAll();
            }
            
            public String toString() {
                return "GetBeanUtilsBeanThread";
            }
        }
        
    
        GetBeanUtilsBeanThread thread = new GetBeanUtilsBeanThread();
        WeakReference threadWeakReference = new WeakReference(thread);
        thread.setContextClassLoader(loader);

        thread.start();
        thread.join();
        
        WeakReference beanUtilsReference = new WeakReference(thread.beanUtils);
        WeakReference convertUtilsReference = new WeakReference(thread.convertUtils);
        
        assertNotNull("Weak reference released early (1)", loaderReference.get());
        assertNotNull("Weak reference released early (2)", beanUtilsReference.get());
        assertNotNull("Weak reference released early (4)", convertUtilsReference.get());
        
        // dereference strong references
        loader = null;
        thread.setContextClassLoader(null);
        thread = null;
        
        int iterations = 0;
        int bytz = 2;
        while(true) {
            LocaleBeanUtilsBean.getLocaleBeanUtilsInstance();
            System.gc();
            if(iterations++ > MAX_GC_ITERATIONS){
                fail("Max iterations reached before resource released.");
            }

            if( 
                loaderReference.get() == null &&
                beanUtilsReference.get() == null && 
                convertUtilsReference.get() == null) {
                break;
                
            } else {
                // create garbage:
                byte[] b =  new byte[bytz];
                bytz = bytz * 2;
            }
        }
    }
    
    /** 
     * Tests whether difference instances are loaded by different 
     * context classloaders.
     */
    public void testGetByContextClassLoader() throws Exception {
            
        class GetBeanUtilsBeanThread extends Thread {
            
            private Signal signal;
        
            GetBeanUtilsBeanThread(Signal signal) {
                this.signal = signal;
            }
            
            public void run() {
                signal.setSignal(2);
                signal.setBean(LocaleBeanUtilsBean.getLocaleBeanUtilsInstance());
                signal.setConvertUtils(LocaleConvertUtilsBean.getInstance());
            }
            
            public String toString() {
                return "GetBeanUtilsBeanThread";
            }
        }
            
        Signal signal = new Signal();
        signal.setSignal(1);
        
        GetBeanUtilsBeanThread thread = new GetBeanUtilsBeanThread(signal);
        thread.setContextClassLoader(new TestClassLoader());
        
        thread.start();
        thread.join();
        
        assertEquals("Signal not set by test thread", 2, signal.getSignal());
        assertTrue(
                    "Different LocaleBeanUtilsBean instances per context classloader", 
                    LocaleBeanUtilsBean.getInstance() != signal.getBean());
        assertTrue(
                    "Different LocaleConvertUtilsBean instances per context classloader", 
                    LocaleConvertUtilsBean.getInstance() != signal.getConvertUtils());
    }
    
    
    /** 
     * Tests whether difference instances are loaded by different 
     * context classloaders.
     */
    public void testContextClassLoaderLocal() throws Exception {
            
        class CCLLTesterThread extends Thread {
            
            private Signal signal;
            private ContextClassLoaderLocal ccll;
        
            CCLLTesterThread(Signal signal, ContextClassLoaderLocal ccll) {
                this.signal = signal;
                this.ccll = ccll;
            }
            
            public void run() {
                ccll.set(new Integer(1789));
                signal.setSignal(2);
                signal.setMarkerObject(ccll.get());
            }
            
            public String toString() {
                return "CCLLTesterThread";
            }
        }
            
        ContextClassLoaderLocal ccll = new ContextClassLoaderLocal();
        ccll.set(new Integer(1776));
        assertEquals("Start thread sets value", new Integer(1776), ccll.get());  
        
        Signal signal = new Signal();
        signal.setSignal(1);
        
        CCLLTesterThread thread = new CCLLTesterThread(signal, ccll);
        thread.setContextClassLoader(new TestClassLoader());
        
        thread.start();
        thread.join();
        
        assertEquals("Signal not set by test thread", 2, signal.getSignal());     
        assertEquals("Second thread preserves value", new Integer(1776), ccll.get()); 
        assertEquals("Second thread gets value it set", new Integer(1789), signal.getMarkerObject()); 
    }
    
    /** Tests whether calls are independent for different classloaders */
    public void testContextClassloaderIndependence() throws Exception {
    
        class TestIndependenceThread extends Thread {
            private Signal signal;
            private PrimitiveBean bean;
        
            TestIndependenceThread(Signal signal, PrimitiveBean bean) {
                this.signal = signal;
                this.bean = bean;
            }
            
            public void run() {
                try {
                    signal.setSignal(3);
                    LocaleConvertUtils.register(new LocaleConverter() {
											public Object convert(Class type, Object value) {
                                                return new Integer(9);
                                            }
                                            public Object convert(Class type, Object value, String pattern) {
                                                return new Integer(9);
                                            }
                                                }, Integer.TYPE, Locale.getDefault());
                    LocaleBeanUtils.setProperty(bean, "int", "1");
                } catch (Exception e) {
                    e.printStackTrace();
                    signal.setException(e);
                }
            }
            
            public String toString() {
                return "TestIndependenceThread";
            }
        }
        
        PrimitiveBean bean = new PrimitiveBean();
        LocaleBeanUtils.setProperty(bean, "int", new Integer(1));
        assertEquals("Wrong property value (1)", 1, bean.getInt());

        LocaleConvertUtils.register(new LocaleConverter() {
								public Object convert(Class type, Object value) {
                                    return new Integer(5);
                                }
                                public Object convert(Class type, Object value, String pattern) {
                                    return new Integer(5);
                                }
                                    }, Integer.TYPE, Locale.getDefault());
        LocaleBeanUtils.setProperty(bean, "int", "1");
        assertEquals("Wrong property value(2)", 5, bean.getInt());
    
        Signal signal = new Signal();
        signal.setSignal(1);
        TestIndependenceThread thread = new TestIndependenceThread(signal, bean);
        thread.setContextClassLoader(new TestClassLoader());
        
        thread.start();
        thread.join();
        
        assertNull("Exception thrown by test thread:" + signal.getException(), signal.getException());
        assertEquals("Signal not set by test thread", 3, signal.getSignal());
        assertEquals("Wrong property value(3)", 9, bean.getInt());
        
    }
    
    /** Tests whether different threads can set beanutils instances correctly */
    public void testBeanUtilsBeanSetInstance() throws Exception {
            
        class SetInstanceTesterThread extends Thread {
            
            private Signal signal;
            private LocaleBeanUtilsBean bean;
        
            SetInstanceTesterThread(Signal signal, LocaleBeanUtilsBean bean) {
                this.signal = signal;
                this.bean = bean;
            }
            
            public void run() {
                LocaleBeanUtilsBean.setInstance(bean);
                signal.setSignal(21);
                signal.setBean(LocaleBeanUtilsBean.getLocaleBeanUtilsInstance());
            }
            
            public String toString() {
                return "SetInstanceTesterThread";
            }
        }
        
        Signal signal = new Signal();
        signal.setSignal(1);

        LocaleBeanUtilsBean beanOne = new LocaleBeanUtilsBean();
        LocaleBeanUtilsBean beanTwo = new LocaleBeanUtilsBean();
        
        SetInstanceTesterThread thread = new SetInstanceTesterThread(signal, beanTwo);
        thread.setContextClassLoader(new TestClassLoader());
        
        LocaleBeanUtilsBean.setInstance(beanOne);
        assertEquals("Start thread gets right instance", beanOne, LocaleBeanUtilsBean.getLocaleBeanUtilsInstance()); 
        
        thread.start();
        thread.join();
        
        assertEquals("Signal not set by test thread", 21, signal.getSignal());     
        assertEquals("Second thread preserves value", beanOne, LocaleBeanUtilsBean.getLocaleBeanUtilsInstance()); 
        assertEquals("Second thread gets value it set", beanTwo, signal.getBean()); 
    }
    
    /** Tests whether the unset method works*/
    public void testContextClassLoaderUnset() throws Exception {
        LocaleBeanUtilsBean beanOne = new LocaleBeanUtilsBean();
        ContextClassLoaderLocal ccll = new ContextClassLoaderLocal();
        ccll.set(beanOne);
        assertEquals("Start thread gets right instance", beanOne, ccll.get()); 
        ccll.unset();
        assertTrue("Unset works", !beanOne.equals(ccll.get())); 
    }
    
    private boolean isPre14JVM() {
        // some pre 1.4 JVM have buggy WeakHashMap implementations 
        // this is used to test for those JVM
        String version = System.getProperty("java.specification.version");
        StringTokenizer tokenizer = new StringTokenizer(version,".");
        if (tokenizer.nextToken().equals("1")) {
            String minorVersion = tokenizer.nextToken();
            if (minorVersion.equals("0")) return true;
            if (minorVersion.equals("1")) return true;
            if (minorVersion.equals("2")) return true;
            if (minorVersion.equals("3")) return true;
        }
        return false;
    }
    
    // ---- Auxillary classes
    
    class TestClassLoader extends ClassLoader {
        public String toString() {
            return "TestClassLoader";
        }
    }
    
    class Signal {
        private Exception e;
        private int signal = 0;
        private LocaleBeanUtilsBean bean;
        private LocaleConvertUtilsBean convertUtils;
        private Object marker;
        
        public Exception getException() {
            return e;
        }
        
        public void setException(Exception e) {
            this.e = e;
        }
        
        public int getSignal() {
            return signal;
        }
        
        public void setSignal(int signal) {
            this.signal = signal;
        }
        
        public Object getMarkerObject() {
            return marker;
        }
        
        public void setMarkerObject(Object marker) {
            this.marker = marker;
        }
        
        public LocaleBeanUtilsBean getBean() {
            return bean;
        }
        
        public void setBean(LocaleBeanUtilsBean bean) {
            this.bean = bean;
        }
        
        public LocaleConvertUtilsBean getConvertUtils() {
            return convertUtils;
        }
        
        public void setConvertUtils(LocaleConvertUtilsBean convertUtils) {
            this.convertUtils = convertUtils;
        }
    }
}

