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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test Case for the {@code LazyDynaClass} implementation class.
 * </p>
 */
public class LazyDynaClassTestCase {

    protected LazyDynaClass dynaClass;

    protected String testProperty = "myProperty";

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() throws Exception {
        dynaClass = new LazyDynaClass();
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        dynaClass = null;
    }

    /**
     * Test add(name) method
     */
    @Test
    public void testAddProperty1() {
        dynaClass.add(testProperty);
        final DynaProperty dynaProperty = dynaClass.getDynaProperty(testProperty);
        assertEquals(testProperty, dynaProperty.getName(), "name is correct");
        assertEquals(Object.class, dynaProperty.getType(), "type is correct");
    }

    /**
     * Test add(name, type) method
     */
    @Test
    public void testAddProperty2() {
        dynaClass.add(testProperty, String.class);
        final DynaProperty dynaProperty = dynaClass.getDynaProperty(testProperty);
        assertEquals(testProperty, dynaProperty.getName(), "name is correct");
        assertEquals(String.class, dynaProperty.getType(), "type is correct");
    }

    /**
     * Test add(name, type, readable, writable) method
     */
    @Test
    public void testAddProperty3() {
        assertThrows(UnsupportedOperationException.class, () -> dynaClass.add(testProperty, String.class, true, true));
    }

    /**
     * Test add(name) method with 'null' name
     */
    @Test
    public void testAddPropertyNullName1() {
        assertThrows(NullPointerException.class, () -> dynaClass.add((String) null));
    }

    /**
     * Test add(name, type) method with 'null' name
     */
    @Test
    public void testAddPropertyNullName2() {
        assertThrows(NullPointerException.class, () -> dynaClass.add(null, String.class));
    }

    /**
     * Test add(name, type, readable, writable) method with 'null' name
     */
    @Test
    public void testAddPropertyNullName3() {
        assertThrows(UnsupportedOperationException.class, () -> dynaClass.add(null, String.class, true, true));
    }

    /**
     * Test add(name) method when restricted is set to 'true'
     */
    @Test
    public void testAddPropertyRestricted1() {
        dynaClass.setRestricted(true);
        assertTrue(dynaClass.isRestricted(), "MutableDynaClass is restricted");
        assertThrows(IllegalStateException.class, () -> dynaClass.add(testProperty));
    }

    /**
     * Test add(name, type) method when restricted is set to 'true'
     */
    @Test
    public void testAddPropertyRestricted2() {
        dynaClass.setRestricted(true);
        assertTrue(dynaClass.isRestricted(), "MutableDynaClass is restricted");
        assertThrows(IllegalStateException.class, () -> dynaClass.add(testProperty, String.class));
    }

    /**
     * Test add(name, type, readable, writable) method when restricted is set to 'true'
     */
    @Test
    public void testAddPropertyRestricted3() {
        dynaClass.setRestricted(true);
        assertTrue(dynaClass.isRestricted(), "MutableDynaClass is restricted");
        assertThrows(UnsupportedOperationException.class, () -> dynaClass.add(testProperty, String.class, true, true));
    }

    /**
     * Test retrieving a property which doesn't exist (returnNull is 'false')
     */
    @Test
    public void testGetPropertyDoesntExist1() {
        dynaClass.setReturnNull(false);
        assertFalse(dynaClass.isReturnNull(), "returnNull is 'false'");
        final DynaProperty dynaProperty = dynaClass.getDynaProperty(testProperty);
        assertEquals(testProperty, dynaProperty.getName(), "name is correct");
        assertEquals(Object.class, dynaProperty.getType(), "type is correct");
        assertFalse(dynaClass.isDynaProperty(testProperty), "property doesn't exist");
    }

    /**
     * Test retrieving a property which doesn't exist (returnNull is 'true')
     */
    @Test
    public void testGetPropertyDoesntExist2() {
        dynaClass.setReturnNull(true);
        assertTrue(dynaClass.isReturnNull(), "returnNull is 'true'");
        assertNull(dynaClass.getDynaProperty(testProperty), "property is null");
    }

    /**
     * Test removing a property
     */
    @Test
    public void testRemoveProperty() {
        dynaClass.setReturnNull(true);
        dynaClass.add(testProperty);
        assertTrue(dynaClass.isDynaProperty(testProperty), "Property exists");
        assertNotNull(dynaClass.getDynaProperty(testProperty), "property is Not null");
        dynaClass.remove(testProperty);
        assertFalse(dynaClass.isDynaProperty(testProperty), "Property doesn't exist");
        assertNull(dynaClass.getDynaProperty(testProperty), "property is null");
    }

    /**
     * Test removing a property which doesn't exist
     */
    @Test
    public void testRemovePropertyDoesntExist() {
        assertFalse(dynaClass.isDynaProperty(testProperty), "property doesn't exist");
        dynaClass.remove(testProperty);
        assertFalse(dynaClass.isDynaProperty(testProperty), "property still doesn't exist");
    }

    /**
     * Test removing a property, name is null
     */
    @Test
    public void testRemovePropertyNullName() {
        assertThrows(NullPointerException.class, () -> dynaClass.remove(null));
    }

    /**
     * Test removing a property, DynaClass is restricted
     */
    @Test
    public void testRemovePropertyRestricted() {
        dynaClass.add(testProperty);
        assertTrue(dynaClass.isDynaProperty(testProperty), "Property exists");
        dynaClass.setRestricted(true);
        assertTrue(dynaClass.isRestricted(), "MutableDynaClass is restricted");
        assertThrows(IllegalStateException.class, () -> dynaClass.remove(testProperty));
    }
}