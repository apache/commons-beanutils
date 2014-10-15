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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.priv.PrivateBeanFactory;
import org.apache.commons.beanutils.priv.PublicSubBean;

/**
 * <p> Test case for <code>MethodUtils</code> </p>
 *
 * @version $Id$
 */
public class MethodUtilsTestCase extends TestCase {

    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public MethodUtilsTestCase(final String name) {
        super(name);
    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    @Override
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
    @Override
    public void tearDown() {
    }


    // ------------------------------------------------ Individual Test Methods

    /**
     * <p> Test <code>getAccessibleMethod</code>.
     */
    public void testGetAccessibleMethod() {
        // easy bit first - find a public method
        final Method method = MethodUtils.getAccessibleMethod
                (TestBean.class, "setStringProperty", String.class);

        assertMethod(method, "setStringProperty");
    }

    public void testGetAccessibleMethodFromInterface() {
        Method method;
        // trickier this one - find a method in a direct interface
        method = MethodUtils.getAccessibleMethod
                (PrivateBeanFactory.create().getClass(),
                        "methodBar",
                        String.class);

        assertMethod(method, "methodBar");
    }


    public void testGetAccessibleMethodIndirectInterface() {
        Method method;
        // trickier this one - find a method in a indirect interface
        method = MethodUtils.getAccessibleMethod
                (PrivateBeanFactory.createSubclass().getClass(),
                        "methodBaz",
                        String.class);

        assertMethod(method, "methodBaz");
    }

    private static void assertMethod(final Method method, final String methodName) {
        assertNotNull(method);
        assertEquals("Method is not named correctly", methodName,
                method.getName());
        assertTrue("Method is not public",
                Modifier.isPublic(method.getModifiers()));
    }

    /**
     * <p> Test <code>invokeExactMethod</code>.
     */
    public void testInvokeExactMethod() throws Exception {
            final TestBean bean = new TestBean();
            final Object ret = MethodUtils.invokeExactMethod(bean, "setStringProperty", "TEST");

            assertNull(ret);
            assertEquals("Method ONE was invoked", "TEST", bean.getStringProperty());
    }

    public void testInvokeExactMethodFromInterface() throws Exception {
        final Object ret = MethodUtils.invokeExactMethod(
                PrivateBeanFactory.create(),
                "methodBar",
                "ANOTHER TEST");

        assertEquals("Method TWO wasn't invoked correctly", "ANOTHER TEST", ret);
    }

    public void testInvokeExactMethodIndirectInterface() throws Exception {
        final Object ret = MethodUtils.invokeExactMethod(
                PrivateBeanFactory.createSubclass(),
                "methodBaz",
                "YET ANOTHER TEST");

        assertEquals("Method TWO was invoked correctly", "YET ANOTHER TEST", ret);
    }


    public void testInvokeExactMethodNullArray() throws Exception {
        final Object result = MethodUtils.invokeExactMethod(
                new AlphaBean("parent"),
                "getName",
                null);
        assertEquals("parent", result);
    }

    public void testInvokeExactMethodNullArrayNullArray() throws Exception {
        final Object result = MethodUtils.invokeExactMethod(
                new AlphaBean("parent"),
                "getName",
                null,
                null);

        assertEquals("parent", result);
    }

    public void testInvokeExactMethodNull() throws Exception {
        final Object object = new Object();
        final Object result = MethodUtils.invokeExactMethod(object, "toString", (Object) null);
        assertEquals(object.toString(), result);
    }

    /**
     * <p> Test <code>invokeMethod</code>.
     */
    public void testInvokeMethod() throws Exception {
        final AbstractParent parent = new AlphaBean("parent");
        final BetaBean childOne = new BetaBean("ChildOne");

        assertEquals(
                        "Cannot invoke through abstract class (1)",
                        "ChildOne",
                        MethodUtils.invokeMethod(parent, "testAddChild", childOne));
    }

    public void testInvokeMethodObject() throws Exception {
        final AbstractParent parent = new AlphaBean("parent");
        final Child childTwo = new AlphaBean("ChildTwo");

        assertEquals("Cannot invoke through interface (1)",
                        "ChildTwo",
                        MethodUtils.invokeMethod(parent, "testAddChild", childTwo));
    }

    public void testInvokeMethodArray() throws Exception {
        final AbstractParent parent = new AlphaBean("parent");
        final AlphaBean childTwo = new AlphaBean("ChildTwo");

        final Object[] params = new Object[2];
        params[0] = "parameter";
        params[1] = childTwo;

        assertEquals("Cannot invoke through abstract class",
                        "ChildTwo",
                        MethodUtils.invokeMethod(parent, "testAddChild2", params));
    }


    public void testInvokeMethodUnknown() throws Exception {
        // test that exception is correctly thrown when a method cannot be found with matching params
        try {
            final AbstractParent parent = new AlphaBean("parent");
            final BetaBean childOne = new BetaBean("ChildOne");
            MethodUtils.invokeMethod(parent, "bogus", childOne);

            fail("No exception thrown when no appropriate method exists");
        } catch (final NoSuchMethodException expected) {
            // this is what we're expecting!
        }
    }

    public void testInvokeMethodNullArray() throws Exception {
        final Object result = MethodUtils.invokeMethod(
                new AlphaBean("parent"),
                "getName",
                null);

        assertEquals("parent", result);
    }

    public void testInvokeMethodNullArrayNullArray() throws Exception {
        final Object result = MethodUtils.invokeMethod(
                new AlphaBean("parent"),
                "getName",
                null,
                null);

        assertEquals("parent", result);
    }

    public void testInvokeMethodNull() throws Exception {
        final Object object = new Object();
        final Object result = MethodUtils.invokeMethod(object, "toString", (Object) null);
        assertEquals(object.toString(), result);
    }

    public void testInvokeMethodPrimitiveBoolean() throws Exception {
        final PrimitiveBean bean = new PrimitiveBean();
        MethodUtils.invokeMethod(bean, "setBoolean", Boolean.FALSE);
        assertEquals("Call boolean property using invokeMethod", false, bean.getBoolean());
    }

    public void testInvokeMethodPrimitiveFloat() throws Exception {
        final PrimitiveBean bean = new PrimitiveBean();
        MethodUtils.invokeMethod(bean, "setFloat", Float.valueOf(20.0f));
        assertEquals("Call float property using invokeMethod", 20.0f, bean.getFloat(), 0.01f);
    }

    public void testInvokeMethodPrimitiveLong() throws Exception {
        final PrimitiveBean bean = new PrimitiveBean();
        MethodUtils.invokeMethod(bean, "setLong", Long.valueOf(10));
        assertEquals("Call long property using invokeMethod", 10, bean.getLong());
    }

    public void testInvokeMethodPrimitiveInt() throws Exception {
        final PrimitiveBean bean = new PrimitiveBean();
        MethodUtils.invokeMethod(bean, "setInt", Integer.valueOf(12));
        assertEquals("Set int property using invokeMethod", 12, bean.getInt());
    }

    public void testInvokeMethodPrimitiveDouble() throws Exception {
        final PrimitiveBean bean = new PrimitiveBean();
        MethodUtils.invokeMethod(bean, "setDouble", Double.valueOf(25.5d));
        assertEquals("Set double property using invokeMethod", 25.5d, bean.getDouble(), 0.01d);
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

    public void testInvokeStaticMethodNull() throws Exception {
        final int current = TestBean.currentCounter();
        final Object value = MethodUtils.invokeStaticMethod(TestBean.class, "currentCounter", (Object) null);
        assertEquals("currentCounter value", current, ((Integer) value).intValue());
    }

    public void testInvokeExactStaticMethodNull() throws Exception {
        final int current = TestBean.currentCounter();
        final Object value = MethodUtils.invokeExactStaticMethod(TestBean.class, "currentCounter", (Object) null);
        assertEquals("currentCounter value", current, ((Integer) value).intValue());
    }

    /**
     * Simple tests for accessing static methods via invokeMethod().
     */
    public void testSimpleStatic1() {

        final TestBean bean = new TestBean();
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

        } catch (final Exception e) {
            fail("Threw exception" + e);
        }

    }


    /**
     * Simple tests for accessing static methods via invokeExactMethod().
     */
    public void testSimpleStatic2() {

        final TestBean bean = new TestBean();
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

        } catch (final Exception e) {
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
            final Method currentCounterMethod = MethodUtils.getAccessibleMethod
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
            final Method incrementCounterMethod1 = MethodUtils.getAccessibleMethod
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
            final Method incrementCounterMethod2 = MethodUtils.getAccessibleMethod
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

        } catch (final Exception e) {
            fail("Threw exception" + e);
        }

    }

    public void testPublicSub() throws Exception {
        // make sure that bean does what it should
        final PublicSubBean bean = new PublicSubBean();
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
        } catch (final Throwable t) {
            fail("getAccessibleMethod() setFoo threw " + t);
        }
        assertNotNull("getAccessibleMethod() setFoo is Null", method);
        try {
            method.invoke(bean, new Object[] {"1111"});
        } catch (final Throwable t) {
            fail("Invoking setFoo threw " + t);
        }
        assertEquals("Set value (foo:3)", "1111", bean.getFoo());

        try {
            method = MethodUtils.getAccessibleMethod(PublicSubBean.class, "setBar", String.class);
        } catch (final Throwable t) {
            fail("getAccessibleMethod() setBar threw " + t);
        }
        assertNotNull("getAccessibleMethod() setBar is Null", method);
        try {
            method.invoke(bean, new Object[] {"2222"});
        } catch (final Throwable t) {
            fail("Invoking setBar threw " + t);
        }
        assertEquals("Set value (bar:3)", "2222", bean.getBar());

    }

    public void testParentMethod() throws Exception {
        final OutputStream os = new PrintStream(System.out);
        final PrintStream ps = new PrintStream(System.out);

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
    public void testClearCache() throws Exception {
        MethodUtils.clearCache(); // make sure it starts empty
        final PublicSubBean bean = new PublicSubBean();
        MethodUtils.invokeMethod(bean, "setFoo", "alpha");
        assertEquals(1, MethodUtils.clearCache());
        assertEquals(0, MethodUtils.clearCache());
    }

    /**
     * Test {@link MethodUtils#setCacheMethods(boolean)}.
     */
    public void testSetCacheMethods() throws Exception {
        MethodUtils.setCacheMethods(true);
        MethodUtils.clearCache(); // make sure it starts empty

        final PublicSubBean bean = new PublicSubBean();
        MethodUtils.invokeMethod(bean, "setFoo", "alpha");
        assertEquals(1, MethodUtils.clearCache());
        assertEquals(0, MethodUtils.clearCache());
    }

    public void testNoCaching() throws Exception {
        // no caching
        MethodUtils.setCacheMethods(false);

        final PublicSubBean bean = new PublicSubBean();
        MethodUtils.invokeMethod(bean, "setFoo", "alpha");
        assertEquals(0, MethodUtils.clearCache());

        // reset default
        MethodUtils.setCacheMethods(true);
    }
}
