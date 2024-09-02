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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test accessing DynaBeans transparently via PropertyUtils.
 */
public class DynaPropertyUtilsTestCase {

    /**
     * The basic test bean for each test.
     */
    protected DynaBean bean;

    /**
     * The set of properties that should be described.
     */
    protected String[] describes = { "booleanProperty", "booleanSecond", "doubleProperty", "floatProperty", "intArray", "intIndexed", "intProperty",
            "listIndexed", "longProperty", "mappedObjects", "mappedProperty", "mappedIntProperty", "nested", "nullProperty",
            // "readOnlyProperty",
            "shortProperty", "stringArray", "stringIndexed", "stringProperty" };

    /**
     * The nested bean pointed at by the "nested" property.
     */
    protected TestBean nested;

    /**
     * Create and return a {@code DynaClass} instance for our test {@code DynaBean}.
     */
    protected DynaClass createDynaClass() {
        final int[] intArray = {};
        final String[] stringArray = {};
        final DynaClass dynaClass = new BasicDynaClass("TestDynaClass", null, new DynaProperty[] { new DynaProperty("booleanProperty", Boolean.TYPE),
                new DynaProperty("booleanSecond", Boolean.TYPE), new DynaProperty("doubleProperty", Double.TYPE),
                new DynaProperty("dupProperty", stringArray.getClass()), new DynaProperty("floatProperty", Float.TYPE),
                new DynaProperty("intArray", intArray.getClass()), new DynaProperty("intIndexed", intArray.getClass()),
                new DynaProperty("intProperty", Integer.TYPE), new DynaProperty("listIndexed", List.class), new DynaProperty("longProperty", Long.TYPE),
                new DynaProperty("mapProperty", Map.class), new DynaProperty("mappedObjects", Map.class), new DynaProperty("mappedProperty", Map.class),
                new DynaProperty("mappedIntProperty", Map.class), new DynaProperty("nested", TestBean.class), new DynaProperty("nullProperty", String.class),
                new DynaProperty("shortProperty", Short.TYPE), new DynaProperty("stringArray", stringArray.getClass()),
                new DynaProperty("stringIndexed", stringArray.getClass()), new DynaProperty("stringProperty", String.class), });
        return dynaClass;
    }

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() throws Exception {
        // Instantiate a new DynaBean instance
        final DynaClass dynaClass = createDynaClass();
        bean = dynaClass.newInstance();
        // Initialize the DynaBean's property values (like TestBean)
        bean.set("booleanProperty", Boolean.valueOf(true));
        bean.set("booleanSecond", Boolean.valueOf(true));
        bean.set("doubleProperty", Double.valueOf(321.0));
        bean.set("floatProperty", Float.valueOf((float) 123.0));
        final int[] intArray = { 0, 10, 20, 30, 40 };
        bean.set("intArray", intArray);
        final int[] intIndexed = { 0, 10, 20, 30, 40 };
        bean.set("intIndexed", intIndexed);
        bean.set("intProperty", Integer.valueOf(123));
        final List<String> listIndexed = new ArrayList<>();
        listIndexed.add("String 0");
        listIndexed.add("String 1");
        listIndexed.add("String 2");
        listIndexed.add("String 3");
        listIndexed.add("String 4");
        bean.set("listIndexed", listIndexed);
        bean.set("longProperty", Long.valueOf(321));
        final HashMap<String, Object> mapProperty = new HashMap<>();
        mapProperty.put("First Key", "First Value");
        mapProperty.put("Second Key", "Second Value");
        bean.set("mapProperty", mapProperty);
        final HashMap<String, Object> mappedObjects = new HashMap<>();
        mappedObjects.put("First Key", "First Value");
        mappedObjects.put("Second Key", "Second Value");
        bean.set("mappedObjects", mappedObjects);
        final HashMap<String, Object> mappedProperty = new HashMap<>();
        mappedProperty.put("First Key", "First Value");
        mappedProperty.put("Second Key", "Second Value");
        bean.set("mappedProperty", mappedProperty);
        final HashMap<String, Integer> mappedIntProperty = new HashMap<>();
        mappedIntProperty.put("One", Integer.valueOf(1));
        mappedIntProperty.put("Two", Integer.valueOf(2));
        bean.set("mappedIntProperty", mappedIntProperty);
        nested = new TestBean();
        bean.set("nested", nested);
        // Property "nullProperty" is not initialized, so it should return null
        bean.set("shortProperty", Short.valueOf((short) 987));
        final String[] stringArray = { "String 0", "String 1", "String 2", "String 3", "String 4" };
        bean.set("stringArray", stringArray);
        final String[] stringIndexed = { "String 0", "String 1", "String 2", "String 3", "String 4" };
        bean.set("stringIndexed", stringIndexed);
        bean.set("stringProperty", "This is a string");

    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        bean = null;
        nested = null;
    }

    /**
     * Test copyProperties() when the origin is a {@code Map}.
     */
    @Test
    public void testCopyPropertiesMap() {
        final Map<String, Object> map = new HashMap<>();
        map.put("booleanProperty", Boolean.FALSE);
        map.put("doubleProperty", Double.valueOf(333.0));
        map.put("dupProperty", new String[] { "New 0", "New 1", "New 2" });
        map.put("floatProperty", Float.valueOf((float) 222.0));
        map.put("intArray", new int[] { 0, 100, 200 });
        map.put("intProperty", Integer.valueOf(111));
        map.put("longProperty", Long.valueOf(444));
        map.put("shortProperty", Short.valueOf((short) 555));
        map.put("stringProperty", "New String Property");
        try {
            PropertyUtils.copyProperties(bean, map);
        } catch (final Throwable t) {
            fail("Threw " + t.toString());
        }

        // Scalar properties
        assertEquals(false, ((Boolean) bean.get("booleanProperty")).booleanValue(), "booleanProperty");
        assertEquals(333.0, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005, "doubleProperty");
        assertEquals((float) 222.0, ((Float) bean.get("floatProperty")).floatValue(), (float) 0.005, "floatProperty");
        assertEquals(111, ((Integer) bean.get("intProperty")).intValue(), "intProperty");
        assertEquals(444, ((Long) bean.get("longProperty")).longValue(), "longProperty");
        assertEquals((short) 555, ((Short) bean.get("shortProperty")).shortValue(), "shortProperty");
        assertEquals("New String Property", (String) bean.get("stringProperty"), "stringProperty");

        // Indexed Properties
        final String[] dupProperty = (String[]) bean.get("dupProperty");
        assertNotNull(dupProperty, "dupProperty present");
        assertEquals(3, dupProperty.length, "dupProperty length");
        assertEquals("New 0", dupProperty[0], "dupProperty[0]");
        assertEquals("New 1", dupProperty[1], "dupProperty[1]");
        assertEquals("New 2", dupProperty[2], "dupProperty[2]");
        final int[] intArray = (int[]) bean.get("intArray");
        assertNotNull(intArray, "intArray present");
        assertEquals(3, intArray.length, "intArray length");
        assertEquals(0, intArray[0], "intArray[0]");
        assertEquals(100, intArray[1], "intArray[1]");
        assertEquals(200, intArray[2], "intArray[2]");

    }

    /**
     * Test the describe() method.
     */
    @Test
    public void testDescribe() {

        Map<String, Object> map = null;
        try {
            map = PropertyUtils.describe(bean);
        } catch (final Exception e) {
            fail("Threw exception " + e);
        }

        // Verify existence of all the properties that should be present
        for (final String describe : describes) {
            assertTrue(map.containsKey(describe), "Property '" + describe + "' is present");
        }
        assertFalse(map.containsKey("writeOnlyProperty"), "Property 'writeOnlyProperty' is not present");

        // Verify the values of scalar properties
        assertEquals(Boolean.TRUE, map.get("booleanProperty"), "Value of 'booleanProperty'");
        assertEquals(Double.valueOf(321.0), map.get("doubleProperty"), "Value of 'doubleProperty'");
        assertEquals(Float.valueOf((float) 123.0), map.get("floatProperty"), "Value of 'floatProperty'");
        assertEquals(Integer.valueOf(123), map.get("intProperty"), "Value of 'intProperty'");
        assertEquals(Long.valueOf(321), map.get("longProperty"), "Value of 'longProperty'");
        assertEquals(Short.valueOf((short) 987), map.get("shortProperty"), "Value of 'shortProperty'");
        assertEquals("This is a string", (String) map.get("stringProperty"), "Value of 'stringProperty'");

    }

    /**
     * Corner cases on getIndexedProperty invalid arguments.
     */
    @Test
    public void testGetIndexedArguments() {
        // Use explicit index argument
        assertThrows(NullPointerException.class, () -> PropertyUtils.getIndexedProperty(null, "intArray", 0));
        assertThrows(NullPointerException.class, () -> PropertyUtils.getIndexedProperty(bean, null, 0));
        // Use index expression
        assertThrows(NullPointerException.class, () -> PropertyUtils.getIndexedProperty(null, "intArray[0]"));
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.getIndexedProperty(bean, "[0]"));
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getIndexedProperty(bean, "intArray"));
        // Use explicit index argument
        assertThrows(NullPointerException.class, () -> PropertyUtils.getIndexedProperty(null, "intIndexed", 0));
        assertThrows(NullPointerException.class, () -> PropertyUtils.getIndexedProperty(bean, null, 0));
        // Use index expression
        assertThrows(NullPointerException.class, () -> PropertyUtils.getIndexedProperty(null, "intIndexed[0]"));
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.getIndexedProperty(bean, "[0]"));
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getIndexedProperty(bean, "intIndexed"));
    }

    /**
     * Positive and negative tests on getIndexedProperty valid arguments.
     */
    @Test
    public void testGetIndexedValues() {

        Object value = null;

        // Use explicit key argument

        for (int i = 0; i < 5; i++) {

            try {
                value = PropertyUtils.getIndexedProperty(bean, "intArray", i);
                assertNotNull(value, "intArray returned value " + i);
                assertInstanceOf(Integer.class, value, "intArray returned Integer " + i);
                assertEquals(i * 10, ((Integer) value).intValue(), "intArray returned correct " + i);
            } catch (final Throwable t) {
                fail("intArray " + i + " threw " + t);
            }

            try {
                value = PropertyUtils.getIndexedProperty(bean, "intIndexed", i);
                assertNotNull(value, "intIndexed returned value " + i);
                assertInstanceOf(Integer.class, value, "intIndexed returned Integer " + i);
                assertEquals(i * 10, ((Integer) value).intValue(), "intIndexed returned correct " + i);
            } catch (final Throwable t) {
                fail("intIndexed " + i + " threw " + t);
            }

            try {
                value = PropertyUtils.getIndexedProperty(bean, "listIndexed", i);
                assertNotNull(value, "listIndexed returned value " + i);
                assertInstanceOf(String.class, value, "list returned String " + i);
                assertEquals("String " + i, (String) value, "listIndexed returned correct " + i);
            } catch (final Throwable t) {
                fail("listIndexed " + i + " threw " + t);
            }

            try {
                value = PropertyUtils.getIndexedProperty(bean, "stringArray", i);
                assertNotNull(value, "stringArray returned value " + i);
                assertInstanceOf(String.class, value, "stringArray returned String " + i);
                assertEquals("String " + i, (String) value, "stringArray returned correct " + i);
            } catch (final Throwable t) {
                fail("stringArray " + i + " threw " + t);
            }

            try {
                value = PropertyUtils.getIndexedProperty(bean, "stringIndexed", i);
                assertNotNull(value, "stringIndexed returned value " + i);
                assertInstanceOf(String.class, value, "stringIndexed returned String " + i);
                assertEquals("String " + i, (String) value, "stringIndexed returned correct " + i);
            } catch (final Throwable t) {
                fail("stringIndexed " + i + " threw " + t);
            }

        }

        // Use key expression

        for (int i = 0; i < 5; i++) {

            try {
                value = PropertyUtils.getIndexedProperty(bean, "intArray[" + i + "]");
                assertNotNull(value, "intArray returned value " + i);
                assertInstanceOf(Integer.class, value, "intArray returned Integer " + i);
                assertEquals(i * 10, ((Integer) value).intValue(), "intArray returned correct " + i);
            } catch (final Throwable t) {
                fail("intArray " + i + " threw " + t);
            }

            try {
                value = PropertyUtils.getIndexedProperty(bean, "intIndexed[" + i + "]");
                assertNotNull(value, "intIndexed returned value " + i);
                assertInstanceOf(Integer.class, value, "intIndexed returned Integer " + i);
                assertEquals(i * 10, ((Integer) value).intValue(), "intIndexed returned correct " + i);
            } catch (final Throwable t) {
                fail("intIndexed " + i + " threw " + t);
            }

            try {
                value = PropertyUtils.getIndexedProperty(bean, "listIndexed[" + i + "]");
                assertNotNull(value, "listIndexed returned value " + i);
                assertInstanceOf(String.class, value, "listIndexed returned String " + i);
                assertEquals("String " + i, (String) value, "listIndexed returned correct " + i);
            } catch (final Throwable t) {
                fail("listIndexed " + i + " threw " + t);
            }

            try {
                value = PropertyUtils.getIndexedProperty(bean, "stringArray[" + i + "]");
                assertNotNull(value, "stringArray returned value " + i);
                assertInstanceOf(String.class, value, "stringArray returned String " + i);
                assertEquals("String " + i, (String) value, "stringArray returned correct " + i);
            } catch (final Throwable t) {
                fail("stringArray " + i + " threw " + t);
            }

            try {
                value = PropertyUtils.getIndexedProperty(bean, "stringIndexed[" + i + "]");
                assertNotNull(value, "stringIndexed returned value " + i);
                assertInstanceOf(String.class, value, "stringIndexed returned String " + i);
                assertEquals("String " + i, (String) value, "stringIndexed returned correct " + i);
            } catch (final Throwable t) {
                fail("stringIndexed " + i + " threw " + t);
            }

        }

        // Index out of bounds tests

        try {
            value = PropertyUtils.getIndexedProperty(bean, "intArray", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value = PropertyUtils.getIndexedProperty(bean, "intArray", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value = PropertyUtils.getIndexedProperty(bean, "intIndexed", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value = PropertyUtils.getIndexedProperty(bean, "intIndexed", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value = PropertyUtils.getIndexedProperty(bean, "listIndexed", -1);
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            value = PropertyUtils.getIndexedProperty(bean, "listIndexed", 5);
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            value = PropertyUtils.getIndexedProperty(bean, "stringArray", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value = PropertyUtils.getIndexedProperty(bean, "stringArray", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value = PropertyUtils.getIndexedProperty(bean, "stringIndexed", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value = PropertyUtils.getIndexedProperty(bean, "stringIndexed", 5);
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
    @Test
    public void testGetMappedArguments() {
        // Use explicit key argument
        assertThrows(NullPointerException.class, () -> PropertyUtils.getMappedProperty(null, "mappedProperty", "First Key"));
        assertThrows(NullPointerException.class, () -> PropertyUtils.getMappedProperty(bean, null, "First Key"));
        assertThrows(NullPointerException.class, () -> PropertyUtils.getMappedProperty(bean, "mappedProperty", null));
        // Use key expression
        assertThrows(NullPointerException.class, () -> PropertyUtils.getMappedProperty(null, "mappedProperty(First Key)"));
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.getMappedProperty(bean, "(Second Key)"));
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getMappedProperty(bean, "mappedProperty"));

    }

    /**
     * Test getting mapped values with periods in the key.
     */
    @Test
    public void testGetMappedPeriods() {

        bean.set("mappedProperty", "key.with.a.dot", "Special Value");
        assertEquals("Special Value", (String) bean.get("mappedProperty", "key.with.a.dot"), "Can retrieve directly");
        try {
            assertEquals("Special Value", PropertyUtils.getMappedProperty(bean, "mappedProperty", "key.with.a.dot"), "Can retrieve via getMappedProperty");
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }
        try {
            assertEquals("Special Value", PropertyUtils.getNestedProperty(bean, "mappedProperty(key.with.a.dot)"), "Can retrieve via getNestedProperty");
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }

        bean.set("mappedObjects", "nested.property", new TestBean());
        assertNotNull(bean.get("mappedObjects", "nested.property"), "Can retrieve directly");
        try {
            assertEquals("This is a string", PropertyUtils.getNestedProperty(bean, "mappedObjects(nested.property).stringProperty"), "Can retrieve nested");
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }

    }

    /**
     * Test getting mapped values with slashes in the key. This is different from periods because slashes are not syntactically significant.
     */
    @Test
    public void testGetMappedSlashes() {

        bean.set("mappedProperty", "key/with/a/slash", "Special Value");
        assertEquals("Special Value", bean.get("mappedProperty", "key/with/a/slash"), "Can retrieve directly");
        try {
            assertEquals("Special Value", PropertyUtils.getMappedProperty(bean, "mappedProperty", "key/with/a/slash"), "Can retrieve via getMappedProperty");
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }
        try {
            assertEquals("Special Value", PropertyUtils.getNestedProperty(bean, "mappedProperty(key/with/a/slash)"), "Can retrieve via getNestedProperty");
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }

        bean.set("mappedObjects", "nested/property", new TestBean());
        assertNotNull(bean.get("mappedObjects", "nested/property"), "Can retrieve directly");
        try {
            assertEquals("This is a string", PropertyUtils.getNestedProperty(bean, "mappedObjects(nested/property).stringProperty"), "Can retrieve nested");
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }

    }

    /**
     * Positive and negative tests on getMappedProperty valid arguments.
     */
    @Test
    public void testGetMappedValues() {

        Object value = null;

        // Use explicit key argument

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty", "First Key");
            assertEquals("First Value", value, "Can find first value");
        } catch (final Throwable t) {
            fail("Finding first value threw " + t);
        }

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty", "Second Key");
            assertEquals("Second Value", value, "Can find second value");
        } catch (final Throwable t) {
            fail("Finding second value threw " + t);
        }

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty", "Third Key");
            assertNull(value, "Can not find third value");
        } catch (final Throwable t) {
            fail("Finding third value threw " + t);
        }

        // Use key expression with parentheses

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty(First Key)");
            assertEquals("First Value", value, "Can find first value");
        } catch (final Throwable t) {
            fail("Finding first value threw " + t);
        }

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty(Second Key)");
            assertEquals("Second Value", value, "Can find second value");
        } catch (final Throwable t) {
            fail("Finding second value threw " + t);
        }

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty(Third Key)");
            assertNull(value, "Can not find third value");
        } catch (final Throwable t) {
            fail("Finding third value threw " + t);
        }

        // Use key expression with dotted syntax

        try {
            value = PropertyUtils.getNestedProperty(bean, "mapProperty.First Key");
            assertEquals("First Value", value, "Can find first value");
        } catch (final Throwable t) {
            fail("Finding first value threw " + t);
        }

        try {
            value = PropertyUtils.getNestedProperty(bean, "mapProperty.Second Key");
            assertEquals("Second Value", value, "Can find second value");
        } catch (final Throwable t) {
            fail("Finding second value threw " + t);
        }

        try {
            value = PropertyUtils.getNestedProperty(bean, "mapProperty.Third Key");
            assertNull(value, "Can not find third value");
        } catch (final Throwable t) {
            fail("Finding third value threw " + t);
        }

    }

    /**
     * Corner cases on getNestedProperty invalid arguments.
     */
    @Test
    public void testGetNestedArguments() {
        assertThrows(NullPointerException.class, () -> PropertyUtils.getNestedProperty(null, "stringProperty"));
        assertThrows(NullPointerException.class, () -> PropertyUtils.getNestedProperty(bean, null));
    }

    /**
     * Test getNestedProperty on a boolean property.
     */
    @Test
    public void testGetNestedBoolean() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.booleanProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Boolean.class, value, "Got correct type");
        final TestBean nested = (TestBean) bean.get("nested");
        assertEquals(((Boolean) value).booleanValue(), nested.getBooleanProperty(), "Got correct value");
    }

    /**
     * Test getNestedProperty on a double property.
     */
    @Test
    public void testGetNestedDouble() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.doubleProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Double.class, value, "Got correct type");
        final TestBean nested = (TestBean) bean.get("nested");
        assertEquals(((Double) value).doubleValue(), nested.getDoubleProperty(), 0.005, "Got correct value");
    }

    /**
     * Test getNestedProperty on a float property.
     */
    @Test
    public void testGetNestedFloat() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.floatProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Float.class, value, "Got correct type");
        final TestBean nested = (TestBean) bean.get("nested");
        assertEquals(((Float) value).floatValue(), nested.getFloatProperty(), (float) 0.005, "Got correct value");
    }

    /**
     * Test getNestedProperty on an int property.
     */
    @Test
    public void testGetNestedInt() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.intProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Integer.class, value, "Got correct type");
        final TestBean nested = (TestBean) bean.get("nested");
        assertEquals(((Integer) value).intValue(), nested.getIntProperty(), "Got correct value");
    }

    /**
     * Test getNestedProperty on a long property.
     */
    @Test
    public void testGetNestedLong() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.longProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Long.class, value, "Got correct type");
        final TestBean nested = (TestBean) bean.get("nested");
        assertEquals(((Long) value).longValue(), nested.getLongProperty(), "Got correct value");
    }

    /**
     * Test getNestedProperty on a read-only String property.
     */
    @Test
    public void testGetNestedReadOnly() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.readOnlyProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(String.class, value, "Got correct type");
        final TestBean nested = (TestBean) bean.get("nested");
        assertEquals((String) value, nested.getReadOnlyProperty(), "Got correct value");
    }

    /**
     * Test getNestedProperty on a short property.
     */
    @Test
    public void testGetNestedShort() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.shortProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Short.class, value, "Got correct type");
        final TestBean nested = (TestBean) bean.get("nested");
        assertEquals(((Short) value).shortValue(), nested.getShortProperty(), "Got correct value");
    }

    /**
     * Test getNestedProperty on a String property.
     */
    @Test
    public void testGetNestedString() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.stringProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(String.class, value, "Got correct type");
        final TestBean nested = (TestBean) bean.get("nested");
        assertEquals((String) value, nested.getStringProperty(), "Got correct value");
    }

    /**
     * Negative test getNestedProperty on an unknown property.
     */
    @Test
    public void testGetNestedUnknown() throws Exception {
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.getNestedProperty(bean, "nested.unknown"));
    }

    /**
     * Corner cases on getSimpleProperty invalid arguments.
     */
    @Test
    public void testGetSimpleArguments() {
        assertThrows(NullPointerException.class, () -> PropertyUtils.getSimpleProperty(null, "stringProperty"));
        assertThrows(NullPointerException.class, () -> PropertyUtils.getSimpleProperty(bean, null));
    }

    /**
     * Test getSimpleProperty on a boolean property.
     */
    @Test
    public void testGetSimpleBoolean() throws Exception {
        final Object value = PropertyUtils.getSimpleProperty(bean, "booleanProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Boolean.class, value, "Got correct type");
        assertTrue(((Boolean) value).booleanValue(), "Got correct value");
    }

    /**
     * Test getSimpleProperty on a double property.
     */
    @Test
    public void testGetSimpleDouble() throws Exception {
        final Object value = PropertyUtils.getSimpleProperty(bean, "doubleProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Double.class, value, "Got correct type");
        assertEquals(((Double) value).doubleValue(), 321.0, 0.005, "Got correct value");
    }

    /**
     * Test getSimpleProperty on a float property.
     */
    @Test
    public void testGetSimpleFloat() throws Exception {
        final Object value = PropertyUtils.getSimpleProperty(bean, "floatProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Float.class, value, "Got correct type");
        assertEquals(((Float) value).floatValue(), (float) 123.0, (float) 0.005, "Got correct value");
    }

    /**
     * Negative test getSimpleProperty on an indexed property.
     */
    @Test
    public void testGetSimpleIndexed() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getSimpleProperty(bean, "intIndexed[0]"));
    }

    /**
     * Test getSimpleProperty on an int property.
     */
    @Test
    public void testGetSimpleInt() throws Exception {
        final Object value = PropertyUtils.getSimpleProperty(bean, "intProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Integer.class, value, "Got correct type");
        assertEquals(((Integer) value).intValue(), 123, "Got correct value");
    }

    /**
     * Test getSimpleProperty on a long property.
     */
    @Test
    public void testGetSimpleLong() throws Exception {
        final Object value = PropertyUtils.getSimpleProperty(bean, "longProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Long.class, value, "Got correct type");
        assertEquals(((Long) value).longValue(), 321, "Got correct value");
    }

    /**
     * Negative test getSimpleProperty on a nested property.
     */
    @Test
    public void testGetSimpleNested() throws Exception {
        try {
            PropertyUtils.getSimpleProperty(bean, "nested.stringProperty");
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // Correct result for this test
        }

    }

    /**
     * Test getSimpleProperty on a short property.
     */
    @Test
    public void testGetSimpleShort() throws Exception {
        final Object value = PropertyUtils.getSimpleProperty(bean, "shortProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Short.class, value, "Got correct type");
        assertEquals(((Short) value).shortValue(), (short) 987, "Got correct value");
    }

    /**
     * Test getSimpleProperty on a String property.
     */
    @Test
    public void testGetSimpleString() throws Exception {
        final Object value = PropertyUtils.getSimpleProperty(bean, "stringProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(String.class, value, "Got correct type");
        assertEquals((String) value, "This is a string", "Got correct value");
    }

    /**
     * Negative test getSimpleProperty on an unknown property.
     */
    @Test
    public void testGetSimpleUnknown() throws Exception {
        try {
            PropertyUtils.getSimpleProperty(bean, "unknown");
            fail("Should have thrown NoSuchMethodException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
            assertEquals("Unknown property 'unknown' on dynaclass '" + bean.getDynaClass() + "'", e.getMessage());
        }

    }

    /**
     * Corner cases on setIndexedProperty invalid arguments.
     */
    @Test
    public void testSetIndexedArguments() {
        // Use explicit index argument
        assertThrows(NullPointerException.class, () -> PropertyUtils.setIndexedProperty(null, "intArray", 0, Integer.valueOf(1)));
        assertThrows(NullPointerException.class, () -> PropertyUtils.setIndexedProperty(bean, null, 0, Integer.valueOf(1)));
        // Use index expression
        assertThrows(NullPointerException.class, () -> PropertyUtils.setIndexedProperty(null, "intArray[0]", Integer.valueOf(1)));
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.setIndexedProperty(bean, "[0]", Integer.valueOf(1)));
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.setIndexedProperty(bean, "intArray", Integer.valueOf(1)));
        // Use explicit index argument
        assertThrows(NullPointerException.class, () -> PropertyUtils.setIndexedProperty(null, "intIndexed", 0, Integer.valueOf(1)));
        assertThrows(NullPointerException.class, () -> PropertyUtils.setIndexedProperty(bean, null, 0, Integer.valueOf(1)));
        // Use index expression
        assertThrows(NullPointerException.class, () -> PropertyUtils.setIndexedProperty(null, "intIndexed[0]", Integer.valueOf(1)));
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.setIndexedProperty(bean, "[0]", Integer.valueOf(1)));
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.setIndexedProperty(bean, "intIndexed", Integer.valueOf(1)));
    }

    /**
     * Positive and negative tests on setIndexedProperty valid arguments.
     */
    @Test
    public void testSetIndexedValues() {

        Object value = null;

        // Use explicit index argument

        try {
            PropertyUtils.setIndexedProperty(bean, "intArray", 0, Integer.valueOf(1));
            value = PropertyUtils.getIndexedProperty(bean, "intArray", 0);
            assertNotNull(value, "Returned new value 0");
            assertInstanceOf(Integer.class, value, "Returned Integer new value 0");
            assertEquals(1, ((Integer) value).intValue(), "Returned correct new value 0");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "intIndexed", 1, Integer.valueOf(11));
            value = PropertyUtils.getIndexedProperty(bean, "intIndexed", 1);
            assertNotNull(value, "Returned new value 1");
            assertInstanceOf(Integer.class, value, "Returned Integer new value 1");
            assertEquals(11, ((Integer) value).intValue(), "Returned correct new value 1");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "listIndexed", 2, "New Value 2");
            value = PropertyUtils.getIndexedProperty(bean, "listIndexed", 2);
            assertNotNull(value, "Returned new value 2");
            assertInstanceOf(String.class, value, "Returned String new value 2");
            assertEquals("New Value 2", (String) value, "Returned correct new value 2");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "stringArray", 2, "New Value 2");
            value = PropertyUtils.getIndexedProperty(bean, "stringArray", 2);
            assertNotNull(value, "Returned new value 2");
            assertInstanceOf(String.class, value, "Returned String new value 2");
            assertEquals("New Value 2", (String) value, "Returned correct new value 2");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "stringArray", 3, "New Value 3");
            value = PropertyUtils.getIndexedProperty(bean, "stringArray", 3);
            assertNotNull(value, "Returned new value 3");
            assertInstanceOf(String.class, value, "Returned String new value 3");
            assertEquals("New Value 3", (String) value, "Returned correct new value 3");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        // Use index expression

        try {
            PropertyUtils.setIndexedProperty(bean, "intArray[4]", Integer.valueOf(1));
            value = PropertyUtils.getIndexedProperty(bean, "intArray[4]");
            assertNotNull(value, "Returned new value 4");
            assertInstanceOf(Integer.class, value, "Returned Integer new value 4");
            assertEquals(1, ((Integer) value).intValue(), "Returned correct new value 4");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "intIndexed[3]", Integer.valueOf(11));
            value = PropertyUtils.getIndexedProperty(bean, "intIndexed[3]");
            assertNotNull(value, "Returned new value 5");
            assertInstanceOf(Integer.class, value, "Returned Integer new value 5");
            assertEquals(11, ((Integer) value).intValue(), "Returned correct new value 5");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "listIndexed[1]", "New Value 2");
            value = PropertyUtils.getIndexedProperty(bean, "listIndexed[1]");
            assertNotNull(value, "Returned new value 6");
            assertInstanceOf(String.class, value, "Returned String new value 6");
            assertEquals("New Value 2", (String) value, "Returned correct new value 6");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "stringArray[1]", "New Value 2");
            value = PropertyUtils.getIndexedProperty(bean, "stringArray[2]");
            assertNotNull(value, "Returned new value 6");
            assertInstanceOf(String.class, value, "Returned String new value 6");
            assertEquals("New Value 2", (String) value, "Returned correct new value 6");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "stringArray[0]", "New Value 3");
            value = PropertyUtils.getIndexedProperty(bean, "stringArray[0]");
            assertNotNull(value, "Returned new value 7");
            assertInstanceOf(String.class, value, "Returned String new value 7");
            assertEquals("New Value 3", (String) value, "Returned correct new value 7");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        // Index out of bounds tests

        try {
            PropertyUtils.setIndexedProperty(bean, "intArray", -1, Integer.valueOf(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "intArray", 5, Integer.valueOf(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "intIndexed", -1, Integer.valueOf(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "intIndexed", 5, Integer.valueOf(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "listIndexed", 5, "New String");
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "listIndexed", -1, "New String");
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "stringArray", -1, "New String");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "stringArray", 5, "New String");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "stringIndexed", -1, "New String");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "stringIndexed", 5, "New String");
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
    @Test
    public void testSetMappedArguments() {
        // Use explicit key argument
        assertThrows(NullPointerException.class, () -> PropertyUtils.setMappedProperty(null, "mappedProperty", "First Key", "First Value"));
        assertThrows(NullPointerException.class, () -> PropertyUtils.setMappedProperty(bean, null, "First Key", "First Value"));
        assertThrows(NullPointerException.class, () -> PropertyUtils.setMappedProperty(bean, "mappedProperty", null, "First Value"));
        // Use key expression
        assertThrows(NullPointerException.class, () -> PropertyUtils.setMappedProperty(null, "mappedProperty(First Key)", "First Value"));
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.setMappedProperty(bean, "(Second Key)", "Second Value"));
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.setMappedProperty(bean, "mappedProperty", "Third Value"));
    }

    /**
     * Positive and negative tests on setMappedProperty valid arguments.
     */
    @Test
    public void testSetMappedValues() {

        Object value = null;

        // Use explicit key argument

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty", "Fourth Key");
            assertNull(value, "Can not find fourth value");
        } catch (final Throwable t) {
            fail("Finding fourth value threw " + t);
        }

        try {
            PropertyUtils.setMappedProperty(bean, "mappedProperty", "Fourth Key", "Fourth Value");
        } catch (final Throwable t) {
            fail("Setting fourth value threw " + t);
        }

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty", "Fourth Key");
            assertEquals("Fourth Value", value, "Can find fourth value");
        } catch (final Throwable t) {
            fail("Finding fourth value threw " + t);
        }

        // Use key expression with parentheses

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty(Fifth Key)");
            assertNull(value, "Can not find fifth value");
        } catch (final Throwable t) {
            fail("Finding fifth value threw " + t);
        }

        try {
            PropertyUtils.setMappedProperty(bean, "mappedProperty(Fifth Key)", "Fifth Value");
        } catch (final Throwable t) {
            fail("Setting fifth value threw " + t);
        }

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty(Fifth Key)");
            assertEquals("Fifth Value", value, "Can find fifth value");
        } catch (final Throwable t) {
            fail("Finding fifth value threw " + t);
        }

        // Use key expression with dotted expression

        try {
            value = PropertyUtils.getNestedProperty(bean, "mapProperty.Sixth Key");
            assertNull(value, "Can not find sixth value");
        } catch (final Throwable t) {
            fail("Finding fifth value threw " + t);
        }

        try {
            PropertyUtils.setNestedProperty(bean, "mapProperty.Sixth Key", "Sixth Value");
        } catch (final Throwable t) {
            fail("Setting sixth value threw " + t);
        }

        try {
            value = PropertyUtils.getNestedProperty(bean, "mapProperty.Sixth Key");
            assertEquals("Sixth Value", value, "Can find sixth value");
        } catch (final Throwable t) {
            fail("Finding sixth value threw " + t);
        }

    }

    /**
     * Corner cases on setNestedProperty invalid arguments.
     */
    @Test
    public void testSetNestedArguments() {
        assertThrows(NullPointerException.class, () -> PropertyUtils.setNestedProperty(null, "stringProperty", ""));
        assertThrows(NullPointerException.class, () -> PropertyUtils.setNestedProperty(bean, null, ""));
    }

    /**
     * Test setNextedProperty on a boolean property.
     */
    @Test
    public void testSetNestedBoolean() throws Exception {
        final boolean oldValue = nested.getBooleanProperty();
        final boolean newValue = !oldValue;
        PropertyUtils.setNestedProperty(bean, "nested.booleanProperty", Boolean.valueOf(newValue));
        assertEquals(newValue, nested.getBooleanProperty(), "Matched new value");
    }

    /**
     * Test setNestedProperty on a double property.
     */
    @Test
    public void testSetNestedDouble() throws Exception {
        final double oldValue = nested.getDoubleProperty();
        final double newValue = oldValue + 1.0;
        PropertyUtils.setNestedProperty(bean, "nested.doubleProperty", Double.valueOf(newValue));
        assertEquals(newValue, nested.getDoubleProperty(), 0.005, "Matched new value");
    }

    /**
     * Test setNestedProperty on a float property.
     */
    @Test
    public void testSetNestedFloat() throws Exception {
        final float oldValue = nested.getFloatProperty();
        final float newValue = oldValue + (float) 1.0;
        PropertyUtils.setNestedProperty(bean, "nested.floatProperty", Float.valueOf(newValue));
        assertEquals(newValue, nested.getFloatProperty(), (float) 0.005, "Matched new value");
    }

    /**
     * Test setNestedProperty on a int property.
     */
    @Test
    public void testSetNestedInt() throws Exception {
        final int oldValue = nested.getIntProperty();
        final int newValue = oldValue + 1;
        PropertyUtils.setNestedProperty(bean, "nested.intProperty", Integer.valueOf(newValue));
        assertEquals(newValue, nested.getIntProperty(), "Matched new value");
    }

    /**
     * Test setNestedProperty on a long property.
     */
    @Test
    public void testSetNestedLong() throws Exception {
        final long oldValue = nested.getLongProperty();
        final long newValue = oldValue + 1;
        PropertyUtils.setNestedProperty(bean, "nested.longProperty", Long.valueOf(newValue));
        assertEquals(newValue, nested.getLongProperty(), "Matched new value");
    }

    /**
     * Test setNestedProperty on a read-only String property.
     */
    @Test
    public void testSetNestedReadOnly() throws Exception {
        try {
            final String oldValue = nested.getWriteOnlyPropertyValue();
            final String newValue = oldValue + " Extra Value";
            PropertyUtils.setNestedProperty(bean, "nested.readOnlyProperty", newValue);
            fail("Should have thrown NoSuchMethodException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
        }

    }

    /**
     * Test setNestedProperty on a short property.
     */
    @Test
    public void testSetNestedShort() throws Exception {
        final short oldValue = nested.getShortProperty();
        short newValue = oldValue;
        newValue++;
        PropertyUtils.setNestedProperty(bean, "nested.shortProperty", Short.valueOf(newValue));
        assertEquals(newValue, nested.getShortProperty(), "Matched new value");
    }

    /**
     * Test setNestedProperty on a String property.
     */
    @Test
    public void testSetNestedString() throws Exception {
        final String oldValue = nested.getStringProperty();
        final String newValue = oldValue + " Extra Value";
        PropertyUtils.setNestedProperty(bean, "nested.stringProperty", newValue);
        assertEquals(newValue, nested.getStringProperty(), "Matched new value");
    }

    /**
     * Test setNestedProperty on an unknown property name.
     */
    @Test
    public void testSetNestedUnknown() throws Exception {
        try {
            final String newValue = "New String Value";
            PropertyUtils.setNestedProperty(bean, "nested.unknown", newValue);
            fail("Should have thrown NoSuchMethodException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
        }

    }

    /**
     * Test setNestedProperty on a write-only String property.
     */
    @Test
    public void testSetNestedWriteOnly() throws Exception {
        final String oldValue = nested.getWriteOnlyPropertyValue();
        final String newValue = oldValue + " Extra Value";
        PropertyUtils.setNestedProperty(bean, "nested.writeOnlyProperty", newValue);
        assertEquals(newValue, nested.getWriteOnlyPropertyValue(), "Matched new value");
    }

    /**
     * Corner cases on setSimpleProperty invalid arguments.
     */
    @Test
    public void testSetSimpleArguments() {
        assertThrows(NullPointerException.class, () -> PropertyUtils.setSimpleProperty(null, "stringProperty", ""));
        assertThrows(NullPointerException.class, () -> PropertyUtils.setSimpleProperty(bean, null, ""));
    }

    /**
     * Test setSimpleProperty on a boolean property.
     */
    @Test
    public void testSetSimpleBoolean() throws Exception {
        final boolean oldValue = ((Boolean) bean.get("booleanProperty")).booleanValue();
        final boolean newValue = !oldValue;
        PropertyUtils.setSimpleProperty(bean, "booleanProperty", Boolean.valueOf(newValue));
        assertEquals(newValue, ((Boolean) bean.get("booleanProperty")).booleanValue(), "Matched new value");
    }

    /**
     * Test setSimpleProperty on a double property.
     */
    @Test
    public void testSetSimpleDouble() throws Exception {
        final double oldValue = ((Double) bean.get("doubleProperty")).doubleValue();
        final double newValue = oldValue + 1.0;
        PropertyUtils.setSimpleProperty(bean, "doubleProperty", Double.valueOf(newValue));
        assertEquals(newValue, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005, "Matched new value");
    }

    /**
     * Test setSimpleProperty on a float property.
     */
    @Test
    public void testSetSimpleFloat() throws Exception {
        final float oldValue = ((Float) bean.get("floatProperty")).floatValue();
        final float newValue = oldValue + (float) 1.0;
        PropertyUtils.setSimpleProperty(bean, "floatProperty", Float.valueOf(newValue));
        assertEquals(newValue, ((Float) bean.get("floatProperty")).floatValue(), (float) 0.005, "Matched new value");
    }

    /**
     * Negative test setSimpleProperty on an indexed property.
     */
    @Test
    public void testSetSimpleIndexed() throws Exception {
        try {
            PropertyUtils.setSimpleProperty(bean, "stringIndexed[0]", "New String Value");
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // Correct result for this test
        }
    }

    /**
     * Test setSimpleProperty on a int property.
     */
    @Test
    public void testSetSimpleInt() throws Exception {
        final int oldValue = ((Integer) bean.get("intProperty")).intValue();
        final int newValue = oldValue + 1;
        PropertyUtils.setSimpleProperty(bean, "intProperty", Integer.valueOf(newValue));
        assertEquals(newValue, ((Integer) bean.get("intProperty")).intValue(), "Matched new value");
    }

    /**
     * Test setSimpleProperty on a long property.
     */
    @Test
    public void testSetSimpleLong() throws Exception {
        final long oldValue = ((Long) bean.get("longProperty")).longValue();
        final long newValue = oldValue + 1;
        PropertyUtils.setSimpleProperty(bean, "longProperty", Long.valueOf(newValue));
        assertEquals(newValue, ((Long) bean.get("longProperty")).longValue(), "Matched new value");
    }

    /**
     * Negative test setSimpleProperty on a nested property.
     */
    @Test
    public void testSetSimpleNested() throws Exception {
        try {
            PropertyUtils.setSimpleProperty(bean, "nested.stringProperty", "New String Value");
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // Correct result for this test
        }
    }

    /**
     * Test setSimpleProperty on a short property.
     */
    @Test
    public void testSetSimpleShort() throws Exception {
        final short oldValue = ((Short) bean.get("shortProperty")).shortValue();
        short newValue = oldValue;
        newValue++;
        PropertyUtils.setSimpleProperty(bean, "shortProperty", Short.valueOf(newValue));
        assertEquals(newValue, ((Short) bean.get("shortProperty")).shortValue(), "Matched new value");
    }

    /**
     * Test setSimpleProperty on a String property.
     */
    @Test
    public void testSetSimpleString() throws Exception {
        final String oldValue = (String) bean.get("stringProperty");
        final String newValue = oldValue + " Extra Value";
        PropertyUtils.setSimpleProperty(bean, "stringProperty", newValue);
        assertEquals(newValue, (String) bean.get("stringProperty"), "Matched new value");
    }

    /**
     * Test setSimpleProperty on an unknown property name.
     */
    @Test
    public void testSetSimpleUnknown() throws Exception {
        try {
            final String newValue = "New String Value";
            PropertyUtils.setSimpleProperty(bean, "unknown", newValue);
            fail("Should have thrown NoSuchMethodException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
            assertEquals("Unknown property 'unknown' on dynaclass '" + bean.getDynaClass() + "'", e.getMessage());
        }

    }

}
