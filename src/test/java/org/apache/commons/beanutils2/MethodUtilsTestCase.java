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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.beanutils2.priv.PrivateBeanFactory;
import org.apache.commons.beanutils2.priv.PublicSubBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test case for {@code MethodUtils}
 * </p>
 */
public class MethodUtilsTestCase {

    private static void assertMethod(final Method method, final String methodName) {
        assertNotNull(method);
        assertEquals(methodName, method.getName(), "Method is not named correctly");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method is not public");
    }

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() {
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test {@link MethodUtils#clearCache()}.
     */
    @Test
    public void testClearCache() throws Exception {
        MethodUtils.clearCache(); // make sure it starts empty
        final PublicSubBean bean = new PublicSubBean();
        MethodUtils.invokeMethod(bean, "setFoo", "alpha");
        assertEquals(1, MethodUtils.clearCache());
        assertEquals(0, MethodUtils.clearCache());
    }

    /**
     * <p>
     * Test {@code getAccessibleMethod}.
     */
    @Test
    public void testGetAccessibleMethod() {
        // easy bit first - find a public method
        final Method method = MethodUtils.getAccessibleMethod(TestBean.class, "setStringProperty", String.class);

        assertMethod(method, "setStringProperty");
    }

    @Test
    public void testGetAccessibleMethodFromInterface() {
        Method method;
        // trickier this one - find a method in a direct interface
        method = MethodUtils.getAccessibleMethod(PrivateBeanFactory.create().getClass(), "methodBar", String.class);

        assertMethod(method, "methodBar");
    }

    @Test
    public void testGetAccessibleMethodIndirectInterface() {
        Method method;
        // trickier this one - find a method in a indirect interface
        method = MethodUtils.getAccessibleMethod(PrivateBeanFactory.createSubclass().getClass(), "methodBaz", String.class);

        assertMethod(method, "methodBaz");
    }

    /**
     * <p>
     * Test {@code invokeExactMethod}.
     */
    @Test
    public void testInvokeExactMethod() throws Exception {
        final TestBean bean = new TestBean();
        final Object ret = MethodUtils.invokeExactMethod(bean, "setStringProperty", "TEST");

        assertNull(ret);
        assertEquals("TEST", bean.getStringProperty(), "Method ONE was invoked");
    }

    @Test
    public void testInvokeExactMethodFromInterface() throws Exception {
        final Object ret = MethodUtils.invokeExactMethod(PrivateBeanFactory.create(), "methodBar", "ANOTHER TEST");

        assertEquals("ANOTHER TEST", ret, "Method TWO wasn't invoked correctly");
    }

    @Test
    public void testInvokeExactMethodIndirectInterface() throws Exception {
        final Object ret = MethodUtils.invokeExactMethod(PrivateBeanFactory.createSubclass(), "methodBaz", "YET ANOTHER TEST");

        assertEquals("YET ANOTHER TEST", ret, "Method TWO was invoked correctly");
    }

    @Test
    public void testInvokeExactMethodNull() throws Exception {
        final Object object = new Object();
        final Object result = MethodUtils.invokeExactMethod(object, "toString", (Object) null);
        assertEquals(object.toString(), result);
    }

    @Test
    public void testInvokeExactMethodNullArray() throws Exception {
        final Object result = MethodUtils.invokeExactMethod(new AlphaBean("parent"), "getName", null);
        assertEquals("parent", result);
    }

    @Test
    public void testInvokeExactMethodNullArrayNullArray() throws Exception {
        final Object result = MethodUtils.invokeExactMethod(new AlphaBean("parent"), "getName", null, null);

        assertEquals("parent", result);
    }

    @Test
    public void testInvokeExactStaticMethodNull() throws Exception {
        final int current = TestBean.currentCounter();
        final Object value = MethodUtils.invokeExactStaticMethod(TestBean.class, "currentCounter", (Object) null);
        assertEquals(current, ((Integer) value).intValue(), "currentCounter value");
    }

    /**
     * <p>
     * Test {@code invokeMethod}.
     */
    @Test
    public void testInvokeMethod() throws Exception {
        final AbstractParent parent = new AlphaBean("parent");
        final BetaBean childOne = new BetaBean("ChildOne");

        assertEquals("ChildOne", MethodUtils.invokeMethod(parent, "testAddChild", childOne), "Cannot invoke through abstract class (1)");
    }

    @Test
    public void testInvokeMethodArray() throws Exception {
        final AbstractParent parent = new AlphaBean("parent");
        final AlphaBean childTwo = new AlphaBean("ChildTwo");

        final Object[] params = new Object[2];
        params[0] = "parameter";
        params[1] = childTwo;

        assertEquals("ChildTwo", MethodUtils.invokeMethod(parent, "testAddChild2", params), "Cannot invoke through abstract class");
    }

    @Test
    public void testInvokeMethodNull() throws Exception {
        final Object object = new Object();
        final Object result = MethodUtils.invokeMethod(object, "toString", (Object) null);
        assertEquals(object.toString(), result);
    }

    @Test
    public void testInvokeMethodNullArray() throws Exception {
        final Object result = MethodUtils.invokeMethod(new AlphaBean("parent"), "getName", null);

        assertEquals("parent", result);
    }

    @Test
    public void testInvokeMethodNullArrayNullArray() throws Exception {
        final Object result = MethodUtils.invokeMethod(new AlphaBean("parent"), "getName", null, null);

        assertEquals("parent", result);
    }

    @Test
    public void testInvokeMethodObject() throws Exception {
        final AbstractParent parent = new AlphaBean("parent");
        final Child childTwo = new AlphaBean("ChildTwo");

        assertEquals("ChildTwo", MethodUtils.invokeMethod(parent, "testAddChild", childTwo), "Cannot invoke through interface (1)");
    }

    @Test
    public void testInvokeMethodPrimitiveBoolean() throws Exception {
        final PrimitiveBean bean = new PrimitiveBean();
        MethodUtils.invokeMethod(bean, "setBoolean", Boolean.FALSE);
        assertEquals(false, bean.getBoolean(), "Call boolean property using invokeMethod");
    }

    @Test
    public void testInvokeMethodPrimitiveDouble() throws Exception {
        final PrimitiveBean bean = new PrimitiveBean();
        MethodUtils.invokeMethod(bean, "setDouble", Double.valueOf(25.5d));
        assertEquals(25.5d, bean.getDouble(), 0.01d, "Set double property using invokeMethod");
    }

    @Test
    public void testInvokeMethodPrimitiveFloat() throws Exception {
        final PrimitiveBean bean = new PrimitiveBean();
        MethodUtils.invokeMethod(bean, "setFloat", Float.valueOf(20.0f));
        assertEquals(20.0f, bean.getFloat(), 0.01f, "Call float property using invokeMethod");
    }

    @Test
    public void testInvokeMethodPrimitiveInt() throws Exception {
        final PrimitiveBean bean = new PrimitiveBean();
        MethodUtils.invokeMethod(bean, "setInt", Integer.valueOf(12));
        assertEquals(12, bean.getInt(), "Set int property using invokeMethod");
    }

    @Test
    public void testInvokeMethodPrimitiveLong() throws Exception {
        final PrimitiveBean bean = new PrimitiveBean();
        MethodUtils.invokeMethod(bean, "setLong", Long.valueOf(10));
        assertEquals(10, bean.getLong(), "Call long property using invokeMethod");
    }

    @Test
    public void testInvokeMethodUnknown() throws Exception {
        // test that exception is correctly thrown when a method cannot be found with matching params
        final AbstractParent parent = new AlphaBean("parent");
        final BetaBean childOne = new BetaBean("ChildOne");
        assertThrows(NoSuchMethodException.class, () -> MethodUtils.invokeMethod(parent, "bogus", childOne));
    }

    @Test
    public void testInvokeStaticMethodNull() throws Exception {
        final int current = TestBean.currentCounter();
        final Object value = MethodUtils.invokeStaticMethod(TestBean.class, "currentCounter", (Object) null);
        assertEquals(current, ((Integer) value).intValue(), "currentCounter value");
    }

    @Test
    public void testNoCaching() throws Exception {
        // no caching
        MethodUtils.setCacheMethods(false);

        final PublicSubBean bean = new PublicSubBean();
        MethodUtils.invokeMethod(bean, "setFoo", "alpha");
        assertEquals(0, MethodUtils.clearCache());

        // reset default
        MethodUtils.setCacheMethods(true);
    }

    @Test
    public void testParentMethod() throws Exception {
        final String a = "A";

        assertAll(() -> {
            final String actual1 = (String) MethodUtils.invokeMethod(a, "toLowerCase", null);
            assertEquals("a", actual1);
        }, () -> {
            final char actual2 = (char) MethodUtils.invokeMethod(a, "charAt", 0);
            assertEquals('A', actual2);
        });
    }

    @Test
    public void testPublicSub() throws Exception {
        // make sure that bean does what it should
        final PublicSubBean bean = new PublicSubBean();
        assertEquals(bean.getFoo(), "This is foo", "Start value (foo)");
        assertEquals(bean.getBar(), "This is bar", "Start value (bar)");
        bean.setFoo("new foo");
        bean.setBar("new bar");
        assertEquals(bean.getFoo(), "new foo", "Set value (foo)");
        assertEquals(bean.getBar(), "new bar", "Set value (bar)");

        // see if we can access public methods in a default access superclass
        // from a public access subclass instance
        MethodUtils.invokeMethod(bean, "setFoo", "alpha");
        assertEquals(bean.getFoo(), "alpha", "Set value (foo:2)");
        MethodUtils.invokeMethod(bean, "setBar", "beta");
        assertEquals(bean.getBar(), "beta", "Set value (bar:2)");

        Method method = MethodUtils.getAccessibleMethod(PublicSubBean.class, "setFoo", String.class);
        assertNotNull(method, "getAccessibleMethod() setFoo is Null");
        method.invoke(bean, "1111");
        assertEquals("1111", bean.getFoo(), "Set value (foo:3)");

        method = MethodUtils.getAccessibleMethod(PublicSubBean.class, "setBar", String.class);
        assertNotNull(method, "getAccessibleMethod() setBar is Null");
        method.invoke(bean, "2222");
        assertEquals("2222", bean.getBar(), "Set value (bar:3)");

    }

    /**
     * Test {@link MethodUtils#setCacheMethods(boolean)}.
     */
    @Test
    public void testSetCacheMethods() throws Exception {
        MethodUtils.setCacheMethods(true);
        MethodUtils.clearCache(); // make sure it starts empty

        final PublicSubBean bean = new PublicSubBean();
        MethodUtils.invokeMethod(bean, "setFoo", "alpha");
        assertEquals(1, MethodUtils.clearCache());
        assertEquals(0, MethodUtils.clearCache());
    }

    /**
     * Simple tests for accessing static methods via invokeMethod().
     */
    @Test
    public void testSimpleStatic1() throws Exception {
        final TestBean bean = new TestBean();
        Object value = null;
        int current = TestBean.currentCounter();
        // Return initial value of the counter
        value = MethodUtils.invokeMethod(bean, "currentCounter", new Object[0], new Class[0]);
        assertNotNull(value, "currentCounter exists");
        assertInstanceOf(Integer.class, value, "currentCounter type");
        assertEquals(current, ((Integer) value).intValue(), "currentCounter value");

        // Increment via no-arguments version
        MethodUtils.invokeMethod(bean, "incrementCounter", new Object[0], new Class[0]);

        // Validate updated value
        current++;
        value = MethodUtils.invokeMethod(bean, "currentCounter", new Object[0], new Class[0]);
        assertNotNull(value, "currentCounter exists");
        assertInstanceOf(Integer.class, value, "currentCounter type");
        assertEquals(current, ((Integer) value).intValue(), "currentCounter value");

        // Increment via specified-argument version
        MethodUtils.invokeMethod(bean, "incrementCounter", new Object[] { Integer.valueOf(5) }, new Class[] { Integer.TYPE });

        // Validate updated value
        current += 5;
        value = MethodUtils.invokeMethod(bean, "currentCounter", new Object[0], new Class[0]);
        assertNotNull(value, "currentCounter exists");
        assertInstanceOf(Integer.class, value, "currentCounter type");
        assertEquals(current, ((Integer) value).intValue(), "currentCounter value");
    }

    /**
     * Simple tests for accessing static methods via invokeExactMethod().
     */
    @Test
    public void testSimpleStatic2() throws Exception {
        final TestBean bean = new TestBean();
        Object value = null;
        int current = TestBean.currentCounter();
        // Return initial value of the counter
        value = MethodUtils.invokeExactMethod(bean, "currentCounter", new Object[0], new Class[0]);
        assertNotNull(value, "currentCounter exists");
        assertInstanceOf(Integer.class, value, "currentCounter type");
        assertEquals(current, ((Integer) value).intValue(), "currentCounter value");

        // Increment via no-arguments version
        MethodUtils.invokeExactMethod(bean, "incrementCounter", new Object[0], new Class[0]);

        // Validate updated value
        current++;
        value = MethodUtils.invokeExactMethod(bean, "currentCounter", new Object[0], new Class[0]);
        assertNotNull(value, "currentCounter exists");
        assertInstanceOf(Integer.class, value, "currentCounter type");
        assertEquals(current, ((Integer) value).intValue(), "currentCounter value");

        // Increment via specified-argument version
        MethodUtils.invokeExactMethod(bean, "incrementCounter", new Object[] { Integer.valueOf(5) }, new Class[] { Integer.TYPE });

        // Validate updated value
        current += 5;
        value = MethodUtils.invokeExactMethod(bean, "currentCounter", new Object[0], new Class[0]);
        assertNotNull(value, "currentCounter exists");
        assertInstanceOf(Integer.class, value, "currentCounter type");
        assertEquals(current, ((Integer) value).intValue(), "currentCounter value");
    }

    /**
     * Simple tests for accessing static methods via getAccessibleMethod()
     */
    @Test
    public void testSimpleStatic3() throws Exception {
        Object value = null;
        int current = TestBean.currentCounter();
        // Acquire the methods we need
        final Method currentCounterMethod = MethodUtils.getAccessibleMethod(TestBean.class, "currentCounter", new Class[0]);
        assertNotNull(currentCounterMethod, "currentCounterMethod exists");
        assertEquals("currentCounter", currentCounterMethod.getName(), "currentCounterMethod name");
        assertEquals(0, currentCounterMethod.getParameterTypes().length, "currentCounterMethod args");
        assertTrue(Modifier.isPublic(currentCounterMethod.getModifiers()), "currentCounterMethod public");
        assertTrue(Modifier.isStatic(currentCounterMethod.getModifiers()), "currentCounterMethod static");
        final Method incrementCounterMethod1 = MethodUtils.getAccessibleMethod(TestBean.class, "incrementCounter", new Class[0]);
        assertNotNull(incrementCounterMethod1, "incrementCounterMethod1 exists");
        assertEquals("incrementCounter", incrementCounterMethod1.getName(), "incrementCounterMethod1 name");
        assertEquals(0, incrementCounterMethod1.getParameterTypes().length, "incrementCounterMethod1 args");
        assertTrue(Modifier.isPublic(incrementCounterMethod1.getModifiers()), "incrementCounterMethod1 public");
        assertTrue(Modifier.isStatic(incrementCounterMethod1.getModifiers()), "incrementCounterMethod1 static");
        final Method incrementCounterMethod2 = MethodUtils.getAccessibleMethod(TestBean.class, "incrementCounter", new Class[] { Integer.TYPE });
        assertNotNull(incrementCounterMethod2, "incrementCounterMethod2 exists");
        assertEquals("incrementCounter", incrementCounterMethod2.getName(), "incrementCounterMethod2 name");
        assertEquals(1, incrementCounterMethod2.getParameterTypes().length, "incrementCounterMethod2 args");
        assertTrue(Modifier.isPublic(incrementCounterMethod2.getModifiers()), "incrementCounterMethod2 public");
        assertTrue(Modifier.isStatic(incrementCounterMethod2.getModifiers()), "incrementCounterMethod2 static");

        // Return initial value of the counter
        value = currentCounterMethod.invoke(null);
        assertNotNull(value, "currentCounter exists");
        assertInstanceOf(Integer.class, value, "currentCounter type");
        assertEquals(current, ((Integer) value).intValue(), "currentCounter value");

        // Increment via no-arguments version
        incrementCounterMethod1.invoke(null);

        // Validate updated value
        current++;
        value = currentCounterMethod.invoke(null);
        assertNotNull(value, "currentCounter exists");
        assertInstanceOf(Integer.class, value, "currentCounter type");
        assertEquals(current, ((Integer) value).intValue(), "currentCounter value");

        // Increment via specified-argument version
        incrementCounterMethod2.invoke(null, Integer.valueOf(5));

        // Validate updated value
        current += 5;
        value = currentCounterMethod.invoke(null);
        assertNotNull(value, "currentCounter exists");
        assertInstanceOf(Integer.class, value, "currentCounter type");
        assertEquals(current, ((Integer) value).intValue(), "currentCounter value");
    }

    @Test
    public void testStaticInvokeMethod() throws Exception {

        Object value;
        int current = TestBean.currentCounter();

        value = MethodUtils.invokeStaticMethod(TestBean.class, "currentCounter", new Object[0]);
        assertEquals(current, ((Integer) value).intValue(), "currentCounter value");

        MethodUtils.invokeStaticMethod(TestBean.class, "incrementCounter", new Object[0]);
        current++;

        value = MethodUtils.invokeStaticMethod(TestBean.class, "currentCounter", new Object[0]);
        assertEquals(current, ((Integer) value).intValue(), "currentCounter value");

        MethodUtils.invokeStaticMethod(TestBean.class, "incrementCounter", new Object[] { Integer.valueOf(8) });
        current += 8;

        value = MethodUtils.invokeStaticMethod(TestBean.class, "currentCounter", new Object[0]);
        assertEquals(current, ((Integer) value).intValue(), "currentCounter value");

        MethodUtils.invokeExactStaticMethod(TestBean.class, "incrementCounter", new Object[] { Integer.valueOf(8) }, new Class[] { Number.class });
        current += 16;

        value = MethodUtils.invokeStaticMethod(TestBean.class, "currentCounter", new Object[0]);
        assertEquals(current, ((Integer) value).intValue(), "currentCounter value");
    }
}
