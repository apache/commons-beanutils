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
import java.util.TreeMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p>Test Case for the <code>LazyDynaBean</code> implementation class.</p>
 *
 * @version $Id$
 */
public class LazyDynaBeanTestCase extends TestCase {

    protected LazyDynaBean  bean      = null;
    protected LazyDynaClass dynaClass = null;
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
    public LazyDynaBeanTestCase(final String name) {
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
        return (new TestSuite(LazyDynaBeanTestCase.class));
    }

    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {
        bean = new LazyDynaBean();
        dynaClass = (LazyDynaClass)bean.getDynaClass();
        dynaClass.setReturnNull(true);
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {
      bean = null;
    }

    // ------------------------------------------------ Individual Test Methods

    /**
     * Test Getting/Setting a Simple Property
     */
    public void testSimpleProperty() {

        // Check the property & value doesn't exist
        assertNull("Check Property doesn't exist", dynaClass.getDynaProperty(testProperty));
        assertNull("Check Value is null", bean.get(testProperty));

        // Set a new property - should add new property and set value
        bean.set(testProperty, testInteger1);
        assertEquals("Check First Value is correct", testInteger1, bean.get(testProperty));
        assertEquals("Check Property type is correct", Integer.class, dynaClass.getDynaProperty(testProperty).getType());

        // Set the property again - should set the new value
        bean.set(testProperty, testInteger2);
        assertEquals("Check Second Value is correct", testInteger2, bean.get(testProperty));

        // Set the property again - with a different type, should fail
        try {
            bean.set(testProperty, testString1);
            fail("expected ConversionException trying to set an Integer property to a String");
        } catch (final ConversionException expected) {
            // expected result
        }

    }

    /**
     * Test Getting/Setting a 'null' Property
     */
    public void testNullProperty() {

        // Check the property & value doesn't exist
        assertNull("Check Property doesn't exist", dynaClass.getDynaProperty(testProperty));
        assertNull("Check Value is null", bean.get(testProperty));

        // Set a new property to null
        bean.set(testProperty, null);
        assertNull("Check Value is still null", bean.get(testProperty));

    }

    /**
     * Test Setting a Simple Property when MutableDynaClass is set to restricted
     */
    public void testSimplePropertyRestricted() {

        // Set the MutableDyanClass to 'restricted' (i.e. no new properties cab be added
        dynaClass.setRestricted(true);
        assertTrue("Check MutableDynaClass is restricted", dynaClass.isRestricted());

        // Check the property & value doesn't exist
        assertNull("Check Property doesn't exist", dynaClass.getDynaProperty(testProperty));
        assertNull("Check Value is null", bean.get(testProperty));

        // Set the property - should fail because property doesn't exist and MutableDynaClass is restricted
        try {
            bean.set(testProperty, testString1);
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
        assertNull("Check Mapped Property doesn't exist", dynaClass.getDynaProperty(testProperty));
        assertNull("Check Map is null", bean.get(testProperty));
        assertNull("Check Mapped Value is null", bean.get(testProperty, testKey));

        // Set a new mapped property - should add new HashMap property and set the mapped value
        bean.set(testProperty, testKey, testInteger1);
        assertEquals("Check Mapped Property exists", HashMap.class, bean.get(testProperty).getClass());
        assertEquals("Check First Mapped Value is correct(a)", testInteger1, bean.get(testProperty, testKey));
        assertEquals("Check First Mapped Value is correct(b)", testInteger1, ((HashMap<?, ?>)bean.get(testProperty)).get(testKey));

        // Set the property again - should set the new value
        bean.set(testProperty, testKey, testInteger2);
        assertEquals("Check Second Mapped Value is correct(a)", testInteger2, bean.get(testProperty, testKey));
        assertEquals("Check Second Mapped Value is correct(b)", testInteger2, ((HashMap<?, ?>)bean.get(testProperty)).get(testKey));
    }

    /**
     * Test Getting/Setting a 'Mapped' Property - use TreeMap property
     */
    public void testMappedPropertyTreeMap() {

        // Check the property & value doesn't exist
        assertNull("Check Mapped Property doesn't exist", dynaClass.getDynaProperty(testProperty));

        // Add a 'TreeMap' property to the DynaClass
        dynaClass.add(testProperty, TreeMap.class);
        assertTrue("Check Property is mapped", dynaClass.getDynaProperty(testProperty).isMapped());
        assertEquals("Check Property is correct type", TreeMap.class, dynaClass.getDynaProperty(testProperty).getType());
        assertEquals("Check Mapped Property exists", TreeMap.class, bean.get(testProperty).getClass());
//        assertNull("Check mapped property is null", bean.get(testProperty));

        // Set a new mapped property - should instatiate a new TreeMap property and set the mapped value
        bean.set(testProperty, testKey, testInteger1);
        assertEquals("Check Mapped Property exists", TreeMap.class, bean.get(testProperty).getClass());
        assertEquals("Check First Mapped Value is correct(a)", testInteger1, bean.get(testProperty, testKey));
        assertEquals("Check First Mapped Value is correct(b)", testInteger1, ((TreeMap<?, ?>)bean.get(testProperty)).get(testKey));

        // Set the property again - should set the new value
        bean.set(testProperty, testKey, testInteger2);
        assertEquals("Check Second Mapped Value is correct(a)", testInteger2, bean.get(testProperty, testKey));
        assertEquals("Check Second Mapped Value is correct(b)", testInteger2, ((TreeMap<?, ?>)bean.get(testProperty)).get(testKey));
    }

    /**
     * Test Setting a 'Mapped' Property using PropertyUtils
     */
    public void testMappedPropertyUtils() {

        dynaClass.setReturnNull(false);

        // Check the property & value doesn't exist
        assertFalse("Check Mapped Property doesn't exist", dynaClass.isDynaProperty(testProperty));
        assertNull("Check Map is null", bean.get(testProperty));
        assertNull("Check Mapped Value is null", bean.get(testProperty, testKey));

        // Set the mapped property using PropertyUtils
        try {
          PropertyUtils.setProperty(bean, testProperty+"("+testKey+")", testString1);
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
        assertEquals("Check Mapped Bean Value is correct", testString1, bean.get(testProperty, testKey));

    }

    /**
     * Test Setting a Mapped Property when MutableDynaClass is set to restricted
     */
    public void testMappedPropertyRestricted() {

        // Set the MutableDyanClass to 'restricted' (i.e. no new properties cab be added
        dynaClass.setRestricted(true);
        assertTrue("Check MutableDynaClass is restricted", dynaClass.isRestricted());

        // Check the property & value doesn't exist
        assertNull("Check Property doesn't exist", dynaClass.getDynaProperty(testProperty));
        assertNull("Check Value is null", bean.get(testProperty));

        // Set the property - should fail because property doesn't exist and MutableDynaClass is restricted
        try {
            bean.set(testProperty, testKey, testInteger1);
            fail("expected IllegalArgumentException trying to add new property to restricted MutableDynaClass");
        } catch (final IllegalArgumentException expected) {
            // expected result
        }

    }

    /**
     * Test setting mapped property for type which is not Map
     */
    public void testMappedInvalidType() {
        dynaClass.add(testProperty, String.class);
        assertFalse("Check Property is not mapped", dynaClass.getDynaProperty(testProperty).isMapped());
        try {
            bean.set(testProperty, testKey, testInteger1);
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
        assertNull("Check Indexed Property doesn't exist", dynaClass.getDynaProperty(testProperty));
        assertNull("Check Indexed Property is null", bean.get(testProperty));
        assertNull("Check Indexed value is null", bean.get(testProperty, index));

        // Set the property, should create new ArrayList and set appropriate indexed value
        bean.set(testProperty, index, testInteger1);
        assertNotNull("Check Indexed Property is not null", bean.get(testProperty));
        assertEquals("Check Indexed Property is correct type", ArrayList.class, bean.get(testProperty).getClass());
        assertEquals("Check First Indexed Value is correct", testInteger1, bean.get(testProperty, index));
        assertEquals("Check First Array length is correct", new Integer(index+1),  new Integer(((ArrayList<?>)bean.get(testProperty)).size()));

        // Set a second indexed value, should automatically grow the ArrayList and set appropriate indexed value
        index = index + 2;
        bean.set(testProperty, index, testString1);
        assertEquals("Check Second Indexed Value is correct", testString1, bean.get(testProperty, index));
        assertEquals("Check Second Array length is correct", new Integer(index+1),  new Integer(((ArrayList<?>)bean.get(testProperty)).size()));
    }

    /**
     * Test Getting/Setting a List 'Indexed' Property - use alternative List (LinkedList)
     */
    public void testIndexedLinkedList() {

        int   index     = 3;

        // Check the property & value doesn't exist
        assertNull("Check Indexed Property doesn't exist", dynaClass.getDynaProperty(testProperty));
        assertNull("Check Indexed Property is null", bean.get(testProperty));

        // Add a 'LinkedList' property to the DynaClass
        dynaClass.add(testProperty, LinkedList.class);
        assertTrue("Check Property is indexed", dynaClass.getDynaProperty(testProperty).isIndexed());
        assertEquals("Check Property is correct type", LinkedList.class, dynaClass.getDynaProperty(testProperty).getType());
        assertEquals("Check Property type is correct", LinkedList.class, bean.get(testProperty).getClass());

        // Set the property, should instantiate a new LinkedList and set appropriate indexed value
        bean.set(testProperty, index, testString1);
        assertEquals("Check Property type is correct", LinkedList.class, bean.get(testProperty).getClass());
        assertEquals("Check First Indexed Value is correct", testString1, bean.get(testProperty, index));
        assertEquals("Check First Array length is correct", new Integer(index+1),  new Integer(((LinkedList<?>)bean.get(testProperty)).size()));

        // Set a second indexed value, should automatically grow the LinkedList and set appropriate indexed value
        index = index + 2;
        bean.set(testProperty, index, testInteger1);
        assertEquals("Check Second Indexed Value is correct", testInteger1, bean.get(testProperty, index));
        assertEquals("Check Second Array length is correct", new Integer(index+1),  new Integer(((LinkedList<?>)bean.get(testProperty)).size()));
    }

    /**
     * Test Getting/Setting a primitive array 'Indexed' Property - use int[]
     */
    public void testIndexedPrimitiveArray() {

        int   index     = 3;
        final int[] primitiveArray = new int[0];

        // Check the property & value doesn't exist
        assertNull("Check Indexed Property doesn't exist", dynaClass.getDynaProperty(testProperty));
        assertNull("Check Indexed Property is null", bean.get(testProperty));

        // Add a DynaProperty of type int[]
        dynaClass.add(testProperty, primitiveArray.getClass());
        assertEquals("Check Indexed Property exists", primitiveArray.getClass(), dynaClass.getDynaProperty(testProperty).getType());
        assertEquals("Check Indexed Property is correct type", primitiveArray.getClass(), bean.get(testProperty).getClass());

        // Set an indexed value
        bean.set(testProperty, index, testInteger1);
        assertNotNull("Check Indexed Property is not null", bean.get(testProperty));
        assertEquals("Check Indexed Property is correct type", primitiveArray.getClass(), bean.get(testProperty).getClass());
        assertEquals("Check First Indexed Value is correct(a)", testInteger1, bean.get(testProperty, index));
        assertEquals("Check First Indexed Value is correct(b)", testInteger1, new Integer(((int[])bean.get(testProperty))[index]));
        assertEquals("Check Array length is correct", new Integer(index+1),  new Integer(((int[])bean.get(testProperty)).length));

        // Set a second indexed value, should automatically grow the int[] and set appropriate indexed value
        index = index + 2;
        bean.set(testProperty, index, testInteger2);
        assertEquals("Check Second Indexed Value is correct(a)", testInteger2, bean.get(testProperty, index));
        assertEquals("Check Second Indexed Value is correct(b)", testInteger2, new Integer(((int[])bean.get(testProperty))[index]));
        assertEquals("Check Second Array length is correct", new Integer(index+1),  new Integer(((int[])bean.get(testProperty)).length));

    }

    /**
     * Test Getting/Setting an Object array 'Indexed' Property - use String[]
     */
    public void testIndexedObjectArray() {

        int   index     = 3;
        final Object objectArray = new String[0];

        // Check the property & value doesn't exist
        assertNull("Check Indexed Property doesn't exist", dynaClass.getDynaProperty(testProperty));
        assertNull("Check Indexed Property is null", bean.get(testProperty));

        // Add a DynaProperty of type String[]
        dynaClass.add(testProperty, objectArray.getClass());
        assertEquals("Check Indexed Property exists", objectArray.getClass(), dynaClass.getDynaProperty(testProperty).getType());
        assertEquals("Check Indexed Property is correct type", objectArray.getClass(), bean.get(testProperty).getClass());

        // Set an indexed value
        bean.set(testProperty, index, testString1);
        assertNotNull("Check Indexed Property is not null", bean.get(testProperty));
        assertEquals("Check Indexed Property is correct type", objectArray.getClass(), bean.get(testProperty).getClass());
        assertEquals("Check First Indexed Value is correct(a)", testString1, bean.get(testProperty, index));
        assertEquals("Check First Indexed Value is correct(b)", testString1, ((String[])bean.get(testProperty))[index]);
        assertEquals("Check Array length is correct", new Integer(index+1),  new Integer(((String[])bean.get(testProperty)).length));

        // Set a second indexed value, should automatically grow the String[] and set appropriate indexed value
        index = index + 2;
        bean.set(testProperty, index, testString2);
        assertEquals("Check Second Indexed Value is correct(a)", testString2, bean.get(testProperty, index));
        assertEquals("Check Second Indexed Value is correct(b)", testString2, ((String[])bean.get(testProperty))[index]);
        assertEquals("Check Second Array length is correct", new Integer(index+1),  new Integer(((String[])bean.get(testProperty)).length));
    }

    /**
     * Test Getting/Setting an DynaBean[] array
     */
    public void testIndexedDynaBeanArray() {

        final int   index     = 3;
        final Object objectArray = new LazyDynaMap[0];

        // Check the property & value doesn't exist
        assertNull("Check Indexed Property doesn't exist", dynaClass.getDynaProperty(testProperty));
        assertNull("Check Indexed Property is null", bean.get(testProperty));

        // Add a DynaProperty of type String[]
        dynaClass.add(testProperty, objectArray.getClass());
        assertEquals("Check Indexed Property exists", objectArray.getClass(), dynaClass.getDynaProperty(testProperty).getType());
        assertEquals("Check Indexed Property is correct type", objectArray.getClass(), bean.get(testProperty).getClass());

        // Retrieving from Array should initialize DynaBean
        for (int i = index; i >= 0; i--) {
            assertEquals("Check Array Components initialized", LazyDynaMap.class, bean.get(testProperty, index).getClass());
        }

        dynaClass.add(testPropertyB, objectArray.getClass());
        final LazyDynaMap newMap = new LazyDynaMap();
        newMap.set(testPropertyB, testString2);
        bean.set(testPropertyA, index, newMap);
        assertEquals("Check Indexed Value is correct(a)", testString2, ((DynaBean)bean.get(testPropertyA, index)).get(testPropertyB));

    }

    /**
     * Test Setting an 'Indexed' Property using PropertyUtils
     */
    public void testIndexedPropertyUtils() {

        final int   index     = 3;
        dynaClass.setReturnNull(false);

        // Check the property & value doesn't exist
        assertFalse("Check Indexed Property doesn't exist", dynaClass.isDynaProperty(testProperty));
        assertNull("Check Indexed Property is null", bean.get(testProperty));
        assertNull("Check Indexed value is null", bean.get(testProperty, index));

        // Use PropertyUtils to set the indexed value
        try {
          PropertyUtils.setProperty(bean, testProperty+"["+index+"]", testString1);
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
        assertEquals("Check Indexed Bean Value is correct", testString1, bean.get(testProperty, index));

    }

    /**
     * Test Setting an Indexed Property when MutableDynaClass is set to restricted
     */
    public void testIndexedPropertyRestricted() {

        final int   index     = 3;

        // Set the MutableDyanClass to 'restricted' (i.e. no new properties cab be added
        dynaClass.setRestricted(true);
        assertTrue("Check MutableDynaClass is restricted", dynaClass.isRestricted());

        // Check the property & value doesn't exist
        assertNull("Check Property doesn't exist", dynaClass.getDynaProperty(testProperty));
        assertNull("Check Value is null", bean.get(testProperty));

        // Set the property - should fail because property doesn't exist and MutableDynaClass is restricted
        try {
            bean.set(testProperty, index, testInteger1);
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
        dynaClass.add(testProperty, String.class);
        assertFalse("Check Property is not indexed", dynaClass.getDynaProperty(testProperty).isIndexed());
        try {
            bean.set(testProperty, index, testString1);
            fail("set(property, index, value) should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected result
        }
    }

}