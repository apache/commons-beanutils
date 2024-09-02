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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test Case for the {@code LazyDynaBean} implementation class.
 * </p>
 */
public class LazyDynaBeanTestCase {

    protected LazyDynaBean bean;
    protected LazyDynaClass dynaClass;
    protected String testProperty = "myProperty";
    protected String testPropertyA = "myProperty-A";
    protected String testPropertyB = "myProperty-B";
    protected String testString1 = "myStringValue-1";
    protected String testString2 = "myStringValue-2";
    protected Integer testInteger1 = Integer.valueOf(30);

    protected Integer testInteger2 = Integer.valueOf(40);

    protected String testKey = "myKey";

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() throws Exception {
        bean = new LazyDynaBean();
        dynaClass = (LazyDynaClass) bean.getDynaClass();
        dynaClass.setReturnNull(true);
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        bean = null;
    }

    /**
     * Test Getting/Setting an DynaBean[] array
     */
    @Test
    public void testIndexedDynaBeanArray() {

        final int index = 3;
        final Object objectArray = new LazyDynaMap[0];

        // Check the property & value doesn't exist
        assertNull(dynaClass.getDynaProperty(testProperty), "Check Indexed Property doesn't exist");
        assertNull(bean.get(testProperty), "Check Indexed Property is null");

        // Add a DynaProperty of type String[]
        dynaClass.add(testProperty, objectArray.getClass());
        assertEquals(objectArray.getClass(), dynaClass.getDynaProperty(testProperty).getType(), "Check Indexed Property exists");
        assertEquals(objectArray.getClass(), bean.get(testProperty).getClass(), "Check Indexed Property is correct type");

        // Retrieving from Array should initialize DynaBean
        for (int i = index; i >= 0; i--) {
            assertEquals(LazyDynaMap.class, bean.get(testProperty, index).getClass(), "Check Array Components initialized");
        }

        dynaClass.add(testPropertyB, objectArray.getClass());
        final LazyDynaMap newMap = new LazyDynaMap();
        newMap.set(testPropertyB, testString2);
        bean.set(testPropertyA, index, newMap);
        assertEquals(testString2, ((DynaBean) bean.get(testPropertyA, index)).get(testPropertyB), "Check Indexed Value is correct(a)");

    }

    /**
     * Test setting indexed property for type which is not List or Array
     */
    @Test
    public void testIndexedInvalidType() {
        final int index = 3;
        dynaClass.add(testProperty, String.class);
        assertFalse(dynaClass.getDynaProperty(testProperty).isIndexed(), "Check Property is not indexed");
        assertThrows(IllegalArgumentException.class, () -> bean.set(testProperty, index, testString1));
    }

    /**
     * Test Getting/Setting a List 'Indexed' Property - use alternative List (LinkedList)
     */
    @Test
    public void testIndexedLinkedList() {

        int index = 3;

        // Check the property & value doesn't exist
        assertNull(dynaClass.getDynaProperty(testProperty), "Check Indexed Property doesn't exist");
        assertNull(bean.get(testProperty), "Check Indexed Property is null");

        // Add a 'LinkedList' property to the DynaClass
        dynaClass.add(testProperty, LinkedList.class);
        assertTrue(dynaClass.getDynaProperty(testProperty).isIndexed(), "Check Property is indexed");
        assertEquals(LinkedList.class, dynaClass.getDynaProperty(testProperty).getType(), "Check Property is correct type");
        assertEquals(LinkedList.class, bean.get(testProperty).getClass(), "Check Property type is correct");

        // Set the property, should instantiate a new LinkedList and set appropriate indexed value
        bean.set(testProperty, index, testString1);
        assertEquals(LinkedList.class, bean.get(testProperty).getClass(), "Check Property type is correct");
        assertEquals(testString1, bean.get(testProperty, index), "Check First Indexed Value is correct");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((LinkedList<?>) bean.get(testProperty)).size()), "Check First Array length is correct");

        // Set a second indexed value, should automatically grow the LinkedList and set appropriate indexed value
        index += 2;
        bean.set(testProperty, index, testInteger1);
        assertEquals(testInteger1, bean.get(testProperty, index), "Check Second Indexed Value is correct");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((LinkedList<?>) bean.get(testProperty)).size()), "Check Second Array length is correct");
    }

    /**
     * Test Getting/Setting an Object array 'Indexed' Property - use String[]
     */
    @Test
    public void testIndexedObjectArray() {

        int index = 3;
        final Object objectArray = new String[0];

        // Check the property & value doesn't exist
        assertNull(dynaClass.getDynaProperty(testProperty), "Check Indexed Property doesn't exist");
        assertNull(bean.get(testProperty), "Check Indexed Property is null");

        // Add a DynaProperty of type String[]
        dynaClass.add(testProperty, objectArray.getClass());
        assertEquals(objectArray.getClass(), dynaClass.getDynaProperty(testProperty).getType(), "Check Indexed Property exists");
        assertEquals(objectArray.getClass(), bean.get(testProperty).getClass(), "Check Indexed Property is correct type");

        // Set an indexed value
        bean.set(testProperty, index, testString1);
        assertNotNull(bean.get(testProperty), "Check Indexed Property is not null");
        assertEquals(objectArray.getClass(), bean.get(testProperty).getClass(), "Check Indexed Property is correct type");
        assertEquals(testString1, bean.get(testProperty, index), "Check First Indexed Value is correct(a)");
        assertEquals(testString1, ((String[]) bean.get(testProperty))[index], "Check First Indexed Value is correct(b)");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((String[]) bean.get(testProperty)).length), "Check Array length is correct");

        // Set a second indexed value, should automatically grow the String[] and set appropriate indexed value
        index += 2;
        bean.set(testProperty, index, testString2);
        assertEquals(testString2, bean.get(testProperty, index), "Check Second Indexed Value is correct(a)");
        assertEquals(testString2, ((String[]) bean.get(testProperty))[index], "Check Second Indexed Value is correct(b)");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((String[]) bean.get(testProperty)).length), "Check Second Array length is correct");
    }

    /**
     * Test Getting/Setting a primitive array 'Indexed' Property - use int[]
     */
    @Test
    public void testIndexedPrimitiveArray() {

        int index = 3;
        final int[] primitiveArray = {};

        // Check the property & value doesn't exist
        assertNull(dynaClass.getDynaProperty(testProperty), "Check Indexed Property doesn't exist");
        assertNull(bean.get(testProperty), "Check Indexed Property is null");

        // Add a DynaProperty of type int[]
        dynaClass.add(testProperty, primitiveArray.getClass());
        assertEquals(primitiveArray.getClass(), dynaClass.getDynaProperty(testProperty).getType(), "Check Indexed Property exists");
        assertEquals(primitiveArray.getClass(), bean.get(testProperty).getClass(), "Check Indexed Property is correct type");

        // Set an indexed value
        bean.set(testProperty, index, testInteger1);
        assertNotNull(bean.get(testProperty), "Check Indexed Property is not null");
        assertEquals(primitiveArray.getClass(), bean.get(testProperty).getClass(), "Check Indexed Property is correct type");
        assertEquals(testInteger1, bean.get(testProperty, index), "Check First Indexed Value is correct(a)");
        assertEquals(testInteger1, Integer.valueOf(((int[]) bean.get(testProperty))[index]), "Check First Indexed Value is correct(b)");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((int[]) bean.get(testProperty)).length), "Check Array length is correct");

        // Set a second indexed value, should automatically grow the int[] and set appropriate indexed value
        index += 2;
        bean.set(testProperty, index, testInteger2);
        assertEquals(testInteger2, bean.get(testProperty, index), "Check Second Indexed Value is correct(a)");
        assertEquals(testInteger2, Integer.valueOf(((int[]) bean.get(testProperty))[index]), "Check Second Indexed Value is correct(b)");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((int[]) bean.get(testProperty)).length), "Check Second Array length is correct");

    }

    /**
     * Test Getting/Setting an 'Indexed' Property - default ArrayList property
     */
    @Test
    public void testIndexedPropertyDefault() {

        int index = 3;

        // Check the property & value doesn't exist
        assertNull(dynaClass.getDynaProperty(testProperty), "Check Indexed Property doesn't exist");
        assertNull(bean.get(testProperty), "Check Indexed Property is null");
        assertNull(bean.get(testProperty, index), "Check Indexed value is null");

        // Set the property, should create new ArrayList and set appropriate indexed value
        bean.set(testProperty, index, testInteger1);
        assertNotNull(bean.get(testProperty), "Check Indexed Property is not null");
        assertEquals(ArrayList.class, bean.get(testProperty).getClass(), "Check Indexed Property is correct type");
        assertEquals(testInteger1, bean.get(testProperty, index), "Check First Indexed Value is correct");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((ArrayList<?>) bean.get(testProperty)).size()), "Check First Array length is correct");

        // Set a second indexed value, should automatically grow the ArrayList and set appropriate indexed value
        index += 2;
        bean.set(testProperty, index, testString1);
        assertEquals(testString1, bean.get(testProperty, index), "Check Second Indexed Value is correct");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((ArrayList<?>) bean.get(testProperty)).size()), "Check Second Array length is correct");
    }

    /**
     * Test Setting an Indexed Property when MutableDynaClass is set to restricted
     */
    @Test
    public void testIndexedPropertyRestricted() {
        final int index = 3;
        // Set the MutableDyanClass to 'restricted' (i.e. no new properties cab be added
        dynaClass.setRestricted(true);
        assertTrue(dynaClass.isRestricted(), "Check MutableDynaClass is restricted");
        // Check the property & value doesn't exist
        assertNull(dynaClass.getDynaProperty(testProperty), "Check Property doesn't exist");
        assertNull(bean.get(testProperty), "Check Value is null");
        // Set the property - should fail because property doesn't exist and MutableDynaClass is restricted
        assertThrows(IllegalArgumentException.class, () -> bean.set(testProperty, index, testInteger1));
    }

    /**
     * Test Setting an 'Indexed' Property using PropertyUtils
     */
    @Test
    public void testIndexedPropertyUtils() throws Exception {
        final int index = 3;
        dynaClass.setReturnNull(false);
        // Check the property & value doesn't exist
        assertFalse(dynaClass.isDynaProperty(testProperty), "Check Indexed Property doesn't exist");
        assertNull(bean.get(testProperty), "Check Indexed Property is null");
        assertNull(bean.get(testProperty, index), "Check Indexed value is null");
        // Use PropertyUtils to set the indexed value
        PropertyUtils.setProperty(bean, testProperty + "[" + index + "]", testString1);
        // Check property value correctly set
        assertEquals(testString1, bean.get(testProperty, index), "Check Indexed Bean Value is correct");

    }

    /**
     * Test setting mapped property for type which is not Map
     */
    @Test
    public void testMappedInvalidType() {
        dynaClass.add(testProperty, String.class);
        assertFalse(dynaClass.getDynaProperty(testProperty).isMapped(), "Check Property is not mapped");
        assertThrows(IllegalArgumentException.class, () -> bean.set(testProperty, testKey, testInteger1));
    }

    /**
     * Test Getting/Setting a 'Mapped' Property - default HashMap property
     */
    @Test
    public void testMappedPropertyDefault() {

        // Check the property & value doesn't exist
        assertNull(dynaClass.getDynaProperty(testProperty), "Check Mapped Property doesn't exist");
        assertNull(bean.get(testProperty), "Check Map is null");
        assertNull(bean.get(testProperty, testKey), "Check Mapped Value is null");

        // Set a new mapped property - should add new HashMap property and set the mapped value
        bean.set(testProperty, testKey, testInteger1);
        assertEquals(HashMap.class, bean.get(testProperty).getClass(), "Check Mapped Property exists");
        assertEquals(testInteger1, bean.get(testProperty, testKey), "Check First Mapped Value is correct(a)");
        assertEquals(testInteger1, ((HashMap<?, ?>) bean.get(testProperty)).get(testKey), "Check First Mapped Value is correct(b)");

        // Set the property again - should set the new value
        bean.set(testProperty, testKey, testInteger2);
        assertEquals(testInteger2, bean.get(testProperty, testKey), "Check Second Mapped Value is correct(a)");
        assertEquals(testInteger2, ((HashMap<?, ?>) bean.get(testProperty)).get(testKey), "Check Second Mapped Value is correct(b)");
    }

    /**
     * Test Setting a Mapped Property when MutableDynaClass is set to restricted
     */
    @Test
    public void testMappedPropertyRestricted() {
        // Set the MutableDyanClass to 'restricted' (i.e. no new properties cab be added
        dynaClass.setRestricted(true);
        assertTrue(dynaClass.isRestricted(), "Check MutableDynaClass is restricted");
        // Check the property & value doesn't exist
        assertNull(dynaClass.getDynaProperty(testProperty), "Check Property doesn't exist");
        assertNull(bean.get(testProperty), "Check Value is null");
        // Set the property - should fail because property doesn't exist and MutableDynaClass is restricted
        assertThrows(IllegalArgumentException.class, () -> bean.set(testProperty, testKey, testInteger1));
    }

    /**
     * Test Getting/Setting a 'Mapped' Property - use TreeMap property
     */
    @Test
    public void testMappedPropertyTreeMap() {
        // Check the property & value doesn't exist
        assertNull(dynaClass.getDynaProperty(testProperty), "Check Mapped Property doesn't exist");
        // Add a 'TreeMap' property to the DynaClass
        dynaClass.add(testProperty, TreeMap.class);
        assertTrue(dynaClass.getDynaProperty(testProperty).isMapped(), "Check Property is mapped");
        assertEquals(TreeMap.class, dynaClass.getDynaProperty(testProperty).getType(), "Check Property is correct type");
        assertEquals(TreeMap.class, bean.get(testProperty).getClass(), "Check Mapped Property exists");
//        assertNull("Check mapped property is null", bean.get(testProperty));
        // Set a new mapped property - should instantiate a new TreeMap property and set the mapped value
        bean.set(testProperty, testKey, testInteger1);
        assertEquals(TreeMap.class, bean.get(testProperty).getClass(), "Check Mapped Property exists");
        assertEquals(testInteger1, bean.get(testProperty, testKey), "Check First Mapped Value is correct(a)");
        assertEquals(testInteger1, ((TreeMap<?, ?>) bean.get(testProperty)).get(testKey), "Check First Mapped Value is correct(b)");
        // Set the property again - should set the new value
        bean.set(testProperty, testKey, testInteger2);
        assertEquals(testInteger2, bean.get(testProperty, testKey), "Check Second Mapped Value is correct(a)");
        assertEquals(testInteger2, ((TreeMap<?, ?>) bean.get(testProperty)).get(testKey), "Check Second Mapped Value is correct(b)");
    }

    /**
     * Test Setting a 'Mapped' Property using PropertyUtils
     */
    @Test
    public void testMappedPropertyUtils() throws Exception {

        dynaClass.setReturnNull(false);

        // Check the property & value doesn't exist
        assertFalse(dynaClass.isDynaProperty(testProperty), "Check Mapped Property doesn't exist");
        assertNull(bean.get(testProperty), "Check Map is null");
        assertNull(bean.get(testProperty, testKey), "Check Mapped Value is null");

        // Set the mapped property using PropertyUtils
        PropertyUtils.setProperty(bean, testProperty + "(" + testKey + ")", testString1);

        // Check property value correctly set
        assertEquals(testString1, bean.get(testProperty, testKey), "Check Mapped Bean Value is correct");

    }

    /**
     * Test Getting/Setting a 'null' Property
     */
    @Test
    public void testNullProperty() {

        // Check the property & value doesn't exist
        assertNull(dynaClass.getDynaProperty(testProperty), "Check Property doesn't exist");
        assertNull(bean.get(testProperty), "Check Value is null");

        // Set a new property to null
        bean.set(testProperty, null);
        assertNull(bean.get(testProperty), "Check Value is still null");

    }

    /**
     * Test Getting/Setting a Simple Property
     */
    @Test
    public void testSimpleProperty() {

        // Check the property & value doesn't exist
        assertNull(dynaClass.getDynaProperty(testProperty), "Check Property doesn't exist");
        assertNull(bean.get(testProperty), "Check Value is null");

        // Set a new property - should add new property and set value
        bean.set(testProperty, testInteger1);
        assertEquals(testInteger1, bean.get(testProperty), "Check First Value is correct");
        assertEquals(Integer.class, dynaClass.getDynaProperty(testProperty).getType(), "Check Property type is correct");

        // Set the property again - should set the new value
        bean.set(testProperty, testInteger2);
        assertEquals(testInteger2, bean.get(testProperty), "Check Second Value is correct");

        // Set the property again - with a different type, should fail
        assertThrows(ConversionException.class, () -> bean.set(testProperty, testString1));

    }

    /**
     * Test Setting a Simple Property when MutableDynaClass is set to restricted
     */
    @Test
    public void testSimplePropertyRestricted() {

        // Set the MutableDyanClass to 'restricted' (i.e. no new properties cab be added
        dynaClass.setRestricted(true);
        assertTrue(dynaClass.isRestricted(), "Check MutableDynaClass is restricted");

        // Check the property & value doesn't exist
        assertNull(dynaClass.getDynaProperty(testProperty), "Check Property doesn't exist");
        assertNull(bean.get(testProperty), "Check Value is null");

        // Set the property - should fail because property doesn't exist and MutableDynaClass is restricted
        assertThrows(IllegalArgumentException.class, () -> bean.set(testProperty, testString1));
    }

}