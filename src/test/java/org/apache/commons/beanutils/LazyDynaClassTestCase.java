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

/**
 * <p>Test Case for the <code>LazyDynaClass</code> implementation class.</p>
 *
 * @version $Id$
 */
public class LazyDynaClassTestCase extends TestCase {

    protected LazyDynaClass dynaClass = null;
    protected String testProperty     = "myProperty";

    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public LazyDynaClassTestCase(final String name) {
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
        dynaClass = new LazyDynaClass();
    }

    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(LazyDynaClassTestCase.class));
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {
        dynaClass = null;
    }

    // ------------------------------------------------ Individual Test Methods

    /**
     * Test add(name) method
     */
    public void testAddProperty1() {
        dynaClass.add(testProperty);
        final DynaProperty dynaProperty = dynaClass.getDynaProperty(testProperty);
        assertEquals("name is correct", testProperty, dynaProperty.getName());
        assertEquals("type is correct", Object.class, dynaProperty.getType());
    }

    /**
     * Test add(name, type) method
     */
    public void testAddProperty2() {
        dynaClass.add(testProperty, String.class);
        final DynaProperty dynaProperty = dynaClass.getDynaProperty(testProperty);
        assertEquals("name is correct", testProperty, dynaProperty.getName());
        assertEquals("type is correct", String.class, dynaProperty.getType());
    }

    /**
     * Test add(name, type, readable, writable) method
     */
    public void testAddProperty3() {
        try {
            dynaClass.add(testProperty, String.class, true, true);
            fail("add(name, type, readable, writable) did not throw UnsupportedOperationException");
        } catch (final UnsupportedOperationException expected) {
            // expected result
        }
    }

    /**
     * Test add(name) method with 'null' name
     */
    public void testAddPropertyNullName1() {
        try {
            dynaClass.add((String)null);
            fail("null property name not prevented");
        } catch (final IllegalArgumentException expected) {
            // expected result
        }
    }

    /**
     * Test add(name, type) method with 'null' name
     */
    public void testAddPropertyNullName2() {
        try {
            dynaClass.add(null, String.class);
            fail("null property name not prevented");
        } catch (final IllegalArgumentException expected) {
            // expected result
        }
    }

    /**
     * Test add(name, type, readable, writable) method with 'null' name
     */
    public void testAddPropertyNullName3() {
        try {
            dynaClass.add(null, String.class, true, true);
            fail("add(name, type, readable, writable) did not throw UnsupportedOperationException");
        } catch (final UnsupportedOperationException expected) {
            // expected result
        }
    }

    /**
     * Test add(name) method when restricted is set to 'true'
     */
    public void testAddPropertyRestricted1() {
        dynaClass.setRestricted(true);
        assertTrue("MutableDynaClass is restricted", dynaClass.isRestricted());
        try {
            dynaClass.add(testProperty);
            fail("add(name) did not throw IllegalStateException");
        } catch (final IllegalStateException expected) {
            // expected result
        }
    }

    /**
     * Test add(name, type) method when restricted is set to 'true'
     */
    public void testAddPropertyRestricted2() {
        dynaClass.setRestricted(true);
        assertTrue("MutableDynaClass is restricted", dynaClass.isRestricted());
        try {
            dynaClass.add(testProperty, String.class);
            fail("add(name, type) did not throw IllegalStateException");
        } catch (final IllegalStateException expected) {
            // expected result
        }
    }

    /**
     * Test add(name, type, readable, writable) method when restricted is set to 'true'
     */
    public void testAddPropertyRestricted3() {
        dynaClass.setRestricted(true);
        assertTrue("MutableDynaClass is restricted", dynaClass.isRestricted());
        try {
            dynaClass.add(testProperty, String.class, true, true);
            fail("add(name, type, readable, writable) did not throw UnsupportedOperationException");
        } catch (final UnsupportedOperationException t) {
            // expected result
        }
    }

    /**
     * Test retrieving a property which doesn't exist (returnNull is 'false')
     */
    public void testGetPropertyDoesntExist1() {
        dynaClass.setReturnNull(false);
        assertFalse("returnNull is 'false'", dynaClass.isReturnNull());
        final DynaProperty dynaProperty = dynaClass.getDynaProperty(testProperty);
        assertEquals("name is correct", testProperty, dynaProperty.getName());
        assertEquals("type is correct", Object.class, dynaProperty.getType());
        assertFalse("property doesnt exist", dynaClass.isDynaProperty(testProperty));
    }


    /**
     * Test retrieving a property which doesn't exist (returnNull is 'true')
     */
    public void testGetPropertyDoesntExist2() {
        dynaClass.setReturnNull(true);
        assertTrue("returnNull is 'true'", dynaClass.isReturnNull());
        assertNull("property is null", dynaClass.getDynaProperty(testProperty));
    }

    /**
     * Test removing a property
     */
    public void testRemoveProperty() {
        dynaClass.setReturnNull(true);
        dynaClass.add(testProperty);
        assertTrue("Property exists", dynaClass.isDynaProperty(testProperty));
        assertNotNull("property is Not null", dynaClass.getDynaProperty(testProperty));
        dynaClass.remove(testProperty);
        assertFalse("Property doesn't exist", dynaClass.isDynaProperty(testProperty));
        assertNull("property is null", dynaClass.getDynaProperty(testProperty));
    }

    /**
     * Test removing a property, name is null
     */
    public void testRemovePropertyNullName() {
        try {
            dynaClass.remove(null);
            fail("remove(null) did not throw IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected result
        }
    }

    /**
     * Test removing a property, DynaClass is restricted
     */
    public void testRemovePropertyRestricted() {
        dynaClass.add(testProperty);
        assertTrue("Property exists", dynaClass.isDynaProperty(testProperty));
        dynaClass.setRestricted(true);
        assertTrue("MutableDynaClass is restricted", dynaClass.isRestricted());
        try {
            dynaClass.remove(testProperty);
            fail("remove property when MutableDynaClassis restricted did not throw IllegalStateException");
        } catch (final IllegalStateException expected) {
            // expected result
        }
    }

    /**
     * Test removing a property which doesn't exist
     */
    public void testRemovePropertyDoesntExist() {
        assertFalse("property doesn't exist", dynaClass.isDynaProperty(testProperty));
        dynaClass.remove(testProperty);
        assertFalse("property still doesn't exist", dynaClass.isDynaProperty(testProperty));
    }
}