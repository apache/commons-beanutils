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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test Case for the {@code LazyDynaMap} implementation class.
 * </p>
 */
public class LazyDynaMapTestCase {

    protected LazyDynaMap dynaMap;
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
        dynaMap = new LazyDynaMap();
        dynaMap.setReturnNull(true);
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        dynaMap = null;
    }

    /**
     * General Tests
     */
    @Test
    public void testGeneral() {
//        LazyDynaMap bean = new LazyDynaMap("TestBean");
        assertEquals("TestBean", new LazyDynaMap("TestBean").getName(), "Check DynaClass name");

    }

    /**
     * Test Getting/Setting an DynaBean[] array
     */
    @Test
    public void testIndexedDynaBeanArray() {

        final int index = 3;
        final Object objectArray = new LazyDynaBean[0];

        // Check the property & value doesn't exist
        assertNull(dynaMap.getDynaProperty(testProperty), "Check Indexed Property doesn't exist");
        assertNull(dynaMap.get(testProperty), "Check Indexed Property is null");

        // Add a DynaProperty of type String[]
        dynaMap.add(testProperty, objectArray.getClass());
        assertEquals(objectArray.getClass(), dynaMap.getDynaProperty(testProperty).getType(), "Check Indexed Property exists");
        assertEquals(objectArray.getClass(), dynaMap.get(testProperty).getClass(), "Check Indexed Property is correct type");

        // Retrieving from Array should initialize DynaBean
        for (int i = index; i >= 0; i--) {
            assertEquals(LazyDynaBean.class, dynaMap.get(testProperty, index).getClass(), "Check Array Components initialized");
        }

        dynaMap.add(testPropertyB, objectArray.getClass());
        final LazyDynaBean newBean = new LazyDynaBean();
        newBean.set(testPropertyB, testString2);
        dynaMap.set(testPropertyA, index, newBean);
        assertEquals(testString2, ((DynaBean) dynaMap.get(testPropertyA, index)).get(testPropertyB), "Check Indexed Value is correct(a)");

    }

    /**
     * Test setting indexed property for type which is not List or Array
     */
    @Test
    public void testIndexedInvalidType() {
        final int index = 3;
        dynaMap.set(testProperty, "Test String");
        assertFalse(dynaMap.getDynaProperty(testProperty).isIndexed(), "Check Property is not indexed");
        assertThrows(IllegalArgumentException.class, () -> dynaMap.set(testProperty, index, testString1));
    }

    /**
     * Test Getting/Setting a List 'Indexed' Property - use alternative List (LinkedList)
     */
    @Test
    public void testIndexedLinkedList() {

        int index = 3;

        // Check the property & value doesn't exist
        assertNull(dynaMap.getDynaProperty(testProperty), "Check Indexed Property doesn't exist");
        assertNull(dynaMap.get(testProperty), "Check Indexed Property is null");

        // Add a 'LinkedList' property to the DynaClass - should instantiate a new LinkedList
        dynaMap.add(testProperty, LinkedList.class);
        assertTrue(dynaMap.getDynaProperty(testProperty).isIndexed(), "Check Property is indexed");
        assertEquals(LinkedList.class, dynaMap.getDynaProperty(testProperty).getType(), "Check Property is correct type");
        assertEquals(LinkedList.class, dynaMap.get(testProperty).getClass(), "Check Indexed Property now exists");

        // Set the Indexed property, should grow the list to the correct size
        dynaMap.set(testProperty, index, testString1);
        assertEquals(LinkedList.class, dynaMap.get(testProperty).getClass(), "Check Property type is correct");
        assertEquals(testString1, dynaMap.get(testProperty, index), "Check First Indexed Value is correct");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((LinkedList<?>) dynaMap.get(testProperty)).size()), "Check First Array length is correct");

        // Set a second indexed value, should automatically grow the LinkedList and set appropriate indexed value
        index += 2;
        dynaMap.set(testProperty, index, testInteger1);
        assertEquals(testInteger1, dynaMap.get(testProperty, index), "Check Second Indexed Value is correct");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((LinkedList<?>) dynaMap.get(testProperty)).size()), "Check Second Array length is correct");
    }

    /**
     * Test Getting/Setting an Object array 'Indexed' Property - use String[]
     */
    @Test
    public void testIndexedObjectArray() {

        int index = 3;
        final Object objectArray = new String[0];

        // Check the property & value doesn't exist
        assertNull(dynaMap.getDynaProperty(testProperty), "Check Indexed Property doesn't exist");
        assertNull(dynaMap.get(testProperty), "Check Indexed Property is null");

        // Add a DynaProperty of type String[]
        dynaMap.add(testProperty, objectArray.getClass());
        assertEquals(objectArray.getClass(), dynaMap.getDynaProperty(testProperty).getType(), "Check Indexed Property exists");
        assertTrue(dynaMap.get(testProperty).getClass().isInstance(objectArray), "Check Indexed Property exists");

        // Set an indexed value
        dynaMap.set(testProperty, index, testString1);
        assertNotNull(dynaMap.get(testProperty), "Check Indexed Property is not null");
        assertEquals(objectArray.getClass(), dynaMap.get(testProperty).getClass(), "Check Indexed Property is correct type");
        assertEquals(testString1, dynaMap.get(testProperty, index), "Check First Indexed Value is correct(a)");
        assertEquals(testString1, ((String[]) dynaMap.get(testProperty))[index], "Check First Indexed Value is correct(b)");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((String[]) dynaMap.get(testProperty)).length), "Check Array length is correct");

        // Set a second indexed value, should automatically grow the String[] and set appropriate indexed value
        index += 2;
        dynaMap.set(testProperty, index, testString2);
        assertEquals(testString2, dynaMap.get(testProperty, index), "Check Second Indexed Value is correct(a)");
        assertEquals(testString2, ((String[]) dynaMap.get(testProperty))[index], "Check Second Indexed Value is correct(b)");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((String[]) dynaMap.get(testProperty)).length), "Check Second Array length is correct");
    }

    /**
     * Test Getting/Setting a primitive array 'Indexed' Property - use int[]
     */
    @Test
    public void testIndexedPrimitiveArray() {

        int index = 3;
        final int[] primitiveArray = {};

        // Check the property & value doesn't exist
        assertNull(dynaMap.getDynaProperty(testProperty), "Check Indexed Property doesn't exist");
        assertNull(dynaMap.get(testProperty), "Check Indexed Property is null");

        // Add a DynaProperty of type int[]
        dynaMap.add(testProperty, primitiveArray.getClass());
        assertEquals(primitiveArray.getClass(), dynaMap.getDynaProperty(testProperty).getType(), "Check Indexed Property exists");
        assertTrue(dynaMap.get(testProperty).getClass().isInstance(primitiveArray), "Check Indexed Property exists");

        // Set an indexed value
        dynaMap.set(testProperty, index, testInteger1);
        assertNotNull(dynaMap.get(testProperty), "Check Indexed Property is not null");
        assertEquals(primitiveArray.getClass(), dynaMap.get(testProperty).getClass(), "Check Indexed Property is correct type");
        assertEquals(testInteger1, dynaMap.get(testProperty, index), "Check First Indexed Value is correct(a)");
        assertEquals(testInteger1, Integer.valueOf(((int[]) dynaMap.get(testProperty))[index]), "Check First Indexed Value is correct(b)");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((int[]) dynaMap.get(testProperty)).length), "Check Array length is correct");

        // Set a second indexed value, should automatically grow the int[] and set appropriate indexed value
        index += 2;
        dynaMap.set(testProperty, index, testInteger2);
        assertEquals(testInteger2, dynaMap.get(testProperty, index), "Check Second Indexed Value is correct(a)");
        assertEquals(testInteger2, Integer.valueOf(((int[]) dynaMap.get(testProperty))[index]), "Check Second Indexed Value is correct(b)");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((int[]) dynaMap.get(testProperty)).length), "Check Second Array length is correct");

    }

    /**
     * Test Getting/Setting an 'Indexed' Property - default ArrayList property
     */
    @Test
    public void testIndexedPropertyDefault() {

        int index = 3;

        // Check the property & value doesn't exist
        assertNull(dynaMap.getDynaProperty(testProperty), "Check Indexed Property doesn't exist");
        assertNull(dynaMap.get(testProperty), "Check Indexed Property is null");
        assertNull(dynaMap.get(testProperty, index), "Check Indexed value is null");

        // Set the property, should create new ArrayList and set appropriate indexed value
        dynaMap.set(testProperty, index, testInteger1);
        assertNotNull(dynaMap.get(testProperty), "Check Indexed Property is not null");
        assertEquals(ArrayList.class, dynaMap.get(testProperty).getClass(), "Check Indexed Property is correct type");
        assertEquals(testInteger1, dynaMap.get(testProperty, index), "Check First Indexed Value is correct");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((ArrayList<?>) dynaMap.get(testProperty)).size()), "Check First Array length is correct");

        // Set a second indexed value, should automatically grow the ArrayList and set appropriate indexed value
        index += 2;
        dynaMap.set(testProperty, index, testString1);
        assertEquals(testString1, dynaMap.get(testProperty, index), "Check Second Indexed Value is correct");
        assertEquals(Integer.valueOf(index + 1), Integer.valueOf(((ArrayList<?>) dynaMap.get(testProperty)).size()), "Check Second Array length is correct");
    }

    /**
     * Test Setting an Indexed Property when MutableDynaClass is set to restricted
     */
    @Test
    public void testIndexedPropertyRestricted() {

        final int index = 3;

        // Set the MutableDyanClass to 'restricted' (i.e. no new properties cab be added
        dynaMap.setRestricted(true);
        assertTrue(dynaMap.isRestricted(), "Check MutableDynaClass is restricted");

        // Check the property & value doesn't exist
        assertNull(dynaMap.getDynaProperty(testProperty), "Check Property doesn't exist");
        assertNull(dynaMap.get(testProperty), "Check Value is null");

        // Set the property - should fail because property doesn't exist and MutableDynaClass is restricted
        assertThrows(IllegalArgumentException.class, () -> dynaMap.set(testProperty, index, testInteger1));
    }

    /**
     * Test Setting an 'Indexed' Property using PropertyUtils
     */
    @Test
    public void testIndexedPropertyUtils() throws Exception {

        final int index = 3;
        dynaMap.setReturnNull(false);

        // Check the property & value doesn't exist
        assertFalse(dynaMap.isDynaProperty(testProperty), "Check Indexed Property doesn't exist");
        assertNull(dynaMap.get(testProperty), "Check Indexed Property is null");
        assertNull(dynaMap.get(testProperty, index), "Check Indexed value is null");

        PropertyUtils.setProperty(dynaMap, testProperty + "[" + index + "]", testString1);
        // Check property value correctly set
        assertEquals(testString1, dynaMap.get(testProperty, index), "Check Indexed Bean Value is correct");

    }

    /**
     * Test setting mapped property for type which is not Map
     */
    @Test
    public void testMappedInvalidType() {
        dynaMap.set(testProperty, Integer.valueOf(1));
        assertFalse(dynaMap.getDynaProperty(testProperty).isMapped(), "Check Property is not mapped");
        assertThrows(IllegalArgumentException.class, () -> dynaMap.set(testProperty, testKey, testInteger1));
    }

    /**
     * Test Getting/Setting a 'Mapped' Property - default HashMap property
     */
    @Test
    public void testMappedPropertyDefault() {

        // Check the property & value doesn't exist
        assertNull(dynaMap.getDynaProperty(testProperty), "Check Mapped Property doesn't exist");
        assertNull(dynaMap.get(testProperty), "Check Map is null");
        assertNull(dynaMap.get(testProperty, testKey), "Check Mapped Value is null");

        // Set a new mapped property - should add new HashMap property and set the mapped value
        dynaMap.set(testProperty, testKey, testInteger1);
        assertEquals(HashMap.class, dynaMap.get(testProperty).getClass(), "Check Mapped Property exists");
        assertEquals(testInteger1, dynaMap.get(testProperty, testKey), "Check First Mapped Value is correct(a)");
        assertEquals(testInteger1, ((HashMap<?, ?>) dynaMap.get(testProperty)).get(testKey), "Check First Mapped Value is correct(b)");

        // Set the property again - should set the new value
        dynaMap.set(testProperty, testKey, testInteger2);
        assertEquals(testInteger2, dynaMap.get(testProperty, testKey), "Check Second Mapped Value is correct(a)");
        assertEquals(testInteger2, ((HashMap<?, ?>) dynaMap.get(testProperty)).get(testKey), "Check Second Mapped Value is correct(b)");
    }

    /**
     * Test Setting a Mapped Property when MutableDynaClass is set to restricted
     */
    @Test
    public void testMappedPropertyRestricted() {

        // Set the MutableDyanClass to 'restricted' (i.e. no new properties cab be added
        dynaMap.setRestricted(true);
        assertTrue(dynaMap.isRestricted(), "Check MutableDynaClass is restricted");

        // Check the property & value doesn't exist
        assertNull(dynaMap.getDynaProperty(testProperty), "Check Property doesn't exist");
        assertNull(dynaMap.get(testProperty), "Check Value is null");

        // Set the property - should fail because property doesn't exist and MutableDynaClass is restricted
        assertThrows(IllegalArgumentException.class, () -> dynaMap.set(testProperty, testKey, testInteger1));
    }

    /**
     * Test Getting/Setting a 'Mapped' Property - use TreeMap property
     */
    @Test
    public void testMappedPropertyTreeMap() {

        // Check the property & value doesn't exist
        assertNull(dynaMap.getDynaProperty(testProperty), "Check Mapped Property doesn't exist");
        assertNull(dynaMap.get(testProperty), "Check Map is null");

        // Add a 'TreeMap' property to the DynaClass
        dynaMap.add(testProperty, TreeMap.class);
        assertTrue(dynaMap.getDynaProperty(testProperty).isMapped(), "Check Property is mapped");
        assertEquals(TreeMap.class, dynaMap.getDynaProperty(testProperty).getType(), "Check Property is correct type");
        assertEquals(TreeMap.class, dynaMap.get(testProperty).getClass(), "Check Mapped Property now exists");

        // Set a new mapped property - should instantiate a new TreeMap property and set the mapped value
        dynaMap.set(testProperty, testKey, testInteger1);
        assertEquals(TreeMap.class, dynaMap.get(testProperty).getClass(), "Check Mapped Property exists");
        assertEquals(testInteger1, dynaMap.get(testProperty, testKey), "Check First Mapped Value is correct(a)");
        assertEquals(testInteger1, ((TreeMap<?, ?>) dynaMap.get(testProperty)).get(testKey), "Check First Mapped Value is correct(b)");

        // Set the property again - should set the new value
        dynaMap.set(testProperty, testKey, testInteger2);
        assertEquals(testInteger2, dynaMap.get(testProperty, testKey), "Check Second Mapped Value is correct(a)");
        assertEquals(testInteger2, ((TreeMap<?, ?>) dynaMap.get(testProperty)).get(testKey), "Check Second Mapped Value is correct(b)");
    }

    /**
     * Test Setting a 'Mapped' Property using PropertyUtils
     */
    @Test
    public void testMappedPropertyUtils() throws Exception {
        dynaMap.setReturnNull(false);
        // Check the property & value doesn't exist
        assertFalse(dynaMap.isDynaProperty(testProperty), "Check Mapped Property doesn't exist");
        assertNull(dynaMap.get(testProperty), "Check Map is null");
        assertNull(dynaMap.get(testProperty, testKey), "Check Mapped Value is null");
        // Set the mapped property using PropertyUtils
        PropertyUtils.setProperty(dynaMap, testProperty + "(" + testKey + ")", testString1);
        // Check property value correctly set
        assertEquals(testString1, dynaMap.get(testProperty, testKey), "Check Mapped Bean Value is correct");
    }

    /**
     * Test creating using DynaClass.newInstance()
     */
    @Test
    public void testNewInstance() {

        // Create LazyDynaMap using TreeMap
        // containing some properties
        final LazyDynaMap orig = new LazyDynaMap(new TreeMap<>());
        orig.set("indexProp", 0, "indexVal0");
        orig.set("indexProp", 1, "indexVal1");
        assertEquals(2, ((List<?>) orig.get("indexProp")).size(), "Index prop size");

        final LazyDynaMap newOne = (LazyDynaMap) orig.newInstance();
        final Map<String, Object> newMap = newOne.getMap();
        assertEquals(TreeMap.class, newMap.getClass(), "Check Map type");

        final ArrayList<?> indexProp = (ArrayList<?>) newMap.get("indexProp");
        assertNotNull(indexProp, "Indexed Prop missing");
        assertEquals(0, indexProp.size(), "Index prop size");
    }

    /**
     * Test Getting/Setting a Simple Property
     */
    @Test
    public void testSimpleProperty() {

        // Check the property & value doesn't exist
        assertNull(dynaMap.getDynaProperty(testProperty), "Check Property doesn't exist");
        assertNull(dynaMap.get(testProperty), "Check Value is null");

        // Set a new property - should add new property and set value
        dynaMap.set(testProperty, testInteger1);
        assertEquals(testInteger1, dynaMap.get(testProperty), "Check First Value is correct");
        assertEquals(Integer.class, dynaMap.getDynaProperty(testProperty).getType(), "Check Property type is correct");

        // Set the property again - should set the new value
        dynaMap.set(testProperty, testInteger2);
        assertEquals(testInteger2, dynaMap.get(testProperty), "Check Second Value is correct");

        // Set the property again - with a different type, should succeed
        dynaMap.set(testProperty, testString1);
        assertEquals(testString1, dynaMap.get(testProperty), "Check Third Value is correct");

    }

    /**
     * Test Setting a Simple Property when MutableDynaClass is set to restricted
     */
    @Test
    public void testSimplePropertyRestricted() {
        // Set the MutableDyanClass to 'restricted' (i.e. no new properties cab be added
        dynaMap.setRestricted(true);
        assertTrue(dynaMap.isRestricted(), "Check MutableDynaClass is restricted");
        // Check the property & value doesn't exist
        assertNull(dynaMap.getDynaProperty(testProperty), "Check Property doesn't exist");
        assertNull(dynaMap.get(testProperty), "Check Value is null");
        // Set the property - should fail because property doesn't exist and MutableDynaClass is restricted
        assertThrows(IllegalArgumentException.class, () -> dynaMap.set(testProperty, testString1));
    }

}