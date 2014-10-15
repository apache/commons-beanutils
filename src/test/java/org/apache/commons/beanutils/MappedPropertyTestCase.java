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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p>Test Case for the <code>MappedPropertyDescriptor</code>.</p>
 *
 * @version $Id$
 */
public class MappedPropertyTestCase extends TestCase {

    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public MappedPropertyTestCase(final String name) {
        super(name);
    }

    // -------------------------------------------------- Overall Test Methods

    /**
     * Run this Test
     */
    public static void main(final String[] args) {
      junit.textui.TestRunner.run(suite());
    }

    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {
    }

    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(MappedPropertyTestCase.class));
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {
    }

    // ------------------------------------------------ Individual Test Methods

    /**
     * Test valid method name
     */
    public void testFound() {
        final String property = "mapproperty";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test boolean "is" method name
     */
    public void testBooleanMapped() {
        final String property = "mappedBoolean";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test invalid method name
     */
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
     * Test Mapped Property - Getter only
     */
    public void testMappedGetterOnly() {
        final String property = "mappedGetterOnly";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNull("Setter is found", desc.getMappedWriteMethod());
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test Mapped Property - Setter Only
     */
    public void testMappedSetterOnly() {
        final String property = "mappedSetterOnly";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc
                = new MappedPropertyDescriptor(property, clazz);
            assertNull("Getter is found", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test Mapped Property - Invalid Setter
     */
    public void testInvalidSetter() {
        final String property = "invalidSetter";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNull("Setter is found", desc.getMappedWriteMethod());
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test Mapped Property - Invalid Getter
     */
    public void testInvalidGetter() {
        final String property = "invalidGetter";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc
                = new MappedPropertyDescriptor(property, clazz);
            assertNull("Getter is found", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test Mapped Property - Different Types
     *
     * Expect to find the getDifferentTypes() method, but not
     * the setDifferentTypes() method because setDifferentTypes()
     * sets and Integer, while getDifferentTypes() returns a Long.
     */
    public void testDifferentTypes() {
        final String property = "differentTypes";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNull("Setter is found", desc.getMappedWriteMethod());
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test Map getter
     */
    public void testMapGetter() {
        final MappedPropertyTestBean bean = new MappedPropertyTestBean();
        try {
            final String testValue = "test value";
            final String testKey   = "testKey";
            BeanUtils.setProperty(bean, "myMap("+testKey+")", "test value");
            assertEquals("Map getter", testValue, bean.getMyMap().get(testKey));
        } catch (final Exception ex) {
            fail("Test set mapped property failed: " + ex);
        }
    }


    /**
     * Test property with any two args
     */
    public void testAnyArgsProperty() {
        final String property = "anyMapped";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc
                = new MappedPropertyDescriptor(property, clazz);
            assertNull("Getter is found", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test property with two primitive args
     */
    public void testPrimitiveArgsProperty() {
        final String property = "mappedPrimitive";
        final Class<?> clazz = MappedPropertyTestBean.class;
        try {
            final MappedPropertyDescriptor desc
                = new MappedPropertyDescriptor(property, clazz);
            assertNull("Getter is found", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test 'protected' mapped property
     */
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
     * Test 'public' method in parent
     */
    public void testPublicParentMethod() {
        final String property = "mapproperty";
        final Class<?> clazz = MappedPropertyChildBean.class;
        try {
            final MappedPropertyDescriptor desc
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test 'protected' method in parent
     */
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
     * Test Interface with mapped property
     */
    public void testInterfaceMapped() {
        final String property = "mapproperty";
        final Class<?> clazz = MappedPropertyTestInterface.class;
        try {
            final MappedPropertyDescriptor desc
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test property not found in interface
     */
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
     * Test Interface Inherited mapped property
     */
    public void testChildInterfaceMapped() {
        final String property = "mapproperty";
        final Class<?> clazz = MappedPropertyChildInterface.class;
        try {
            final MappedPropertyDescriptor desc
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (final Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }
}