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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test Case for the {@code MappedPropertyDescriptor}.
 * </p>
 */
public class MappedPropertyTestCase {

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
    public void tearDown() {
    }

    /**
     * Test property with any two args
     */
    @Test
    public void testAnyArgsProperty() {
        final String property = "anyMapped";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc = new MappedPropertyDescriptor(property, clazz);
            assertNull(desc.getMappedReadMethod(), "Getter is found");
            assertNotNull(desc.getMappedWriteMethod(), "Setter is missing");
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test boolean "is" method name
     */
    @Test
    public void testBooleanMapped() {
        final String property = "mappedBoolean";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc = new MappedPropertyDescriptor(property, clazz);
            assertNotNull(desc.getMappedReadMethod(), "Getter is missing");
            assertNotNull(desc.getMappedWriteMethod(), "Setter is missing");
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test Interface Inherited mapped property
     */
    @Test
    public void testChildInterfaceMapped() {
        final String property = "mapproperty";
        final Class<?> clazz = MappedPropertyChildInterface.class;
        try {
            final MappedPropertyDescriptor desc = new MappedPropertyDescriptor(property, clazz);
            assertNotNull(desc.getMappedReadMethod(), "Getter is missing");
            assertNotNull(desc.getMappedWriteMethod(), "Setter is missing");
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test Mapped Property - Different Types
     *
     * Expect to find the getDifferentTypes() method, but not the setDifferentTypes() method because setDifferentTypes() sets and Integer, while
     * getDifferentTypes() returns a Long.
     */
    @Test
    public void testDifferentTypes() {
        final String property = "differentTypes";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc = new MappedPropertyDescriptor(property, clazz);
            assertNotNull(desc.getMappedReadMethod(), "Getter is missing");
            assertNull(desc.getMappedWriteMethod(), "Setter is found");
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test valid method name
     */
    @Test
    public void testFound() {
        final String property = "mapproperty";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc = new MappedPropertyDescriptor(property, clazz);
            assertNotNull(desc.getMappedReadMethod(), "Getter is missing");
            assertNotNull(desc.getMappedWriteMethod(), "Setter is missing");
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test Interface with mapped property
     */
    @Test
    public void testInterfaceMapped() {
        final String property = "mapproperty";
        final Class<?> clazz = MappedPropertyTestInterface.class;
        try {
            final MappedPropertyDescriptor desc = new MappedPropertyDescriptor(property, clazz);
            assertNotNull(desc.getMappedReadMethod(), "Getter is missing");
            assertNotNull(desc.getMappedWriteMethod(), "Setter is missing");
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test property not found in interface
     */
    @Test
    public void testInterfaceNotFound() {
        final String property = "XXXXXX";
        final Class<?> clazz = MappedPropertyTestInterface.class;
        try {
            new MappedPropertyDescriptor(property, clazz);
            fail("Property '" + property + "' found in " + clazz.getName());
        } catch (final Exception ex) {
        }
    }

    /**
     * Test Mapped Property - Invalid Getter
     */
    @Test
    public void testInvalidGetter() {
        final String property = "invalidGetter";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc = new MappedPropertyDescriptor(property, clazz);
            assertNull(desc.getMappedReadMethod(), "Getter is found");
            assertNotNull(desc.getMappedWriteMethod(), "Setter is missing");
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test Mapped Property - Invalid Setter
     */
    @Test
    public void testInvalidSetter() {
        final String property = "invalidSetter";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc = new MappedPropertyDescriptor(property, clazz);
            assertNotNull(desc.getMappedReadMethod(), "Getter is missing");
            assertNull(desc.getMappedWriteMethod(), "Setter is found");
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test Map getter
     */
    @Test
    public void testMapGetter() {
        final MappedPropertyTestBean bean = new MappedPropertyTestBean();
        try {
            final String testValue = "test value";
            final String testKey = "testKey";
            BeanUtils.setProperty(bean, "myMap(" + testKey + ")", "test value");
            assertEquals(testValue, bean.getMyMap().get(testKey), "Map getter");
        } catch (final Exception ex) {
            fail("Test set mapped property failed: " + ex);
        }
    }

    /**
     * Test Mapped Property - Getter only
     */
    @Test
    public void testMappedGetterOnly() {
        final String property = "mappedGetterOnly";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc = new MappedPropertyDescriptor(property, clazz);
            assertNotNull(desc.getMappedReadMethod(), "Getter is missing");
            assertNull(desc.getMappedWriteMethod(), "Setter is found");
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test Mapped Property - Setter Only
     */
    @Test
    public void testMappedSetterOnly() {
        final String property = "mappedSetterOnly";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc = new MappedPropertyDescriptor(property, clazz);
            assertNull(desc.getMappedReadMethod(), "Getter is found");
            assertNotNull(desc.getMappedWriteMethod(), "Setter is missing");
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test invalid method name
     */
    @Test
    public void testNotFound() {
        final String property = "xxxxxxx";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            new MappedPropertyDescriptor(property, clazz);
            fail("Property '" + property + "' found in " + clazz.getName());
        } catch (final Exception ex) {
            // expected result
        }
    }

    /**
     * Test property with two primitive args
     */
    @Test
    public void testPrimitiveArgsProperty() {
        final String property = "mappedPrimitive";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc = new MappedPropertyDescriptor(property, clazz);
            assertNull(desc.getMappedReadMethod(), "Getter is found");
            assertNotNull(desc.getMappedWriteMethod(), "Setter is missing");
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test 'protected' mapped property
     */
    @Test
    public void testProtected() {
        final String property = "protectedProperty";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            new MappedPropertyDescriptor(property, clazz);
            fail("Property '" + property + "' found in " + clazz.getName());
        } catch (final Exception ex) {
            // expected result
        }
    }

    /**
     * Test 'protected' method in parent
     */
    @Test
    public void testProtectedParentMethod() {
        final String property = "protectedMapped";
        final Class<?> clazz = MappedPropertyChildBean.class;
        try {
            new MappedPropertyDescriptor(property, clazz);
            fail("Property '" + property + "' found in " + clazz.getName());
        } catch (final Exception ex) {
        }
    }

    /**
     * Test 'public' method in parent
     */
    @Test
    public void testPublicParentMethod() {
        final String property = "mapproperty";
        final Class<?> clazz = MappedPropertyChildBean.class;
        try {
            final MappedPropertyDescriptor desc = new MappedPropertyDescriptor(property, clazz);
            assertNotNull(desc.getMappedReadMethod(), "Getter is missing");
            assertNotNull(desc.getMappedWriteMethod(), "Setter is missing");
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }
}