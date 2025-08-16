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
package org.apache.commons.beanutils2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
class MethodUtilsTest {

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
    void testClearCache() throws Exception {
        MethodUtils.clearCache(); // make sure it starts empty
        final PublicSubBean bean = new PublicSubBean();
        assertNotNull(MethodUtils.getAccessibleMethod(bean.getClass(), "setFoo", new Class[] { String.class }));
        assertEquals(1, MethodUtils.clearCache());
        assertEquals(0, MethodUtils.clearCache());
    }

    /**
     * <p>
     * Test {@code getAccessibleMethod}.
     */
    @Test
    void testGetAccessibleMethod() {
        // easy bit first - find a public method
        final Method method = MethodUtils.getAccessibleMethod(TestBean.class, "setStringProperty", String.class);

        assertMethod(method, "setStringProperty");
    }

    @Test
    void testGetAccessibleMethodFromInterface() {
        Method method;
        // trickier this one - find a method in a direct interface
        method = MethodUtils.getAccessibleMethod(PrivateBeanFactory.create().getClass(), "methodBar", String.class);

        assertMethod(method, "methodBar");
    }

    @Test
    void testGetAccessibleMethodIndirectInterface() {
        Method method;
        // trickier this one - find a method in a indirect interface
        method = MethodUtils.getAccessibleMethod(PrivateBeanFactory.createSubclass().getClass(), "methodBaz", String.class);

        assertMethod(method, "methodBaz");
    }

    @Test
    void testNoCaching() throws Exception {
        // no caching
        MethodUtils.setCacheMethods(false);
        final PublicSubBean bean = new PublicSubBean();
        MethodUtils.getAccessibleMethod(bean.getClass(), "setFoo", new Class[] { String.class });
        assertEquals(0, MethodUtils.clearCache());
        // reset default
        MethodUtils.setCacheMethods(true);
    }

    /**
     * Test {@link MethodUtils#setCacheMethods(boolean)}.
     */
    @Test
    void testSetCacheMethods() throws Exception {
        MethodUtils.setCacheMethods(true);
        MethodUtils.clearCache(); // make sure it starts empty
        final PublicSubBean bean = new PublicSubBean();
        MethodUtils.getAccessibleMethod(bean.getClass(), "setFoo", new Class[] { String.class });
        assertEquals(1, MethodUtils.clearCache());
        assertEquals(0, MethodUtils.clearCache());
    }

    /**
     * Simple tests for accessing static methods via getAccessibleMethod()
     */
    @Test
    void testSimpleStatic3() throws Exception {
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
}
