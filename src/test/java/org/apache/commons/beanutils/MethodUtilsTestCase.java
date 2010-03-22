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

package org.apache.commons.beanutils;


import java.io.OutputStream;
import java.io.PrintStream;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.beanutils.priv.PrivateBeanFactory;
import org.apache.commons.beanutils.priv.PublicSubBean;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * <p> Test case for <code>MethodUtils</code> </p>
 *
 */
public class MethodUtilsTestCase extends TestCase {

    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public MethodUtilsTestCase(String name) {
        super(name);
    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() {
    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(MethodUtilsTestCase.class));
    }

    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {
    }


    // ------------------------------------------------ Individual Test Methods

    /**
     * <p> Test <code>getAccessibleMethod</code>.
     */
    public void testGetAccessibleMethod() {
        // test MethodUtils.getAccessibleMethod
        // we'll make things easier by using the convenience methods

        // easy bit first - find a public method
        // METHOD ONE
        Method method = MethodUtils.getAccessibleMethod
                (TestBean.class, "setStringProperty", String.class);

        // check that we've found one that matches
        assertNotNull(method);
        assertEquals("method ONE is named correctly",
                "setStringProperty", method.getName());
        assertTrue("Method ONE is public",
                Modifier.isPublic(method.getModifiers()));

        // trickier this one - find a method in a direct interface
        // METHOD TWO
        method = MethodUtils.getAccessibleMethod
                (PrivateBeanFactory.create().getClass(),
                        "methodBar",
                        String.class);

        // check that we've found one that matches
        assertNotNull(method);
        assertEquals("Method TWO is named correctly",
                "methodBar", method.getName());
        assertTrue("Method TWO is public",
                Modifier.isPublic(method.getModifiers()));

        // trickier this one - find a method in a indirect interface
        // METHOD THREE
        method = MethodUtils.getAccessibleMethod
                (PrivateBeanFactory.createSubclass().getClass(),
                        "methodBaz",
                        String.class);

        // check that we've found one that matches
        assertNotNull(method);
        assertEquals("Method THREE is named correctly",
                "methodBaz", method.getName());
        assertTrue("Method THREE is public",
                Modifier.isPublic(method.getModifiers()));

    }


    /**
     * <p> Test <code>invokeExactMethod</code>.
     */
    public void testInvokeExactMethod() {
        // test MethodUtils.invokeExactMethod
        // easy bit first - invoke a public method
        // METHOD ONE
        try {

            TestBean bean = new TestBean();
            Object ret = MethodUtils.invokeExactMethod(bean, "setStringProperty", "TEST");
            // check that the return's right and that the properties been set
            assertNull(ret);
            assertEquals("Method ONE was invoked", "TEST", bean.getStringProperty());

        } catch (Throwable t) {
            // ONE
            fail("Exception in method ONE prevented invokation: " + t.toString());
        }

        // trickier this one - find a method in a direct interface
        // METHOD TWO FAILURE
        try {

            Object ret = MethodUtils.invokeExactMethod(
                    PrivateBeanFactory.create(),
                    "methodBar",
                    "ANOTHER TEST");

            // check that we've found one that matches
            assertEquals("Method TWO was invoked correctly", "ANOTHER TEST", ret);

        } catch (Throwable t) {
            // METHOD TWO FAILURE
            fail("Exception in method TWO prevented invokation: " + t.toString());
        }


        // trickier this one - find a method in a indirect interface
        // METHOD THREE
        try {

            Object ret = MethodUtils.invokeExactMethod(
                    PrivateBeanFactory.createSubclass(),
                    "methodBaz",
                    "YET ANOTHER TEST");


            // check that we've found one that matches
            assertEquals("Method TWO was invoked correctly", "YET ANOTHER TEST", ret);


        } catch (Throwable t) {
            // METHOD THREE FAILURE
            fail("Exception in method THREE prevented invokation: " + t.toString());

        }
    }
    
    /**
     * <p> Test <code>invokeMethod</code>.
     */
    public void testInvokeMethod() throws Exception {
        // i'm going to test that the actual calls work first and then try them via reflection
        
        AbstractParent parent = new AlphaBean("parent");
        
        // try testAddChild through abstract superclass
        BetaBean childOne = new BetaBean("ChildOne");
        
        assertEquals("Oh no! Badly coded test case! (1)", "ChildOne", parent.testAddChild(childOne));
        
        // let's try MethodUtils version
        assertEquals(
                        "Cannot invoke through abstract class (1)", 
                        "ChildOne", 
                        MethodUtils.invokeMethod(parent, "testAddChild", childOne));

        
        // try adding through interface
        AlphaBean childTwo = new AlphaBean("ChildTwo");
        
        assertEquals("Oh no! Badly coded test case! (2)", "ChildTwo", parent.testAddChild(childTwo));
        
        // let's try MethodUtils version
        assertEquals(
                        "Cannot invoke through interface (1)", 
                        "ChildTwo", 
                        MethodUtils.invokeMethod(parent, "testAddChild", childTwo));
       
        
        Object[] params = new Object[2];

        assertEquals("Oh no! Badly coded test case! (3)", "ChildOne", parent.testAddChild2("parameter", childOne));
        
        
        // let's try MethodUtils version
        params[0] = "parameter";
        params[1] = childOne;
        
        assertEquals(
                        "Cannot invoke through abstract class (1)", 
                        "ChildOne", 
                        MethodUtils.invokeMethod(parent, "testAddChild2", params));
                        
        assertEquals("Oh no! Badly coded test case! (4)", "ChildTwo", parent.testAddChild2("parameter", childTwo));
        
        // let's try MethodUtils version
        params[0] = "parameter";
        params[1] = childTwo;
       
        assertEquals(
                        "Cannot invoke through abstract class (1)", 
                        "ChildTwo", 
                        MethodUtils.invokeMethod(parent, "testAddChild2", params));
        
        // test that exception is correctly thrown when a method cannot be found with matching params
        try {
            // the next line
            parent = new AlphaBean("parent");
            childOne = new BetaBean("ChildOne");
            MethodUtils.invokeMethod(parent, "bogus", childOne);
            // should get here!
            fail("No exception thrown when no appropriate method exists");
            
        } catch (NoSuchMethodException e) {
            // this is what we're expecting!
        }
        
        MethodUtils.invokeMethod(parent, "getName", null);
        MethodUtils.invokeMethod(parent, "getName", null, null);
        MethodUtils.invokeExactMethod(parent, "getName", null);
        MethodUtils.invokeExactMethod(parent, "getName", null, null);        
    }

    
    /**
     * <p> Test <code>invokeMethod</code> with a primitive.
     */
    public void testInvokeMethodWithPrimitives() throws Exception {
        // first test that the bean works 
        PrimitiveBean bean = new PrimitiveBean();
        bean.setFloat(20.0f);
        bean.setLong(10l);
        bean.setBoolean(true);
        bean.setInt(12);
        bean.setDouble(25.5d);
        
        assertEquals("Bug in PrimitiveBean (1)", 20.0f, bean.getFloat(), 0.01f);
        assertEquals("Bug in PrimitiveBean (2)", 10, bean.getLong());
        assertEquals("Bug in PrimitiveBean (3)", true, bean.getBoolean());
        assertEquals("Bug in PrimitiveBean (4)", 12, bean.getInt());
        assertEquals("Bug in PrimitiveBean (5)", 25.5d, bean.getDouble(), 0.01f);
        
        bean = new PrimitiveBean();
        MethodUtils.invokeMethod(bean, "setBoolean", new Boolean(true));
        assertEquals("Call boolean property using invokeMethod", true, bean.getBoolean());

        bean = new PrimitiveBean();
        MethodUtils.invokeMethod(bean, "setFloat", new Float(20.0f));
        assertEquals("Call float property using invokeMethod", 20.0f, bean.getFloat(), 0.01f);
        
        bean = new PrimitiveBean();
        MethodUtils.invokeMethod(bean, "setLong", new Long(10));
        assertEquals("Call float property using invokeMethod", 10, bean.getLong());
        
        bean = new PrimitiveBean();
        MethodUtils.invokeMethod(bean, "setInt", new Integer(12));
        assertEquals("Set float property using invokeMethod", 12, bean.getInt());
        
        bean = new PrimitiveBean();
        MethodUtils.invokeMethod(bean, "setDouble", new Double(25.5d));
        assertEquals("Set float property using invokeMethod", 25.5d, bean.getDouble(), 0.01d);
    }

    public void testStaticInvokeMethod() throws Exception {

        Object value = null;
        int current = TestBean.currentCounter();

        value = MethodUtils.invokeStaticMethod(TestBean.class, "currentCounter", new Object[0]);
        assertEquals("currentCounter value", current, ((Integer) value).intValue());

        MethodUtils.invokeStaticMethod(TestBean.class, "incrementCounter", new Object[0]);
        current++;

        value = MethodUtils.invokeStaticMethod(TestBean.class, "currentCounter", new Object[0]);
        assertEquals("currentCounter value", current, ((Integer) value).intValue());

        MethodUtils.invokeStaticMethod(TestBean.class, "incrementCounter", new Object[] { new Integer(8) } );
        current += 8;

        value = MethodUtils.invokeStaticMethod(TestBean.class, "currentCounter", new Object[0]);
        assertEquals("currentCounter value", current, ((Integer) value).intValue());

        MethodUtils.invokeExactStaticMethod(TestBean.class, "incrementCounter", 
            new Object[] { new Integer(8) }, new Class[] { Number.class } );
        current += 16;

        value = MethodUtils.invokeStaticMethod(TestBean.class, "currentCounter", new Object[0]);
        assertEquals("currentCounter value", current, ((Integer) value).intValue());
    }


    /**
     * Simple tests for accessing static methods via invokeMethod().
     */
    public void testSimpleStatic1() {

        TestBean bean = new TestBean();
        Object value = null;
        int current = TestBean.currentCounter();

        try {

            // Return initial value of the counter
            value = MethodUtils.invokeMethod
                (bean, "currentCounter", new Object[0], new Class[0]);
            assertNotNull("currentCounter exists", value);
            assertTrue("currentCounter type",
                       value instanceof Integer);
            assertEquals("currentCounter value",
                         current,
                         ((Integer) value).intValue());

            // Increment via no-arguments version
            MethodUtils.invokeMethod
                (bean, "incrementCounter", new Object[0], new Class[0]);

            // Validate updated value
            current++;
            value = MethodUtils.invokeMethod
                (bean, "currentCounter", new Object[0], new Class[0]);
            assertNotNull("currentCounter exists", value);
            assertTrue("currentCounter type",
                       value instanceof Integer);
            assertEquals("currentCounter value",
                         current,
                         ((Integer) value).intValue());

            // Increment via specified-argument version
            MethodUtils.invokeMethod
                (bean, "incrementCounter",
                 new Object[] { new Integer(5) },
                 new Class[] { Integer.TYPE });

            // Validate updated value
            current += 5;
            value = MethodUtils.invokeMethod
                (bean, "currentCounter", new Object[0], new Class[0]);
            assertNotNull("currentCounter exists", value);
            assertTrue("currentCounter type",
                       value instanceof Integer);
            assertEquals("currentCounter value",
                         current,
                         ((Integer) value).intValue());

        } catch (Exception e) {
            fail("Threw exception" + e);
        }

    }


    /**
     * Simple tests for accessing static methods via invokeExactMethod().
     */
    public void testSimpleStatic2() {

        TestBean bean = new TestBean();
        Object value = null;
        int current = TestBean.currentCounter();

        try {

            // Return initial value of the counter
            value = MethodUtils.invokeExactMethod
                (bean, "currentCounter", new Object[0], new Class[0]);
            assertNotNull("currentCounter exists", value);
            assertTrue("currentCounter type",
                       value instanceof Integer);
            assertEquals("currentCounter value",
                         current,
                         ((Integer) value).intValue());

            // Increment via no-arguments version
            MethodUtils.invokeExactMethod
                (bean, "incrementCounter", new Object[0], new Class[0]);

            // Validate updated value
            current++;
            value = MethodUtils.invokeExactMethod
                (bean, "currentCounter", new Object[0], new Class[0]);
            assertNotNull("currentCounter exists", value);
            assertTrue("currentCounter type",
                       value instanceof Integer);
            assertEquals("currentCounter value",
                         current,
                         ((Integer) value).intValue());

            // Increment via specified-argument version
            MethodUtils.invokeExactMethod
                (bean, "incrementCounter",
                 new Object[] { new Integer(5) },
                 new Class[] { Integer.TYPE });

            // Validate updated value
            current += 5;
            value = MethodUtils.invokeExactMethod
                (bean, "currentCounter", new Object[0], new Class[0]);
            assertNotNull("currentCounter exists", value);
            assertTrue("currentCounter type",
                       value instanceof Integer);
            assertEquals("currentCounter value",
                         current,
                         ((Integer) value).intValue());


        } catch (Exception e) {
            fail("Threw exception" + e);
        }

    }


    /**
     * Simple tests for accessing static methods via getAccessibleMethod()
     */
    public void testSimpleStatic3() {

        Object value = null;
        int current = TestBean.currentCounter();

        try {

            // Acquire the methods we need
            Method currentCounterMethod = MethodUtils.getAccessibleMethod
                (TestBean.class, "currentCounter",
                 new Class[0]);
            assertNotNull("currentCounterMethod exists",
                          currentCounterMethod);
            assertEquals("currentCounterMethod name",
                         "currentCounter",
                         currentCounterMethod.getName());
            assertEquals("currentCounterMethod args",
                         0,
                         currentCounterMethod.getParameterTypes().length);
            assertTrue("currentCounterMethod public",
                       Modifier.isPublic(currentCounterMethod.getModifiers()));
            assertTrue("currentCounterMethod static",
                       Modifier.isStatic(currentCounterMethod.getModifiers()));
            Method incrementCounterMethod1 = MethodUtils.getAccessibleMethod
                (TestBean.class, "incrementCounter",
                 new Class[0]);
            assertNotNull("incrementCounterMethod1 exists",
                          incrementCounterMethod1);
            assertEquals("incrementCounterMethod1 name",
                         "incrementCounter",
                         incrementCounterMethod1.getName());
            assertEquals("incrementCounterMethod1 args",
                         0,
                         incrementCounterMethod1.getParameterTypes().length);
            assertTrue("incrementCounterMethod1 public",
                       Modifier.isPublic(incrementCounterMethod1.getModifiers()));
            assertTrue("incrementCounterMethod1 static",
                       Modifier.isStatic(incrementCounterMethod1.getModifiers()));
            Method incrementCounterMethod2 = MethodUtils.getAccessibleMethod
                (TestBean.class, "incrementCounter",
                 new Class[] { Integer.TYPE });
            assertNotNull("incrementCounterMethod2 exists",
                          incrementCounterMethod2);
            assertEquals("incrementCounterMethod2 name",
                         "incrementCounter",
                         incrementCounterMethod2.getName());
            assertEquals("incrementCounterMethod2 args",
                         1,
                         incrementCounterMethod2.getParameterTypes().length);
            assertTrue("incrementCounterMethod2 public",
                       Modifier.isPublic(incrementCounterMethod2.getModifiers()));
            assertTrue("incrementCounterMethod2 static",
                       Modifier.isStatic(incrementCounterMethod2.getModifiers()));

            // Return initial value of the counter
            value = currentCounterMethod.invoke(null, new Object[0]);
            assertNotNull("currentCounter exists", value);
            assertTrue("currentCounter type",
                       value instanceof Integer);
            assertEquals("currentCounter value",
                         current,
                         ((Integer) value).intValue());

            // Increment via no-arguments version
            incrementCounterMethod1.invoke(null, new Object[0]);

            // Validate updated value
            current++;
            value = currentCounterMethod.invoke(null, new Object[0]);
            assertNotNull("currentCounter exists", value);
            assertTrue("currentCounter type",
                       value instanceof Integer);
            assertEquals("currentCounter value",
                         current,
                         ((Integer) value).intValue());

            // Increment via specified-argument version
            incrementCounterMethod2.invoke(null,
                                           new Object[] { new Integer(5) });

            // Validate updated value
            current += 5;
            value = currentCounterMethod.invoke(null, new Object[0]);
            assertNotNull("currentCounter exists", value);
            assertTrue("currentCounter type",
                       value instanceof Integer);
            assertEquals("currentCounter value",
                         current,
                         ((Integer) value).intValue());

        } catch (Exception e) {
            fail("Threw exception" + e);
        }

    }

    public void testPublicSub() throws Exception {
        // make sure that bean does what it should
        PublicSubBean bean = new PublicSubBean();
        assertEquals("Start value (foo)", bean.getFoo(), "This is foo");
        assertEquals("Start value (bar)", bean.getBar(), "This is bar");
        bean.setFoo("new foo");
        bean.setBar("new bar");
        assertEquals("Set value (foo)", bean.getFoo(), "new foo");
        assertEquals("Set value (bar)", bean.getBar(), "new bar");
        
        // see if we can access public methods in a default access superclass
        // from a public access subclass instance
        MethodUtils.invokeMethod(bean, "setFoo", "alpha");
        assertEquals("Set value (foo:2)", bean.getFoo(), "alpha");
        MethodUtils.invokeMethod(bean, "setBar", "beta");
        assertEquals("Set value (bar:2)", bean.getBar(), "beta");

        Method method = null;
        try {
            method = MethodUtils.getAccessibleMethod(PublicSubBean.class, "setFoo", String.class);
        } catch (Throwable t) {
            fail("getAccessibleMethod() setFoo threw " + t);
        }
        assertNotNull("getAccessibleMethod() setFoo is Null", method);
        try {
            method.invoke(bean, new Object[] {"1111"});
        } catch (Throwable t) {
            fail("Invoking setFoo threw " + t);
        }
        assertEquals("Set value (foo:3)", "1111", bean.getFoo());

        try {
            method = MethodUtils.getAccessibleMethod(PublicSubBean.class, "setBar", String.class);
        } catch (Throwable t) {
            fail("getAccessibleMethod() setBar threw " + t);
        }
        assertNotNull("getAccessibleMethod() setBar is Null", method);
        try {
            method.invoke(bean, new Object[] {"2222"});
        } catch (Throwable t) {
            fail("Invoking setBar threw " + t);
        }
        assertEquals("Set value (bar:3)", "2222", bean.getBar());
    
    }
    
    public void testParentMethod() throws Exception {
        OutputStream os = new PrintStream(System.out);
        PrintStream ps = new PrintStream(System.out);
        
        A a = new A();
        MethodUtils.invokeMethod(a, "foo", os);
        assertTrue("Method Invoked(1)", a.called);
        
        a = new A();
        MethodUtils.invokeMethod(a, "foo", ps);
        assertTrue("Method Invoked(2)", a.called);
    }

    /**
     * Test {@link MethodUtils#clearCache()}.
     */
    public void testClearCache() {

        MethodUtils.clearCache(); // make sure it starts empty
        PublicSubBean bean = new PublicSubBean();
        try {
            MethodUtils.invokeMethod(bean, "setFoo", "alpha");
        } catch (Throwable t) {
            fail("invokeMethod() threw " + t);
        }
        assertEquals(1, MethodUtils.clearCache());
        assertEquals(0, MethodUtils.clearCache());
    }

    /**
     * Test {@link MethodUtils#setCacheMethods(boolean)}.
     */
    public void testSetCacheMethods() {

        MethodUtils.clearCache(); // make sure it starts empty

        // caching
        MethodUtils.setCacheMethods(true);
        PublicSubBean bean = new PublicSubBean();
        try {
            MethodUtils.invokeMethod(bean, "setFoo", "alpha");
        } catch (Throwable t) {
            fail("invokeMethod() threw " + t);
        }
        assertEquals(1, MethodUtils.clearCache());
        assertEquals(0, MethodUtils.clearCache());

        // no caching
        MethodUtils.setCacheMethods(false);
        try {
            MethodUtils.invokeMethod(bean, "setFoo", "alpha");
        } catch (Throwable t) {
            fail("invokeMethod() threw " + t);
        }
        assertEquals(0, MethodUtils.clearCache());

        // reset default
        MethodUtils.setCacheMethods(true);
    }
}
