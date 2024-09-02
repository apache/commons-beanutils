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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test case for {@code ConstructorUtils}
 * </p>
 */
public class ConstructorUtilsTestCase {

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() throws Exception {
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetAccessibleConstructor() throws Exception {
        {
            final Constructor<?> ctor = ConstructorUtils.getAccessibleConstructor(TestBean.class, String.class);
            assertNotNull(ctor);
            assertTrue(Modifier.isPublic(ctor.getModifiers()));
        }
        {
            final Constructor<?> ctor = ConstructorUtils.getAccessibleConstructor(TestBean.class, Integer.class);
            assertNotNull(ctor);
            assertTrue(Modifier.isPublic(ctor.getModifiers()));
        }
        {
            final Constructor<?> ctor = ConstructorUtils.getAccessibleConstructor(TestBean.class, Integer.TYPE);
            assertNull(ctor);
        }
    }

    @Test
    public void testGetAccessibleConstructorWithConstructorArg() throws Exception {
        {
            final Class<?>[] types = { Integer.class };
            final Constructor<?> c1 = TestBean.class.getConstructor(types);
            final Constructor<?> ctor = ConstructorUtils.getAccessibleConstructor(c1);
            assertNotNull(ctor);
            assertTrue(Modifier.isPublic(ctor.getModifiers()));
        }
        {
            final Class<?>[] types = { Integer.class };
            final Constructor<?> c1 = TestBean.class.getDeclaredConstructor(types);
            final Constructor<?> ctor = ConstructorUtils.getAccessibleConstructor(c1);
            assertNotNull(ctor);
            assertTrue(Modifier.isPublic(ctor.getModifiers()));
        }
        {
            final Class<?>[] types = { Integer.TYPE };
            final Constructor<?> c1 = TestBean.class.getDeclaredConstructor(types);
            final Constructor<?> ctor = ConstructorUtils.getAccessibleConstructor(c1);
            assertNull(ctor);
        }
    }

    @Test
    public void testGetAccessibleConstructorWithTypeArray() throws Exception {
        {
            final Class<?>[] types = { Boolean.TYPE, String.class };
            final Constructor<?> ctor = ConstructorUtils.getAccessibleConstructor(TestBean.class, types);
            assertNotNull(ctor);
            assertTrue(Modifier.isPublic(ctor.getModifiers()));
        }
        {
            final Class<?>[] types = { Boolean.TYPE, Boolean.TYPE, String.class };
            final Constructor<?> ctor = ConstructorUtils.getAccessibleConstructor(TestBean.class, types);
            assertNull(ctor);
        }
    }

    @Test
    public void testInvokeConstructor() throws Exception {
        {
            final Object obj = ConstructorUtils.invokeConstructor(TestBean.class, "TEST");
            assertNotNull(obj);
            assertInstanceOf(TestBean.class, obj);
            assertEquals("TEST", ((TestBean) obj).getStringProperty());
        }
        {
            final Object obj = ConstructorUtils.invokeConstructor(TestBean.class, Float.valueOf(17.3f));
            assertNotNull(obj);
            assertInstanceOf(TestBean.class, obj);
            assertEquals(17.3f, ((TestBean) obj).getFloatProperty(), 0.0f);
        }
    }

    @Test
    public void testInvokeConstructorNull() throws Exception {
        final Object obj = ConstructorUtils.invokeConstructor(TestBean.class, (Object) null);
        assertNotNull(obj);
        assertInstanceOf(TestBean.class, obj);
    }

    @Test
    public void testInvokeConstructorWithArgArray() throws Exception {
        final Object[] args = { Float.valueOf(17.3f), "TEST" };
        final Object obj = ConstructorUtils.invokeConstructor(TestBean.class, args);
        assertNotNull(obj);
        assertInstanceOf(TestBean.class, obj);
        assertEquals(17.3f, ((TestBean) obj).getFloatProperty(), 0.0f);
        assertEquals("TEST", ((TestBean) obj).getStringProperty());
    }

    @Test
    public void testInvokeConstructorWithTypeArray() throws Exception {
        {
            final Object[] args = { Boolean.TRUE, "TEST" };
            final Class<?>[] types = { Boolean.TYPE, String.class };
            final Object obj = ConstructorUtils.invokeConstructor(TestBean.class, args, types);
            assertNotNull(obj);
            assertInstanceOf(TestBean.class, obj);
            assertTrue(((TestBean) obj).getBooleanProperty());
            assertEquals("TEST", ((TestBean) obj).getStringProperty());
        }
        {
            final Object[] args = { Boolean.TRUE, "TEST" };
            final Class<?>[] types = { Boolean.class, String.class };
            final Object obj = ConstructorUtils.invokeConstructor(TestBean.class, args, types);
            assertNotNull(obj);
            assertInstanceOf(TestBean.class, obj);
            assertTrue(((TestBean) obj).isBooleanSecond());
            assertEquals("TEST", ((TestBean) obj).getStringProperty());
        }
    }

    @Test
    public void testInvokeExactConstructor() throws Exception {
        {
            final Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class, "TEST");
            assertNotNull(obj);
            assertInstanceOf(TestBean.class, obj);
            assertEquals("TEST", ((TestBean) obj).getStringProperty());
        }
        assertThrows(NoSuchMethodException.class, () -> ConstructorUtils.invokeExactConstructor(TestBean.class, Float.valueOf(17.3f)));
        {
            final Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class, Boolean.TRUE);
            assertNotNull(obj);
            assertInstanceOf(TestBean.class, obj);
            assertTrue(((TestBean) obj).isBooleanSecond());
        }
    }

    @Test
    public void testInvokeExactConstructorWithArgArray() throws Exception {
        {
            final Object[] args = { Float.valueOf(17.3f), "TEST" };
            assertThrows(NoSuchMethodException.class, () -> ConstructorUtils.invokeExactConstructor(TestBean.class, args));
        }
        {
            final Object[] args = { Boolean.TRUE, "TEST" };
            final Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class, args);
            assertNotNull(obj);
            assertInstanceOf(TestBean.class, obj);
            assertTrue(((TestBean) obj).isBooleanSecond());
            assertEquals("TEST", ((TestBean) obj).getStringProperty());
        }
    }

    @Test
    public void testInvokeExactConstructorWithNull() throws Exception {
        final Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class, (Object) null);
        assertNotNull(obj);
        assertInstanceOf(TestBean.class, obj);
    }

    @Test
    public void testInvokeExactConstructorWithTypeArray() throws Exception {
        {
            final Object[] args = { Boolean.TRUE, "TEST" };
            final Class<?>[] types = { Boolean.TYPE, String.class };
            final Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class, args, types);
            assertNotNull(obj);
            assertInstanceOf(TestBean.class, obj);
            assertTrue(((TestBean) obj).getBooleanProperty());
            assertEquals("TEST", ((TestBean) obj).getStringProperty());
        }
        {
            final Object[] args = { Boolean.TRUE, "TEST" };
            final Class<?>[] types = { Boolean.class, String.class };
            final Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class, args, types);
            assertNotNull(obj);
            assertInstanceOf(TestBean.class, obj);
            assertTrue(((TestBean) obj).isBooleanSecond());
            assertEquals("TEST", ((TestBean) obj).getStringProperty());
        }
        {
            final Object[] args = { Float.valueOf(17.3f), "TEST" };
            final Class<?>[] types = { Float.TYPE, String.class };
            final Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class, args, types);
            assertNotNull(obj);
            assertInstanceOf(TestBean.class, obj);
            assertEquals(17.3f, ((TestBean) obj).getFloatProperty(), 0.0f);
            assertEquals("TEST", ((TestBean) obj).getStringProperty());
        }
        {
            final Object[] args = { Float.valueOf(17.3f), "TEST" };
            final Class<?>[] types = { Float.class, String.class };
            assertThrows(NoSuchMethodException.class, () -> ConstructorUtils.invokeExactConstructor(TestBean.class, args, types));
        }
    }

}
