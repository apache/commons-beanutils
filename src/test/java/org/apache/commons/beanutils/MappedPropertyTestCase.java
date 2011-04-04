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

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Test Case for the <code>MappedPropertyDescriptor</code>.</p>
 *
 */
public class MappedPropertyTestCase extends TestCase {

    private static final Log log = LogFactory.getLog(MappedPropertyTestCase.class);


    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public MappedPropertyTestCase(String name) {
        super(name);
    }

    // -------------------------------------------------- Overall Test Methods

    /**
     * Run this Test
     */
    public static void main(String[] args) {
      junit.textui.TestRunner.run(suite());
    }

    /**
     * Set up instance variables required by this test case.
     */
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
    public void tearDown() {
    }

    // ------------------------------------------------ Individual Test Methods

    /**
     * Test valid method name
     */
    public void testFound() {
        String property = "mapproperty";
        Class clazz = MappedPropertyTestBean.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test boolean "is" method name
     */
    public void testBooleanMapped() {
        String property = "mappedBoolean";
        Class clazz = MappedPropertyTestBean.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test invalid method name
     */
    public void testNotFound() {
        String property = "xxxxxxx";
        Class clazz = MappedPropertyTestBean.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            fail("Property '" + property + "' found in " + clazz.getName());
        } catch (Exception ex) {
            // expected result
        }
    }

    /**
     * Test Mapped Property - Getter only
     */
    public void testMappedGetterOnly() {
        String property = "mappedGetterOnly";
        Class clazz = MappedPropertyTestBean.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNull("Setter is found", desc.getMappedWriteMethod());
        } catch (Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test Mapped Property - Setter Only
     */
    public void testMappedSetterOnly() {
        String property = "mappedSetterOnly";
        Class clazz = MappedPropertyTestBean.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            assertNull("Getter is found", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test Mapped Property - Invalid Setter
     */
    public void testInvalidSetter() {
        String property = "invalidSetter";
        Class clazz = MappedPropertyTestBean.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNull("Setter is found", desc.getMappedWriteMethod());
        } catch (Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test Mapped Property - Invalid Getter
     */
    public void testInvalidGetter() {
        String property = "invalidGetter";
        Class clazz = MappedPropertyTestBean.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            assertNull("Getter is found", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (Exception ex) {
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
        String property = "differentTypes";
        Class clazz = MappedPropertyTestBean.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNull("Setter is found", desc.getMappedWriteMethod());
        } catch (Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test Mpa getter
     */
    public void testMapGetter() {
        MappedPropertyTestBean bean = new MappedPropertyTestBean();
        Class clazz = MappedPropertyTestBean.class;
        String property = "myMap";
        try {
            String testValue = "test value";
            String testKey   = "testKey";
            BeanUtils.setProperty(bean, "myMap("+testKey+")", "test value");
            assertEquals("Map getter", testValue, bean.getMyMap().get(testKey));
        } catch (Exception ex) {
            fail("Test set mapped property failed: " + ex);
        }
    }


    /**
     * Test property with any two args
     */
    public void testAnyArgsProperty() {
        String property = "anyMapped";
        Class clazz = MappedPropertyTestBean.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            assertNull("Getter is found", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test property with two primitve args
     */
    public void testPrimitiveArgsProperty() {
        String property = "mappedPrimitive";
        Class clazz = MappedPropertyTestBean.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            assertNull("Getter is found", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test 'protected' mapped property
     */
    public void testProtected() {
        String property = "protectedProperty";
        Class clazz = MappedPropertyTestBean.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            fail("Property '" + property + "' found in " + clazz.getName());
        } catch (Exception ex) {
            // expected result
        }
    }


    /**
     * Test 'public' method in parent
     */
    public void testPublicParentMethod() {
        String property = "mapproperty";
        Class clazz = MappedPropertyChildBean.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test 'protected' method in parent
     */
    public void testProtectedParentMethod() {
        String property = "protectedMapped";
        Class clazz = MappedPropertyChildBean.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            fail("Property '" + property + "' found in " + clazz.getName());
        } catch (Exception ex) {
        }
    }


    /**
     * Test Interface with mapped property
     */
    public void testInterfaceMapped() {
        String property = "mapproperty";
        Class clazz = MappedPropertyTestInterface.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }

    /**
     * Test property not found in interface
     */
    public void testInterfaceNotFound() {
        String property = "XXXXXX";
        Class clazz = MappedPropertyTestInterface.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            fail("Property '" + property + "' found in " + clazz.getName());
        } catch (Exception ex) {
        }
    }

    /**
     * Test Interface Inherited mapped property
     */
    public void testChildInterfaceMapped() {
        String property = "mapproperty";
        Class clazz = MappedPropertyChildInterface.class;
        try {
            MappedPropertyDescriptor desc 
                = new MappedPropertyDescriptor(property, clazz);
            assertNotNull("Getter is missing", desc.getMappedReadMethod());
            assertNotNull("Setter is missing", desc.getMappedWriteMethod());
        } catch (Exception ex) {
            fail("Property '" + property + "' Not Found in " + clazz.getName() + ": " + ex);
        }
    }
}