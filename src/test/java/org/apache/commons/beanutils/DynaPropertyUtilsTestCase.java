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
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test accessing DynaBeans transparently via PropertyUtils.
 *
 * @version $Id$
 */

public class DynaPropertyUtilsTestCase extends TestCase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The basic test bean for each test.
     */
    protected DynaBean bean = null;


    /**
     * The set of properties that should be described.
     */
    protected String describes[] =
    { "booleanProperty",
      "booleanSecond",
      "doubleProperty",
      "floatProperty",
      "intArray",
      "intIndexed",
      "intProperty",
      "listIndexed",
      "longProperty",
      "mappedObjects",
      "mappedProperty",
      "mappedIntProperty",
      "nested",
      "nullProperty",
      //      "readOnlyProperty",
      "shortProperty",
      "stringArray",
      "stringIndexed",
      "stringProperty"
    };


    /**
     * The nested bean pointed at by the "nested" property.
     */
    protected TestBean nested = null;


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public DynaPropertyUtilsTestCase(final String name) {

        super(name);

    }


    // --------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {

        // Instantiate a new DynaBean instance
        final DynaClass dynaClass = createDynaClass();
        bean = dynaClass.newInstance();

        // Initialize the DynaBean's property values (like TestBean)
        bean.set("booleanProperty", new Boolean(true));
        bean.set("booleanSecond", new Boolean(true));
        bean.set("doubleProperty", new Double(321.0));
        bean.set("floatProperty", new Float((float) 123.0));
        final int intArray[] = { 0, 10, 20, 30, 40 };
        bean.set("intArray", intArray);
        final int intIndexed[] = { 0, 10, 20, 30, 40 };
        bean.set("intIndexed", intIndexed);
        bean.set("intProperty", new Integer(123));
        final List<String> listIndexed = new ArrayList<String>();
        listIndexed.add("String 0");
        listIndexed.add("String 1");
        listIndexed.add("String 2");
        listIndexed.add("String 3");
        listIndexed.add("String 4");
        bean.set("listIndexed", listIndexed);
        bean.set("longProperty", new Long(321));
        final HashMap<String, Object> mapProperty = new HashMap<String, Object>();
        mapProperty.put("First Key", "First Value");
        mapProperty.put("Second Key", "Second Value");
        bean.set("mapProperty", mapProperty);
        final HashMap<String, Object> mappedObjects = new HashMap<String, Object>();
        mappedObjects.put("First Key", "First Value");
        mappedObjects.put("Second Key", "Second Value");
        bean.set("mappedObjects", mappedObjects);
        final HashMap<String, Object> mappedProperty = new HashMap<String, Object>();
        mappedProperty.put("First Key", "First Value");
        mappedProperty.put("Second Key", "Second Value");
        bean.set("mappedProperty", mappedProperty);
        final HashMap<String, Integer> mappedIntProperty = new HashMap<String, Integer>();
        mappedIntProperty.put("One", new Integer(1));
        mappedIntProperty.put("Two", new Integer(2));
        bean.set("mappedIntProperty", mappedIntProperty);
        nested = new TestBean();
        bean.set("nested", nested);
        // Property "nullProperty" is not initialized, so it should return null
        bean.set("shortProperty", new Short((short) 987));
        final String stringArray[] =
                { "String 0", "String 1", "String 2", "String 3", "String 4" };
        bean.set("stringArray", stringArray);
        final String stringIndexed[] =
                { "String 0", "String 1", "String 2", "String 3", "String 4" };
        bean.set("stringIndexed", stringIndexed);
        bean.set("stringProperty", "This is a string");

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return (new TestSuite(DynaPropertyUtilsTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {

        bean = null;
        nested = null;

    }



    // ------------------------------------------------ Individual Test Methods


    /**
     * Test copyProperties() when the origin is a a <code>Map</code>.
     */
    public void testCopyPropertiesMap() {

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("booleanProperty", Boolean.FALSE);
        map.put("doubleProperty", new Double(333.0));
        map.put("dupProperty", new String[] { "New 0", "New 1", "New 2" });
        map.put("floatProperty", new Float((float) 222.0));
        map.put("intArray", new int[] { 0, 100, 200 });
        map.put("intProperty", new Integer(111));
        map.put("longProperty", new Long(444));
        map.put("shortProperty", new Short((short) 555));
        map.put("stringProperty", "New String Property");

        try {
            PropertyUtils.copyProperties(bean, map);
        } catch (final Throwable t) {
            fail("Threw " + t.toString());
        }

        // Scalar properties
        assertEquals("booleanProperty", false,
                     ((Boolean) bean.get("booleanProperty")).booleanValue());
        assertEquals("doubleProperty", 333.0,
                     ((Double) bean.get("doubleProperty")).doubleValue(),
                     0.005);
        assertEquals("floatProperty", (float) 222.0,
                     ((Float) bean.get("floatProperty")).floatValue(),
                     (float) 0.005);
        assertEquals("intProperty", 111,
                     ((Integer) bean.get("intProperty")).intValue());
        assertEquals("longProperty", 444,
                     ((Long) bean.get("longProperty")).longValue());
        assertEquals("shortProperty", (short) 555,
                     ((Short) bean.get("shortProperty")).shortValue());
        assertEquals("stringProperty", "New String Property",
                     (String) bean.get("stringProperty"));

        // Indexed Properties
        final String dupProperty[] = (String[]) bean.get("dupProperty");
        assertNotNull("dupProperty present", dupProperty);
        assertEquals("dupProperty length", 3, dupProperty.length);
        assertEquals("dupProperty[0]", "New 0", dupProperty[0]);
        assertEquals("dupProperty[1]", "New 1", dupProperty[1]);
        assertEquals("dupProperty[2]", "New 2", dupProperty[2]);
        final int intArray[] = (int[]) bean.get("intArray");
        assertNotNull("intArray present", intArray);
        assertEquals("intArray length", 3, intArray.length);
        assertEquals("intArray[0]", 0, intArray[0]);
        assertEquals("intArray[1]", 100, intArray[1]);
        assertEquals("intArray[2]", 200, intArray[2]);

    }


    /**
     * Test the describe() method.
     */
    public void testDescribe() {

        Map<String, Object> map = null;
        try {
            map = PropertyUtils.describe(bean);
        } catch (final Exception e) {
            fail("Threw exception " + e);
        }

        // Verify existence of all the properties that should be present
        for (String describe : describes) {
            assertTrue("Property '" + describe + "' is present",
                       map.containsKey(describe));
        }
        assertTrue("Property 'writeOnlyProperty' is not present",
                   !map.containsKey("writeOnlyProperty"));

        // Verify the values of scalar properties
        assertEquals("Value of 'booleanProperty'",
                     Boolean.TRUE, map.get("booleanProperty"));
        assertEquals("Value of 'doubleProperty'",
                     new Double(321.0), map.get("doubleProperty"));
        assertEquals("Value of 'floatProperty'",
                     new Float((float) 123.0), map.get("floatProperty"));
        assertEquals("Value of 'intProperty'",
                     new Integer(123), map.get("intProperty"));
        assertEquals("Value of 'longProperty'",
                     new Long(321), map.get("longProperty"));
        assertEquals("Value of 'shortProperty'",
                     new Short((short) 987), map.get("shortProperty"));
        assertEquals("Value of 'stringProperty'",
                     "This is a string",
                     (String) map.get("stringProperty"));

    }


    /**
     * Corner cases on getIndexedProperty invalid arguments.
     */
    public void testGetIndexedArguments() {

        // Use explicit index argument

        try {
            PropertyUtils.getIndexedProperty(null, "intArray", 0);
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, null, 0);
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        // Use index expression

        try {
            PropertyUtils.getIndexedProperty(null,
                    "intArray[0]");
            fail("Should throw IllegalArgumentException 3");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, "[0]");
            fail("Should throw NoSuchMethodException 4");
        } catch (final NoSuchMethodException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 4");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, "intArray");
            fail("Should throw IllegalArgumentException 5");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 5");
        }

        // Use explicit index argument

        try {
            PropertyUtils.getIndexedProperty(null, "intIndexed", 0);
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, null, 0);
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        // Use index expression

        try {
            PropertyUtils.getIndexedProperty(null,
                    "intIndexed[0]");
            fail("Should throw IllegalArgumentException 3");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, "[0]");
            fail("Should throw NoSuchMethodException 4");
        } catch (final NoSuchMethodException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 4");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, "intIndexed");
            fail("Should throw IllegalArgumentException 5");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 5");
        }

    }


    /**
     * Positive and negative tests on getIndexedProperty valid arguments.
     */
    public void testGetIndexedValues() {

        Object value = null;

        // Use explicit key argument

        for (int i = 0; i < 5; i++) {

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean, "intArray", i);
                assertNotNull("intArray returned value " + i, value);
                assertTrue("intArray returned Integer " + i,
                        value instanceof Integer);
                assertEquals("intArray returned correct " + i, i * 10,
                        ((Integer) value).intValue());
            } catch (final Throwable t) {
                fail("intArray " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean, "intIndexed", i);
                assertNotNull("intIndexed returned value " + i, value);
                assertTrue("intIndexed returned Integer " + i,
                        value instanceof Integer);
                assertEquals("intIndexed returned correct " + i, i * 10,
                        ((Integer) value).intValue());
            } catch (final Throwable t) {
                fail("intIndexed " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean, "listIndexed", i);
                assertNotNull("listIndexed returned value " + i, value);
                assertTrue("list returned String " + i,
                        value instanceof String);
                assertEquals("listIndexed returned correct " + i,
                        "String " + i, (String) value);
            } catch (final Throwable t) {
                fail("listIndexed " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean, "stringArray", i);
                assertNotNull("stringArray returned value " + i, value);
                assertTrue("stringArray returned String " + i,
                        value instanceof String);
                assertEquals("stringArray returned correct " + i,
                        "String " + i, (String) value);
            } catch (final Throwable t) {
                fail("stringArray " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean, "stringIndexed", i);
                assertNotNull("stringIndexed returned value " + i, value);
                assertTrue("stringIndexed returned String " + i,
                        value instanceof String);
                assertEquals("stringIndexed returned correct " + i,
                        "String " + i, (String) value);
            } catch (final Throwable t) {
                fail("stringIndexed " + i + " threw " + t);
            }

        }

        // Use key expression

        for (int i = 0; i < 5; i++) {

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean,
                                "intArray[" + i + "]");
                assertNotNull("intArray returned value " + i, value);
                assertTrue("intArray returned Integer " + i,
                        value instanceof Integer);
                assertEquals("intArray returned correct " + i, i * 10,
                        ((Integer) value).intValue());
            } catch (final Throwable t) {
                fail("intArray " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean,
                                "intIndexed[" + i + "]");
                assertNotNull("intIndexed returned value " + i, value);
                assertTrue("intIndexed returned Integer " + i,
                        value instanceof Integer);
                assertEquals("intIndexed returned correct " + i, i * 10,
                        ((Integer) value).intValue());
            } catch (final Throwable t) {
                fail("intIndexed " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean,
                                "listIndexed[" + i + "]");
                assertNotNull("listIndexed returned value " + i, value);
                assertTrue("listIndexed returned String " + i,
                        value instanceof String);
                assertEquals("listIndexed returned correct " + i,
                        "String " + i, (String) value);
            } catch (final Throwable t) {
                fail("listIndexed " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean,
                                "stringArray[" + i + "]");
                assertNotNull("stringArray returned value " + i, value);
                assertTrue("stringArray returned String " + i,
                        value instanceof String);
                assertEquals("stringArray returned correct " + i,
                        "String " + i, (String) value);
            } catch (final Throwable t) {
                fail("stringArray " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean,
                                "stringIndexed[" + i + "]");
                assertNotNull("stringIndexed returned value " + i, value);
                assertTrue("stringIndexed returned String " + i,
                        value instanceof String);
                assertEquals("stringIndexed returned correct " + i,
                        "String " + i, (String) value);
            } catch (final Throwable t) {
                fail("stringIndexed " + i + " threw " + t);
            }

        }

        // Index out of bounds tests

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intArray", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intArray", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intIndexed", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intIndexed", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "listIndexed", -1);
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "listIndexed", 5);
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringArray", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringArray", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringIndexed", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringIndexed", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

    }


    /**
     * Corner cases on getMappedProperty invalid arguments.
     */
    public void testGetMappedArguments() {

        // Use explicit key argument

        try {
            PropertyUtils.getMappedProperty(null, "mappedProperty",
                    "First Key");
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getMappedProperty(bean, null, "First Key");
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        try {
            PropertyUtils.getMappedProperty(bean, "mappedProperty", null);
            fail("Should throw IllegalArgumentException 3");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        // Use key expression

        try {
            PropertyUtils.getMappedProperty(null,
                    "mappedProperty(First Key)");
            fail("Should throw IllegalArgumentException 4");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 4");
        }

        try {
            PropertyUtils.getMappedProperty(bean, "(Second Key)");
            fail("Should throw IllegalArgumentException 5");
        } catch (final NoSuchMethodException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 5");
        }

        try {
            PropertyUtils.getMappedProperty(bean, "mappedProperty");
            fail("Should throw IllegalArgumentException 6");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 6");
        }

    }


    /**
     * Test getting mapped values with periods in the key.
     */
    public void testGetMappedPeriods() {

        bean.set("mappedProperty", "key.with.a.dot", "Special Value");
        assertEquals("Can retrieve directly",
                     "Special Value",
                     (String) bean.get("mappedProperty", "key.with.a.dot"));
        try {
            assertEquals("Can retrieve via getMappedProperty",
                         "Special Value",
                         PropertyUtils.getMappedProperty
                         (bean, "mappedProperty", "key.with.a.dot"));
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }
        try {
            assertEquals("Can retrieve via getNestedProperty",
                         "Special Value",
                         PropertyUtils.getNestedProperty
                         (bean, "mappedProperty(key.with.a.dot)"));
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }

        bean.set("mappedObjects", "nested.property", new TestBean());
        assertNotNull("Can retrieve directly",
                      bean.get("mappedObjects", "nested.property"));
        try {
            assertEquals("Can retrieve nested",
                         "This is a string",
                         PropertyUtils.getNestedProperty
                         (bean,
                          "mappedObjects(nested.property).stringProperty"));
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }

    }


    /**
     * Test getting mapped values with slashes in the key.  This is different
     * from periods because slashes are not syntactically significant.
     */
    public void testGetMappedSlashes() {

        bean.set("mappedProperty", "key/with/a/slash", "Special Value");
        assertEquals("Can retrieve directly",
                     "Special Value",
                     bean.get("mappedProperty", "key/with/a/slash"));
        try {
            assertEquals("Can retrieve via getMappedProperty",
                         "Special Value",
                         PropertyUtils.getMappedProperty
                         (bean, "mappedProperty", "key/with/a/slash"));
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }
        try {
            assertEquals("Can retrieve via getNestedProperty",
                         "Special Value",
                         PropertyUtils.getNestedProperty
                         (bean, "mappedProperty(key/with/a/slash)"));
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }

        bean.set("mappedObjects", "nested/property", new TestBean());
        assertNotNull("Can retrieve directly",
                      bean.get("mappedObjects", "nested/property"));
        try {
            assertEquals("Can retrieve nested",
                         "This is a string",
                         PropertyUtils.getNestedProperty
                         (bean,
                          "mappedObjects(nested/property).stringProperty"));
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }

    }


    /**
     * Positive and negative tests on getMappedProperty valid arguments.
     */
    public void testGetMappedValues() {

        Object value = null;

        // Use explicit key argument

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty",
                    "First Key");
            assertEquals("Can find first value", "First Value", value);
        } catch (final Throwable t) {
            fail("Finding first value threw " + t);
        }

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty",
                    "Second Key");
            assertEquals("Can find second value", "Second Value", value);
        } catch (final Throwable t) {
            fail("Finding second value threw " + t);
        }

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty",
                    "Third Key");
            assertNull("Can not find third value", value);
        } catch (final Throwable t) {
            fail("Finding third value threw " + t);
        }

        // Use key expression with parentheses

        try {
            value =
                    PropertyUtils.getMappedProperty(bean,
                            "mappedProperty(First Key)");
            assertEquals("Can find first value", "First Value", value);
        } catch (final Throwable t) {
            fail("Finding first value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getMappedProperty(bean,
                            "mappedProperty(Second Key)");
            assertEquals("Can find second value", "Second Value", value);
        } catch (final Throwable t) {
            fail("Finding second value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getMappedProperty(bean,
                            "mappedProperty(Third Key)");
            assertNull("Can not find third value", value);
        } catch (final Throwable t) {
            fail("Finding third value threw " + t);
        }

        // Use key expression with dotted syntax

        try {
            value =
                    PropertyUtils.getNestedProperty(bean,
                            "mapProperty.First Key");
            assertEquals("Can find first value", "First Value", value);
        } catch (final Throwable t) {
            fail("Finding first value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getNestedProperty(bean,
                            "mapProperty.Second Key");
            assertEquals("Can find second value", "Second Value", value);
        } catch (final Throwable t) {
            fail("Finding second value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getNestedProperty(bean,
                            "mapProperty.Third Key");
            assertNull("Can not find third value", value);
        } catch (final Throwable t) {
            fail("Finding third value threw " + t);
        }

    }


    /**
     * Corner cases on getNestedProperty invalid arguments.
     */
    public void testGetNestedArguments() {

        try {
            PropertyUtils.getNestedProperty(null, "stringProperty");
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getNestedProperty(bean, null);
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

    }


    /**
     * Test getNestedProperty on a boolean property.
     */
    public void testGetNestedBoolean() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.booleanProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Boolean));
            final TestBean nested = (TestBean) bean.get("nested");
            assertTrue("Got correct value",
                    ((Boolean) value).booleanValue() ==
                    nested.getBooleanProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a double property.
     */
    public void testGetNestedDouble() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.doubleProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Double));
            final TestBean nested = (TestBean) bean.get("nested");
            assertEquals("Got correct value",
                    ((Double) value).doubleValue(),
                    nested.getDoubleProperty(),
                    0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a float property.
     */
    public void testGetNestedFloat() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.floatProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Float));
            final TestBean nested = (TestBean) bean.get("nested");
            assertEquals("Got correct value",
                    ((Float) value).floatValue(),
                    nested.getFloatProperty(),
                    (float) 0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on an int property.
     */
    public void testGetNestedInt() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.intProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Integer));
            final TestBean nested = (TestBean) bean.get("nested");
            assertEquals("Got correct value",
                    ((Integer) value).intValue(),
                    nested.getIntProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a long property.
     */
    public void testGetNestedLong() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.longProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Long));
            final TestBean nested = (TestBean) bean.get("nested");
            assertEquals("Got correct value",
                    ((Long) value).longValue(),
                    nested.getLongProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a read-only String property.
     */
    public void testGetNestedReadOnly() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.readOnlyProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof String));
            final TestBean nested = (TestBean) bean.get("nested");
            assertEquals("Got correct value",
                    (String) value,
                    nested.getReadOnlyProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a short property.
     */
    public void testGetNestedShort() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.shortProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Short));
            final TestBean nested = (TestBean) bean.get("nested");
            assertEquals("Got correct value",
                    ((Short) value).shortValue(),
                    nested.getShortProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a String property.
     */
    public void testGetNestedString() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.stringProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof String));
            final TestBean nested = (TestBean) bean.get("nested");
            assertEquals("Got correct value",
                    ((String) value),
                    nested.getStringProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Negative test getNestedProperty on an unknown property.
     */
    public void testGetNestedUnknown() {

        try {
            PropertyUtils.getNestedProperty(bean, "nested.unknown");
            fail("Should have thrown NoSuchMethodException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
        }

    }


    /**
     * Corner cases on getSimpleProperty invalid arguments.
     */
    public void testGetSimpleArguments() {

        try {
            PropertyUtils.getSimpleProperty(null, "stringProperty");
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getSimpleProperty(bean, null);
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

    }


    /**
     * Test getSimpleProperty on a boolean property.
     */
    public void testGetSimpleBoolean() {

        try {
            final Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "booleanProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Boolean));
            assertTrue("Got correct value",
                    ((Boolean) value).booleanValue() == true);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a double property.
     */
    public void testGetSimpleDouble() {

        try {
            final Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "doubleProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Double));
            assertEquals("Got correct value",
                    ((Double) value).doubleValue(), 321.0, 0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a float property.
     */
    public void testGetSimpleFloat() {

        try {
            final Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "floatProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Float));
            assertEquals("Got correct value",
                    ((Float) value).floatValue(),
                    (float) 123.0,
                    (float) 0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Negative test getSimpleProperty on an indexed property.
     */
    public void testGetSimpleIndexed() {

        try {
            PropertyUtils.getSimpleProperty(bean,
                    "intIndexed[0]");
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            // Correct result for this test
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on an int property.
     */
    public void testGetSimpleInt() {

        try {
            final Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "intProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Integer));
            assertEquals("Got correct value",
                    ((Integer) value).intValue(),
                    123);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a long property.
     */
    public void testGetSimpleLong() {

        try {
            final Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "longProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Long));
            assertEquals("Got correct value",
                    ((Long) value).longValue(),
                    321);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Negative test getSimpleProperty on a nested property.
     */
    public void testGetSimpleNested() {

        try {
            PropertyUtils.getSimpleProperty(bean,
                    "nested.stringProperty");
            fail("Should have thrown IllegaArgumentException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            // Correct result for this test
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a short property.
     */
    public void testGetSimpleShort() {

        try {
            final Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "shortProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Short));
            assertEquals("Got correct value",
                    ((Short) value).shortValue(),
                    (short) 987);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a String property.
     */
    public void testGetSimpleString() {

        try {
            final Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "stringProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof String));
            assertEquals("Got correct value",
                    (String) value,
                    "This is a string");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Negative test getSimpleProperty on an unknown property.
     */
    public void testGetSimpleUnknown() {

        try {
            PropertyUtils.getSimpleProperty(bean, "unknown");
            fail("Should have thrown NoSuchMethodException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
            assertEquals("Unknown property 'unknown' on dynaclass '" +
                         bean.getDynaClass() + "'", e.getMessage() );
        }

    }


    /**
     * Corner cases on setIndexedProperty invalid arguments.
     */
    public void testSetIndexedArguments() {

        // Use explicit index argument

        try {
            PropertyUtils.setIndexedProperty(null, "intArray", 0,
                    new Integer(1));
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, null, 0,
                    new Integer(1));
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        // Use index expression

        try {
            PropertyUtils.setIndexedProperty(null,
                    "intArray[0]",
                    new Integer(1));
            fail("Should throw IllegalArgumentException 3");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "[0]",
                    new Integer(1));
            fail("Should throw NoSuchMethodException 4");
        } catch (final NoSuchMethodException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 4");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "intArray",
                    new Integer(1));
            fail("Should throw IllegalArgumentException 5");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 5");
        }

        // Use explicit index argument

        try {
            PropertyUtils.setIndexedProperty(null, "intIndexed", 0,
                    new Integer(1));
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, null, 0,
                    new Integer(1));
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        // Use index expression

        try {
            PropertyUtils.setIndexedProperty(null,
                    "intIndexed[0]",
                    new Integer(1));
            fail("Should throw IllegalArgumentException 3");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "[0]",
                    new Integer(1));
            fail("Should throw NoSuchMethodException 4");
        } catch (final NoSuchMethodException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 4");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "intIndexed",
                    new Integer(1));
            fail("Should throw IllegalArgumentException 5");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 5");
        }

    }


    /**
     * Positive and negative tests on setIndexedProperty valid arguments.
     */
    public void testSetIndexedValues() {

        Object value = null;

        // Use explicit index argument

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intArray", 0,
                    new Integer(1));
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intArray", 0);
            assertNotNull("Returned new value 0", value);
            assertTrue("Returned Integer new value 0",
                    value instanceof Integer);
            assertEquals("Returned correct new value 0", 1,
                    ((Integer) value).intValue());
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intIndexed", 1,
                    new Integer(11));
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intIndexed", 1);
            assertNotNull("Returned new value 1", value);
            assertTrue("Returned Integer new value 1",
                    value instanceof Integer);
            assertEquals("Returned correct new value 1", 11,
                    ((Integer) value).intValue());
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "listIndexed", 2,
                    "New Value 2");
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "listIndexed", 2);
            assertNotNull("Returned new value 2", value);
            assertTrue("Returned String new value 2",
                    value instanceof String);
            assertEquals("Returned correct new value 2", "New Value 2",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringArray", 2,
                    "New Value 2");
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringArray", 2);
            assertNotNull("Returned new value 2", value);
            assertTrue("Returned String new value 2",
                    value instanceof String);
            assertEquals("Returned correct new value 2", "New Value 2",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringArray", 3,
                    "New Value 3");
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringArray", 3);
            assertNotNull("Returned new value 3", value);
            assertTrue("Returned String new value 3",
                    value instanceof String);
            assertEquals("Returned correct new value 3", "New Value 3",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        // Use index expression

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intArray[4]",
                    new Integer(1));
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intArray[4]");
            assertNotNull("Returned new value 4", value);
            assertTrue("Returned Integer new value 4",
                    value instanceof Integer);
            assertEquals("Returned correct new value 4", 1,
                    ((Integer) value).intValue());
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intIndexed[3]",
                    new Integer(11));
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intIndexed[3]");
            assertNotNull("Returned new value 5", value);
            assertTrue("Returned Integer new value 5",
                    value instanceof Integer);
            assertEquals("Returned correct new value 5", 11,
                    ((Integer) value).intValue());
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "listIndexed[1]",
                    "New Value 2");
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "listIndexed[1]");
            assertNotNull("Returned new value 6", value);
            assertTrue("Returned String new value 6",
                    value instanceof String);
            assertEquals("Returned correct new value 6", "New Value 2",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringArray[1]",
                    "New Value 2");
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringArray[2]");
            assertNotNull("Returned new value 6", value);
            assertTrue("Returned String new value 6",
                    value instanceof String);
            assertEquals("Returned correct new value 6", "New Value 2",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringArray[0]",
                    "New Value 3");
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringArray[0]");
            assertNotNull("Returned new value 7", value);
            assertTrue("Returned String new value 7",
                    value instanceof String);
            assertEquals("Returned correct new value 7", "New Value 3",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        // Index out of bounds tests

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intArray", -1,
                    new Integer(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intArray", 5,
                    new Integer(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intIndexed", -1,
                    new Integer(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intIndexed", 5,
                    new Integer(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "listIndexed", 5,
                    "New String");
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "listIndexed", -1,
                    "New String");
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringArray", -1,
                    "New String");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringArray", 5,
                    "New String");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringIndexed", -1,
                    "New String");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringIndexed", 5,
                    "New String");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

    }


    /**
     * Corner cases on getMappedProperty invalid arguments.
     */
    public void testSetMappedArguments() {

        // Use explicit key argument

        try {
            PropertyUtils.setMappedProperty(null, "mappedProperty",
                    "First Key", "First Value");
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.setMappedProperty(bean, null, "First Key",
                    "First Value");
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        try {
            PropertyUtils.setMappedProperty(bean, "mappedProperty", null,
                    "First Value");
            fail("Should throw IllegalArgumentException 3");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        // Use key expression

        try {
            PropertyUtils.setMappedProperty(null,
                    "mappedProperty(First Key)",
                    "First Value");
            fail("Should throw IllegalArgumentException 4");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 4");
        }

        try {
            PropertyUtils.setMappedProperty(bean, "(Second Key)",
                    "Second Value");
            fail("Should throw IllegalArgumentException 5");
        } catch (final NoSuchMethodException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 5");
        }

        try {
            PropertyUtils.setMappedProperty(bean, "mappedProperty",
                    "Third Value");
            fail("Should throw IllegalArgumentException 6");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 6");
        }

    }


    /**
     * Positive and negative tests on setMappedProperty valid arguments.
     */
    public void testSetMappedValues() {

        Object value = null;

        // Use explicit key argument

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty",
                    "Fourth Key");
            assertNull("Can not find fourth value", value);
        } catch (final Throwable t) {
            fail("Finding fourth value threw " + t);
        }

        try {
            PropertyUtils.setMappedProperty(bean, "mappedProperty",
                    "Fourth Key", "Fourth Value");
        } catch (final Throwable t) {
            fail("Setting fourth value threw " + t);
        }

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty",
                    "Fourth Key");
            assertEquals("Can find fourth value", "Fourth Value", value);
        } catch (final Throwable t) {
            fail("Finding fourth value threw " + t);
        }

        // Use key expression with parentheses

        try {
            value =
                    PropertyUtils.getMappedProperty(bean,
                            "mappedProperty(Fifth Key)");
            assertNull("Can not find fifth value", value);
        } catch (final Throwable t) {
            fail("Finding fifth value threw " + t);
        }

        try {
            PropertyUtils.setMappedProperty(bean,
                    "mappedProperty(Fifth Key)",
                    "Fifth Value");
        } catch (final Throwable t) {
            fail("Setting fifth value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getMappedProperty(bean,
                            "mappedProperty(Fifth Key)");
            assertEquals("Can find fifth value", "Fifth Value", value);
        } catch (final Throwable t) {
            fail("Finding fifth value threw " + t);
        }

        // Use key expression with dotted expression

        try {
            value =
                    PropertyUtils.getNestedProperty(bean,
                            "mapProperty.Sixth Key");
            assertNull("Can not find sixth value", value);
        } catch (final Throwable t) {
            fail("Finding fifth value threw " + t);
        }

        try {
            PropertyUtils.setNestedProperty(bean,
                    "mapProperty.Sixth Key",
                    "Sixth Value");
        } catch (final Throwable t) {
            fail("Setting sixth value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getNestedProperty(bean,
                            "mapProperty.Sixth Key");
            assertEquals("Can find sixth value", "Sixth Value", value);
        } catch (final Throwable t) {
            fail("Finding sixth value threw " + t);
        }

    }


    /**
     * Corner cases on setNestedProperty invalid arguments.
     */
    public void testSetNestedArguments() {

        try {
            PropertyUtils.setNestedProperty(null, "stringProperty", "");
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.setNestedProperty(bean, null, "");
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

    }


    /**
     * Test setNextedProperty on a boolean property.
     */
    public void testSetNestedBoolean() {

        try {
            final boolean oldValue = nested.getBooleanProperty();
            final boolean newValue = !oldValue;
            PropertyUtils.setNestedProperty(bean,
                    "nested.booleanProperty",
                    new Boolean(newValue));
            assertTrue("Matched new value",
                    newValue ==
                    nested.getBooleanProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a double property.
     */
    public void testSetNestedDouble() {

        try {
            final double oldValue = nested.getDoubleProperty();
            final double newValue = oldValue + 1.0;
            PropertyUtils.setNestedProperty(bean,
                    "nested.doubleProperty",
                    new Double(newValue));
            assertEquals("Matched new value",
                    newValue,
                    nested.getDoubleProperty(),
                    0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a float property.
     */
    public void testSetNestedFloat() {

        try {
            final float oldValue = nested.getFloatProperty();
            final float newValue = oldValue + (float) 1.0;
            PropertyUtils.setNestedProperty(bean,
                    "nested.floatProperty",
                    new Float(newValue));
            assertEquals("Matched new value",
                    newValue,
                    nested.getFloatProperty(),
                    (float) 0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a int property.
     */
    public void testSetNestedInt() {

        try {
            final int oldValue = nested.getIntProperty();
            final int newValue = oldValue + 1;
            PropertyUtils.setNestedProperty(bean,
                    "nested.intProperty",
                    new Integer(newValue));
            assertEquals("Matched new value",
                    newValue,
                    nested.getIntProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a long property.
     */
    public void testSetNestedLong() {

        try {
            final long oldValue = nested.getLongProperty();
            final long newValue = oldValue + 1;
            PropertyUtils.setNestedProperty(bean,
                    "nested.longProperty",
                    new Long(newValue));
            assertEquals("Matched new value",
                    newValue,
                    nested.getLongProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a read-only String property.
     */
    public void testSetNestedReadOnly() {

        try {
            final String oldValue = nested.getWriteOnlyPropertyValue();
            final String newValue = oldValue + " Extra Value";
            PropertyUtils.setNestedProperty(bean,
                    "nested.readOnlyProperty",
                    newValue);
            fail("Should have thrown NoSuchMethodException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
        }

    }


    /**
     * Test setNestedProperty on a short property.
     */
    public void testSetNestedShort() {

        try {
            final short oldValue = nested.getShortProperty();
            short newValue = oldValue;
            newValue++;
            PropertyUtils.setNestedProperty(bean,
                    "nested.shortProperty",
                    new Short(newValue));
            assertEquals("Matched new value",
                    newValue,
                    nested.getShortProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a String property.
     */
    public void testSetNestedString() {

        try {
            final String oldValue = nested.getStringProperty();
            final String newValue = oldValue + " Extra Value";
            PropertyUtils.setNestedProperty(bean,
                    "nested.stringProperty",
                    newValue);
            assertEquals("Matched new value",
                    newValue,
                    nested.getStringProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on an unknown property name.
     */
    public void testSetNestedUnknown() {

        try {
            final String newValue = "New String Value";
            PropertyUtils.setNestedProperty(bean,
                    "nested.unknown",
                    newValue);
            fail("Should have thrown NoSuchMethodException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
        }

    }


    /**
     * Test setNestedProperty on a write-only String property.
     */
    public void testSetNestedWriteOnly() {

        try {
            final String oldValue = nested.getWriteOnlyPropertyValue();
            final String newValue = oldValue + " Extra Value";
            PropertyUtils.setNestedProperty(bean,
                    "nested.writeOnlyProperty",
                    newValue);
            assertEquals("Matched new value",
                    newValue,
                    nested.getWriteOnlyPropertyValue());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Corner cases on setSimpleProperty invalid arguments.
     */
    public void testSetSimpleArguments() {

        try {
            PropertyUtils.setSimpleProperty(null, "stringProperty", "");
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.setSimpleProperty(bean, null, "");
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

    }


    /**
     * Test setSimpleProperty on a boolean property.
     */
    public void testSetSimpleBoolean() {

        try {
            final boolean oldValue = ((Boolean) bean.get("booleanProperty")).booleanValue();
            final boolean newValue = !oldValue;
            PropertyUtils.setSimpleProperty(bean,
                    "booleanProperty",
                    new Boolean(newValue));
            assertTrue("Matched new value",
                    newValue ==
                    ((Boolean) bean.get("booleanProperty")).booleanValue());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a double property.
     */
    public void testSetSimpleDouble() {

        try {
            final double oldValue = ((Double) bean.get("doubleProperty")).doubleValue();
            final double newValue = oldValue + 1.0;
            PropertyUtils.setSimpleProperty(bean,
                    "doubleProperty",
                    new Double(newValue));
            assertEquals("Matched new value",
                    newValue,
                    ((Double) bean.get("doubleProperty")).doubleValue(),
                    0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a float property.
     */
    public void testSetSimpleFloat() {

        try {
            final float oldValue = ((Float) bean.get("floatProperty")).floatValue();
            final float newValue = oldValue + (float) 1.0;
            PropertyUtils.setSimpleProperty(bean,
                    "floatProperty",
                    new Float(newValue));
            assertEquals("Matched new value",
                    newValue,
                    ((Float) bean.get("floatProperty")).floatValue(),
                    (float) 0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Negative test setSimpleProperty on an indexed property.
     */
    public void testSetSimpleIndexed() {

        try {
            PropertyUtils.setSimpleProperty(bean,
                    "stringIndexed[0]",
                    "New String Value");
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            // Correct result for this test
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a int property.
     */
    public void testSetSimpleInt() {

        try {
            final int oldValue = ((Integer) bean.get("intProperty")).intValue();
            final int newValue = oldValue + 1;
            PropertyUtils.setSimpleProperty(bean,
                    "intProperty",
                    new Integer(newValue));
            assertEquals("Matched new value",
                    newValue,
                    ((Integer) bean.get("intProperty")).intValue());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a long property.
     */
    public void testSetSimpleLong() {

        try {
            final long oldValue = ((Long) bean.get("longProperty")).longValue();
            final long newValue = oldValue + 1;
            PropertyUtils.setSimpleProperty(bean,
                    "longProperty",
                    new Long(newValue));
            assertEquals("Matched new value",
                    newValue,
                    ((Long) bean.get("longProperty")).longValue());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Negative test setSimpleProperty on a nested property.
     */
    public void testSetSimpleNested() {

        try {
            PropertyUtils.setSimpleProperty(bean,
                    "nested.stringProperty",
                    "New String Value");
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            // Correct result for this test
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a short property.
     */
    public void testSetSimpleShort() {

        try {
            final short oldValue = ((Short) bean.get("shortProperty")).shortValue();
            short newValue = oldValue;
            newValue++;
            PropertyUtils.setSimpleProperty(bean,
                    "shortProperty",
                    new Short(newValue));
            assertEquals("Matched new value",
                    newValue,
                    ((Short) bean.get("shortProperty")).shortValue());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a String property.
     */
    public void testSetSimpleString() {

        try {
            final String oldValue = (String) bean.get("stringProperty");
            final String newValue = oldValue + " Extra Value";
            PropertyUtils.setSimpleProperty(bean,
                    "stringProperty",
                    newValue);
            assertEquals("Matched new value",
                    newValue,
                    (String) bean.get("stringProperty"));
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on an unknown property name.
     */
    public void testSetSimpleUnknown() {

        try {
            final String newValue = "New String Value";
            PropertyUtils.setSimpleProperty(bean,
                    "unknown",
                    newValue);
            fail("Should have thrown NoSuchMethodException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
            assertEquals("Unknown property 'unknown' on dynaclass '" +
                         bean.getDynaClass() + "'", e.getMessage() );
        }

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Create and return a <code>DynaClass</code> instance for our test
     * <code>DynaBean</code>.
     */
    protected DynaClass createDynaClass() {

        final int intArray[] = new int[0];
        final String stringArray[] = new String[0];

        final DynaClass dynaClass = new BasicDynaClass
                ("TestDynaClass", null,
                        new DynaProperty[]{
                            new DynaProperty("booleanProperty", Boolean.TYPE),
                            new DynaProperty("booleanSecond", Boolean.TYPE),
                            new DynaProperty("doubleProperty", Double.TYPE),
                            new DynaProperty("dupProperty", stringArray.getClass()),
                            new DynaProperty("floatProperty", Float.TYPE),
                            new DynaProperty("intArray", intArray.getClass()),
                            new DynaProperty("intIndexed", intArray.getClass()),
                            new DynaProperty("intProperty", Integer.TYPE),
                            new DynaProperty("listIndexed", List.class),
                            new DynaProperty("longProperty", Long.TYPE),
                            new DynaProperty("mapProperty", Map.class),
                            new DynaProperty("mappedObjects", Map.class),
                            new DynaProperty("mappedProperty", Map.class),
                            new DynaProperty("mappedIntProperty", Map.class),
                            new DynaProperty("nested", TestBean.class),
                            new DynaProperty("nullProperty", String.class),
                            new DynaProperty("shortProperty", Short.TYPE),
                            new DynaProperty("stringArray", stringArray.getClass()),
                            new DynaProperty("stringIndexed", stringArray.getClass()),
                            new DynaProperty("stringProperty", String.class),
                        });
        return (dynaClass);

    }


}
