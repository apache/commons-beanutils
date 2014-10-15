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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p>Test Case for the <code>LazyDynaMap</code> implementation class.</p>
 *
 * @version $Id$
 */
public class LazyDynaMapTestCase extends TestCase {

    protected LazyDynaMap  dynaMap    = null;
    protected String testProperty     = "myProperty";
    protected String testPropertyA    = "myProperty-A";
    protected String testPropertyB    = "myProperty-B";
    protected String testString1      = "myStringValue-1";
    protected String testString2      = "myStringValue-2";
    protected Integer testInteger1    = new Integer(30);
    protected Integer testInteger2    = new Integer(40);
    protected String testKey          = "myKey";

    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public LazyDynaMapTestCase(final String name) {
        super(name);
    }

    // -------------------------------------------------- Overall Test Methods

    /**
     * Run thus Test
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(LazyDynaMapTestCase.class));
    }

    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {
        dynaMap = new LazyDynaMap();
        dynaMap.setReturnNull(true);
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {
      dynaMap = null;
    }

    // ------------------------------------------------ Individual Test Methods

    /**
     * General Tests
     */
    public void testGeneral() {
//        LazyDynaMap bean = new LazyDynaMap("TestBean");
        assertEquals("Check DynaClass name", "TestBean", new LazyDynaMap("TestBean").getName());

    }

    /**
     * Test Getting/Setting a Simple Property
     */
    public void testSimpleProperty() {

        // Check the property & value doesn't exist
        assertNull("Check Property doesn't exist", dynaMap.getDynaProperty(testProperty));
        assertNull("Check Value is null", dynaMap.get(testProperty));

        // Set a new property - should add new property and set value
        dynaMap.set(testProperty, testInteger1);
        assertEquals("Check First Value is correct", testInteger1, dynaMap.get(testProperty));
        assertEquals("Check Property type is correct", Integer.class, dynaMap.getDynaProperty(testProperty).getType());

        // Set the property again - should set the new value
        dynaMap.set(testProperty, testInteger2);
        assertEquals("Check Second Value is correct", testInteger2, dynaMap.get(testProperty));

        // Set the property again - with a different type, should succeed
        dynaMap.set(testProperty, testString1);
        assertEquals("Check Third Value is correct", testString1, dynaMap.get(testProperty));

    }

    /**
     * Test Setting a Simple Property when MutableDynaClass is set to restricted
     */
    public void testSimplePropertyRestricted() {

        // Set the MutableDyanClass to 'restricted' (i.e. no new properties cab be added
        dynaMap.setRestricted(true);
        assertTrue("Check MutableDynaClass is restricted", dynaMap.isRestricted());

        // Check the property & value doesn't exist
        assertNull("Check Property doesn't exist", dynaMap.getDynaProperty(testProperty));
        assertNull("Check Value is null", dynaMap.get(testProperty));

        // Set the property - should fail because property doesn't exist and MutableDynaClass is restricted
        try {
            dynaMap.set(testProperty, testString1);
            fail("expected IllegalArgumentException trying to add new property to restricted DynaClass");
        } catch (final IllegalArgumentException expected) {
            // expected result
        }

    }

    /**
     * Test Getting/Setting a 'Mapped' Property - default HashMap property
     */
    public void testMappedPropertyDefault() {

        // Check the property & value doesn't exist
        assertNull("Check Mapped Property doesn't exist", dynaMap.getDynaProperty(testProperty));
        assertNull("Check Map is null", dynaMap.get(testProperty));
        assertNull("Check Mapped Value is null", dynaMap.get(testProperty, testKey));

        // Set a new mapped property - should add new HashMap property and set the mapped value
        dynaMap.set(testProperty, testKey, testInteger1);
        assertEquals("Check Mapped Property exists", HashMap.class, dynaMap.get(testProperty).getClass());
        assertEquals("Check First Mapped Value is correct(a)", testInteger1, dynaMap.get(testProperty, testKey));
        assertEquals("Check First Mapped Value is correct(b)", testInteger1, ((HashMap<?, ?>)dynaMap.get(testProperty)).get(testKey));

        // Set the property again - should set the new value
        dynaMap.set(testProperty, testKey, testInteger2);
        assertEquals("Check Second Mapped Value is correct(a)", testInteger2, dynaMap.get(testProperty, testKey));
        assertEquals("Check Second Mapped Value is correct(b)", testInteger2, ((HashMap<?, ?>)dynaMap.get(testProperty)).get(testKey));
    }

    /**
     * Test Getting/Setting a 'Mapped' Property - use TreeMap property
     */
    public void testMappedPropertyTreeMap() {

        // Check the property & value doesn't exist
        assertNull("Check Mapped Property doesn't exist", dynaMap.getDynaProperty(testProperty));
        assertNull("Check Map is null", dynaMap.get(testProperty));

        // Add a 'TreeMap' property to the DynaClass
        dynaMap.add(testProperty, TreeMap.class);
        assertTrue("Check Property is mapped", dynaMap.getDynaProperty(testProperty).isMapped());
        assertEquals("Check Property is correct type", TreeMap.class, dynaMap.getDynaProperty(testProperty).getType());
        assertEquals("Check Mapped Property now exists", TreeMap.class, dynaMap.get(testProperty).getClass());

        // Set a new mapped property - should instatiate a new TreeMap property and set the mapped value
        dynaMap.set(testProperty, testKey, testInteger1);
        assertEquals("Check Mapped Property exists", TreeMap.class, dynaMap.get(testProperty).getClass());
        assertEquals("Check First Mapped Value is correct(a)", testInteger1, dynaMap.get(testProperty, testKey));
        assertEquals("Check First Mapped Value is correct(b)", testInteger1, ((TreeMap<?, ?>)dynaMap.get(testProperty)).get(testKey));

        // Set the property again - should set the new value
        dynaMap.set(testProperty, testKey, testInteger2);
        assertEquals("Check Second Mapped Value is correct(a)", testInteger2, dynaMap.get(testProperty, testKey));
        assertEquals("Check Second Mapped Value is correct(b)", testInteger2, ((TreeMap<?, ?>)dynaMap.get(testProperty)).get(testKey));
    }

    /**
     * Test Setting a 'Mapped' Property using PropertyUtils
     */
    public void testMappedPropertyUtils() {

        dynaMap.setReturnNull(false);

        // Check the property & value doesn't exist
        assertFalse("Check Mapped Property doesn't exist", dynaMap.isDynaProperty(testProperty));
        assertNull("Check Map is null", dynaMap.get(testProperty));
        assertNull("Check Mapped Value is null", dynaMap.get(testProperty, testKey));

        // Set the mapped property using PropertyUtils
        try {
          PropertyUtils.setProperty(dynaMap, testProperty+"("+testKey+")", testString1);
        }
        catch (final NoSuchMethodException ex) {
            fail("testIndexedPropertyUtils threw "+ex);
        }
        catch (final InvocationTargetException ex) {
            fail("testIndexedPropertyUtils threw "+ex);
        }
        catch (final IllegalAccessException ex) {
            fail("testIndexedPropertyUtils threw "+ex);
        }

        // Check property value correctly set
        assertEquals("Check Mapped Bean Value is correct", testString1, dynaMap.get(testProperty, testKey));

    }

    /**
     * Test Setting a Mapped Property when MutableDynaClass is set to restricted
     */
    public void testMappedPropertyRestricted() {

        // Set the MutableDyanClass to 'restricted' (i.e. no new properties cab be added
        dynaMap.setRestricted(true);
        assertTrue("Check MutableDynaClass is restricted", dynaMap.isRestricted());

        // Check the property & value doesn't exist
        assertNull("Check Property doesn't exist", dynaMap.getDynaProperty(testProperty));
        assertNull("Check Value is null", dynaMap.get(testProperty));

        // Set the property - should fail because property doesn't exist and MutableDynaClass is restricted
        try {
            dynaMap.set(testProperty, testKey, testInteger1);
            fail("expected IllegalArgumentException trying to add new property to restricted MutableDynaClass");
        } catch (final IllegalArgumentException expected) {
            // expected result
        }

    }

    /**
     * Test setting mapped property for type which is not Map
     */
    public void testMappedInvalidType() {
        dynaMap.set(testProperty, new Integer(1));
        assertFalse("Check Property is not mapped", dynaMap.getDynaProperty(testProperty).isMapped());
        try {
            dynaMap.set(testProperty, testKey, testInteger1);
            fail("set(property, key, value) should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected result
        }
    }

    /**
     * Test Getting/Setting an 'Indexed' Property - default ArrayList property
     */
    public void testIndexedPropertyDefault() {

        int index = 3;

        // Check the property & value doesn't exist
        assertNull("Check Indexed Property doesn't exist", dynaMap.getDynaProperty(testProperty));
        assertNull("Check Indexed Property is null", dynaMap.get(testProperty));
        assertNull("Check Indexed value is null", dynaMap.get(testProperty, index));

        // Set the property, should create new ArrayList and set appropriate indexed value
        dynaMap.set(testProperty, index, testInteger1);
        assertNotNull("Check Indexed Property is not null", dynaMap.get(testProperty));
        assertEquals("Check Indexed Property is correct type", ArrayList.class, dynaMap.get(testProperty).getClass());
        assertEquals("Check First Indexed Value is correct", testInteger1, dynaMap.get(testProperty, index));
        assertEquals("Check First Array length is correct", new Integer(index+1),  new Integer(((ArrayList<?>)dynaMap.get(testProperty)).size()));

        // Set a second indexed value, should automatically grow the ArrayList and set appropriate indexed value
        index = index + 2;
        dynaMap.set(testProperty, index, testString1);
        assertEquals("Check Second Indexed Value is correct", testString1, dynaMap.get(testProperty, index));
        assertEquals("Check Second Array length is correct", new Integer(index+1),  new Integer(((ArrayList<?>)dynaMap.get(testProperty)).size()));
    }

    /**
     * Test Getting/Setting a List 'Indexed' Property - use alternative List (LinkedList)
     */
    public void testIndexedLinkedList() {

        int   index     = 3;

        // Check the property & value doesn't exist
        assertNull("Check Indexed Property doesn't exist", dynaMap.getDynaProperty(testProperty));
        assertNull("Check Indexed Property is null", dynaMap.get(testProperty));

        // Add a 'LinkedList' property to the DynaClass - should instantiate a new LinkedList
        dynaMap.add(testProperty, LinkedList.class);
        assertTrue("Check Property is indexed", dynaMap.getDynaProperty(testProperty).isIndexed());
        assertEquals("Check Property is correct type", LinkedList.class, dynaMap.getDynaProperty(testProperty).getType());
        assertEquals("Check Indexed Property now exists", LinkedList.class, dynaMap.get(testProperty).getClass());

        // Set the Indexed property, should grow the list to the correct size
        dynaMap.set(testProperty, index, testString1);
        assertEquals("Check Property type is correct", LinkedList.class, dynaMap.get(testProperty).getClass());
        assertEquals("Check First Indexed Value is correct", testString1, dynaMap.get(testProperty, index));
        assertEquals("Check First Array length is correct", new Integer(index+1),  new Integer(((LinkedList<?>)dynaMap.get(testProperty)).size()));

        // Set a second indexed value, should automatically grow the LinkedList and set appropriate indexed value
        index = index + 2;
        dynaMap.set(testProperty, index, testInteger1);
        assertEquals("Check Second Indexed Value is correct", testInteger1, dynaMap.get(testProperty, index));
        assertEquals("Check Second Array length is correct", new Integer(index+1),  new Integer(((LinkedList<?>)dynaMap.get(testProperty)).size()));
    }

    /**
     * Test Getting/Setting a primitive array 'Indexed' Property - use int[]
     */
    public void testIndexedPrimitiveArray() {

        int   index     = 3;
        final int[] primitiveArray = new int[0];

        // Check the property & value doesn't exist
        assertNull("Check Indexed Property doesn't exist", dynaMap.getDynaProperty(testProperty));
        assertNull("Check Indexed Property is null", dynaMap.get(testProperty));

        // Add a DynaProperty of type int[]
        dynaMap.add(testProperty, primitiveArray.getClass());
        assertEquals("Check Indexed Property exists", primitiveArray.getClass(), dynaMap.getDynaProperty(testProperty).getType());
        assertTrue("Check Indexed Property exists", dynaMap.get(testProperty).getClass().isInstance(primitiveArray));

        // Set an indexed value
        dynaMap.set(testProperty, index, testInteger1);
        assertNotNull("Check Indexed Property is not null", dynaMap.get(testProperty));
        assertEquals("Check Indexed Property is correct type", primitiveArray.getClass(), dynaMap.get(testProperty).getClass());
        assertEquals("Check First Indexed Value is correct(a)", testInteger1, dynaMap.get(testProperty, index));
        assertEquals("Check First Indexed Value is correct(b)", testInteger1, new Integer(((int[])dynaMap.get(testProperty))[index]));
        assertEquals("Check Array length is correct", new Integer(index+1),  new Integer(((int[])dynaMap.get(testProperty)).length));

        // Set a second indexed value, should automatically grow the int[] and set appropriate indexed value
        index = index + 2;
        dynaMap.set(testProperty, index, testInteger2);
        assertEquals("Check Second Indexed Value is correct(a)", testInteger2, dynaMap.get(testProperty, index));
        assertEquals("Check Second Indexed Value is correct(b)", testInteger2, new Integer(((int[])dynaMap.get(testProperty))[index]));
        assertEquals("Check Second Array length is correct", new Integer(index+1),  new Integer(((int[])dynaMap.get(testProperty)).length));

    }

    /**
     * Test Getting/Setting an Object array 'Indexed' Property - use String[]
     */
    public void testIndexedObjectArray() {

        int   index     = 3;
        final Object objectArray = new String[0];

        // Check the property & value doesn't exist
        assertNull("Check Indexed Property doesn't exist", dynaMap.getDynaProperty(testProperty));
        assertNull("Check Indexed Property is null", dynaMap.get(testProperty));

        // Add a DynaProperty of type String[]
        dynaMap.add(testProperty, objectArray.getClass());
        assertEquals("Check Indexed Property exists", objectArray.getClass(), dynaMap.getDynaProperty(testProperty).getType());
        assertTrue("Check Indexed Property exists", dynaMap.get(testProperty).getClass().isInstance(objectArray));

        // Set an indexed value
        dynaMap.set(testProperty, index, testString1);
        assertNotNull("Check Indexed Property is not null", dynaMap.get(testProperty));
        assertEquals("Check Indexed Property is correct type", objectArray.getClass(), dynaMap.get(testProperty).getClass());
        assertEquals("Check First Indexed Value is correct(a)", testString1, dynaMap.get(testProperty, index));
        assertEquals("Check First Indexed Value is correct(b)", testString1, ((String[])dynaMap.get(testProperty))[index]);
        assertEquals("Check Array length is correct", new Integer(index+1),  new Integer(((String[])dynaMap.get(testProperty)).length));

        // Set a second indexed value, should automatically grow the String[] and set appropriate indexed value
        index = index + 2;
        dynaMap.set(testProperty, index, testString2);
        assertEquals("Check Second Indexed Value is correct(a)", testString2, dynaMap.get(testProperty, index));
        assertEquals("Check Second Indexed Value is correct(b)", testString2, ((String[])dynaMap.get(testProperty))[index]);
        assertEquals("Check Second Array length is correct", new Integer(index+1),  new Integer(((String[])dynaMap.get(testProperty)).length));
    }

    /**
     * Test Getting/Setting an DynaBean[] array
     */
    public void testIndexedDynaBeanArray() {

        final int   index     = 3;
        final Object objectArray = new LazyDynaBean[0];

        // Check the property & value doesn't exist
        assertNull("Check Indexed Property doesn't exist", dynaMap.getDynaProperty(testProperty));
        assertNull("Check Indexed Property is null", dynaMap.get(testProperty));

        // Add a DynaProperty of type String[]
        dynaMap.add(testProperty, objectArray.getClass());
        assertEquals("Check Indexed Property exists", objectArray.getClass(), dynaMap.getDynaProperty(testProperty).getType());
        assertEquals("Check Indexed Property is correct type", objectArray.getClass(), dynaMap.get(testProperty).getClass());

        // Retrieving from Array should initialize DynaBean
        for (int i = index; i >= 0; i--) {
            assertEquals("Check Array Components initialized", LazyDynaBean.class, dynaMap.get(testProperty, index).getClass());
        }

        dynaMap.add(testPropertyB, objectArray.getClass());
        final LazyDynaBean newBean = new LazyDynaBean();
        newBean.set(testPropertyB, testString2);
        dynaMap.set(testPropertyA, index, newBean);
        assertEquals("Check Indexed Value is correct(a)", testString2, ((DynaBean)dynaMap.get(testPropertyA, index)).get(testPropertyB));

    }

    /**
     * Test Setting an 'Indexed' Property using PropertyUtils
     */
    public void testIndexedPropertyUtils() {

        final int   index     = 3;
        dynaMap.setReturnNull(false);

        // Check the property & value doesn't exist
        assertFalse("Check Indexed Property doesn't exist", dynaMap.isDynaProperty(testProperty));
        assertNull("Check Indexed Property is null", dynaMap.get(testProperty));
        assertNull("Check Indexed value is null", dynaMap.get(testProperty, index));

        // Use PropertyUtils to set the indexed value
        try {
          PropertyUtils.setProperty(dynaMap, testProperty+"["+index+"]", testString1);
        }
        catch (final NoSuchMethodException ex) {
            fail("testIndexedPropertyUtils threw "+ex);
        }
        catch (final InvocationTargetException ex) {
            fail("testIndexedPropertyUtils threw "+ex);
        }
        catch (final IllegalAccessException ex) {
            fail("testIndexedPropertyUtils threw "+ex);
        }

        // Check property value correctly set
        assertEquals("Check Indexed Bean Value is correct", testString1, dynaMap.get(testProperty, index));

    }

    /**
     * Test Setting an Indexed Property when MutableDynaClass is set to restricted
     */
    public void testIndexedPropertyRestricted() {

        final int   index     = 3;

        // Set the MutableDyanClass to 'restricted' (i.e. no new properties cab be added
        dynaMap.setRestricted(true);
        assertTrue("Check MutableDynaClass is restricted", dynaMap.isRestricted());

        // Check the property & value doesn't exist
        assertNull("Check Property doesn't exist", dynaMap.getDynaProperty(testProperty));
        assertNull("Check Value is null", dynaMap.get(testProperty));

        // Set the property - should fail because property doesn't exist and MutableDynaClass is restricted
        try {
            dynaMap.set(testProperty, index, testInteger1);
            fail("expected IllegalArgumentException trying to add new property to restricted MutableDynaClass");
        } catch (final IllegalArgumentException expected) {
            // expected result
        }

    }

    /**
     * Test setting indexed property for type which is not List or Array
     */
    public void testIndexedInvalidType() {
        final int   index     = 3;
        dynaMap.set(testProperty, "Test String");
        assertFalse("Check Property is not indexed", dynaMap.getDynaProperty(testProperty).isIndexed());
        try {
            dynaMap.set(testProperty, index, testString1);
            fail("set(property, index, value) should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected result
        }
    }

    /**
     * Test creating using DynaClass.newInstance()
     */
    public void testNewInstance() {

        // Create LazyDynaMap using TreeMap
        // containing some properties
        final LazyDynaMap orig = new LazyDynaMap(new TreeMap<String, Object>());
        orig.set("indexProp", 0, "indexVal0");
        orig.set("indexProp", 1, "indexVal1");
        assertEquals("Index prop size", 2, ((List<?>)orig.get("indexProp")).size());

        final LazyDynaMap newOne = (LazyDynaMap)orig.newInstance();
        final Map<String, Object> newMap = newOne.getMap();
        assertEquals("Check Map type", TreeMap.class, newMap.getClass());

        final ArrayList<?> indexProp = (ArrayList<?>)newMap.get("indexProp");
        assertNotNull("Indexed Prop missing", indexProp);
        assertEquals("Index prop size", 0, indexProp.size());
    }

}