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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for BeanUtils when the underlying bean is actually a DynaBean.
 */
public class DynaBeanUtilsTestCase {

    /**
     * Create and return a {@code DynaClass} instance for our test {@code DynaBean}.
     */
    protected static DynaClass createDynaClass() {

        final int[] intArray = {};
        final String[] stringArray = {};

        return new BasicDynaClass("TestDynaClass", null, new DynaProperty[] { new DynaProperty("booleanProperty", Boolean.TYPE),
                new DynaProperty("booleanSecond", Boolean.TYPE), new DynaProperty("byteProperty", Byte.TYPE), new DynaProperty("doubleProperty", Double.TYPE),
                new DynaProperty("dupProperty", stringArray.getClass()), new DynaProperty("floatProperty", Float.TYPE),
                new DynaProperty("intArray", intArray.getClass()), new DynaProperty("intIndexed", intArray.getClass()),
                new DynaProperty("intProperty", Integer.TYPE), new DynaProperty("listIndexed", List.class), new DynaProperty("longProperty", Long.TYPE),
                new DynaProperty("mapProperty", Map.class), new DynaProperty("mappedProperty", Map.class), new DynaProperty("mappedIntProperty", Map.class),
                new DynaProperty("nested", TestBean.class), new DynaProperty("nullProperty", String.class), new DynaProperty("shortProperty", Short.TYPE),
                new DynaProperty("stringArray", stringArray.getClass()), new DynaProperty("stringIndexed", stringArray.getClass()),
                new DynaProperty("stringProperty", String.class), });

    }

    /**
     * The basic test bean for each test.
     */
    protected DynaBean bean;

    /**
     * The nested bean pointed at by the "nested" property.
     */
    protected TestBean nested;

    /**
     * The set of properties that should be described.
     */
    protected String[] describes = { "booleanProperty", "booleanSecond", "byteProperty", "doubleProperty", "dupProperty", "floatProperty", "intArray",
            "intIndexed", "intProperty", "listIndexed", "longProperty", "mapProperty", "mappedProperty", "mappedIntProperty", "nested", "nullProperty",
            // "readOnlyProperty",
            "shortProperty", "stringArray", "stringIndexed", "stringProperty" };

    // Ensure that the nested intArray matches the specified values
    protected void checkIntArray(final int[] actual, final int[] expected) {
        assertNotNull(actual, "actual array not null");
        assertEquals(expected.length, actual.length, "actual array length");
        for (int i = 0; i < actual.length; i++) {
            assertEquals(expected[i], actual[i], "actual array value[" + i + "]");
        }
    }

    // Ensure that the actual Map matches the expected Map
    protected void checkMap(final Map<?, ?> actual, final Map<?, ?> expected) {
        assertNotNull(actual, "actual map not null");
        assertEquals(expected.size(), actual.size(), "actual map size");
        for (final Object key : expected.keySet()) {
            assertEquals(expected.get(key), actual.get(key), "actual map value(" + key + ")");
        }
    }

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() throws Exception {

        ConvertUtils.deregister();

        // Instantiate a new DynaBean instance
        final DynaClass dynaClass = createDynaClass();
        bean = dynaClass.newInstance();

        // Initialize the DynaBean's property values (like TestBean)
        bean.set("booleanProperty", Boolean.valueOf(true));
        bean.set("booleanSecond", Boolean.valueOf(true));
        bean.set("byteProperty", Byte.valueOf((byte) 121));
        bean.set("doubleProperty", Double.valueOf(321.0));
        bean.set("floatProperty", Float.valueOf((float) 123.0));
        final String[] dupProperty = { "Dup 0", "Dup 1", "Dup 2", "Dup 3", "Dup 4" };
        bean.set("dupProperty", dupProperty);
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
     * Test the cloneBean() method from a DynaBean.
     */
    @Test
    public void testCloneDynaBean() throws Exception {

        // Set up an origin bean with customized properties
        final DynaClass dynaClass = DynaBeanUtilsTestCase.createDynaClass();
        final DynaBean orig = dynaClass.newInstance();
        orig.set("booleanProperty", Boolean.FALSE);
        orig.set("byteProperty", Byte.valueOf((byte) 111));
        orig.set("doubleProperty", Double.valueOf(333.33));
        orig.set("dupProperty", new String[] { "New 0", "New 1", "New 2" });
        orig.set("intArray", new int[] { 100, 200, 300 });
        orig.set("intProperty", Integer.valueOf(333));
        orig.set("longProperty", Long.valueOf(3333));
        orig.set("shortProperty", Short.valueOf((short) 33));
        orig.set("stringArray", new String[] { "New 0", "New 1" });
        orig.set("stringProperty", "Custom string");

        // Copy the origin bean to our destination test bean
        final DynaBean clonedBean = (DynaBean) BeanUtils.cloneBean(orig);

        // Validate the results for scalar properties
        assertEquals(false, ((Boolean) clonedBean.get("booleanProperty")).booleanValue(), "Cloned boolean property");
        assertEquals((byte) 111, ((Byte) clonedBean.get("byteProperty")).byteValue(), "Cloned byte property");
        assertEquals(333.33, ((Double) clonedBean.get("doubleProperty")).doubleValue(), 0.005, "Cloned double property");
        assertEquals(333, ((Integer) clonedBean.get("intProperty")).intValue(), "Cloned int property");
        assertEquals(3333, ((Long) clonedBean.get("longProperty")).longValue(), "Cloned long property");
        assertEquals((short) 33, ((Short) clonedBean.get("shortProperty")).shortValue(), "Cloned short property");
        assertEquals("Custom string", (String) clonedBean.get("stringProperty"), "Cloned string property");

        // Validate the results for array properties
        final String[] dupProperty = (String[]) clonedBean.get("dupProperty");
        assertNotNull(dupProperty, "dupProperty present");
        assertEquals(3, dupProperty.length, "dupProperty length");
        assertEquals("New 0", dupProperty[0], "dupProperty[0]");
        assertEquals("New 1", dupProperty[1], "dupProperty[1]");
        assertEquals("New 2", dupProperty[2], "dupProperty[2]");
        final int[] intArray = (int[]) clonedBean.get("intArray");
        assertNotNull(intArray, "intArray present");
        assertEquals(3, intArray.length, "intArray length");
        assertEquals(100, intArray[0], "intArray[0]");
        assertEquals(200, intArray[1], "intArray[1]");
        assertEquals(300, intArray[2], "intArray[2]");
        final String[] stringArray = (String[]) clonedBean.get("stringArray");
        assertNotNull(stringArray, "stringArray present");
        assertEquals(2, stringArray.length, "stringArray length");
        assertEquals("New 0", stringArray[0], "stringArray[0]");
        assertEquals("New 1", stringArray[1], "stringArray[1]");

    }

    /**
     * Test the copyProperties() method from a DynaBean.
     */
    @Test
    public void testCopyPropertiesDynaBean() throws Exception {

        // Set up an origin bean with customized properties
        final DynaClass dynaClass = DynaBeanUtilsTestCase.createDynaClass();
        final DynaBean orig = dynaClass.newInstance();
        orig.set("booleanProperty", Boolean.FALSE);
        orig.set("byteProperty", Byte.valueOf((byte) 111));
        orig.set("doubleProperty", Double.valueOf(333.33));
        orig.set("dupProperty", new String[] { "New 0", "New 1", "New 2" });
        orig.set("intArray", new int[] { 100, 200, 300 });
        orig.set("intProperty", Integer.valueOf(333));
        orig.set("longProperty", Long.valueOf(3333));
        orig.set("shortProperty", Short.valueOf((short) 33));
        orig.set("stringArray", new String[] { "New 0", "New 1" });
        orig.set("stringProperty", "Custom string");

        // Copy the origin bean to our destination test bean
        BeanUtils.copyProperties(bean, orig);

        // Validate the results for scalar properties
        assertEquals(false, ((Boolean) bean.get("booleanProperty")).booleanValue(), "Copied boolean property");
        assertEquals((byte) 111, ((Byte) bean.get("byteProperty")).byteValue(), "Copied byte property");
        assertEquals(333.33, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005, "Copied double property");
        assertEquals(333, ((Integer) bean.get("intProperty")).intValue(), "Copied int property");
        assertEquals(3333, ((Long) bean.get("longProperty")).longValue(), "Copied long property");
        assertEquals((short) 33, ((Short) bean.get("shortProperty")).shortValue(), "Copied short property");
        assertEquals("Custom string", (String) bean.get("stringProperty"), "Copied string property");

        // Validate the results for array properties
        final String[] dupProperty = (String[]) bean.get("dupProperty");
        assertNotNull(dupProperty, "dupProperty present");
        assertEquals(3, dupProperty.length, "dupProperty length");
        assertEquals("New 0", dupProperty[0], "dupProperty[0]");
        assertEquals("New 1", dupProperty[1], "dupProperty[1]");
        assertEquals("New 2", dupProperty[2], "dupProperty[2]");
        final int[] intArray = (int[]) bean.get("intArray");
        assertNotNull(intArray, "intArray present");
        assertEquals(3, intArray.length, "intArray length");
        assertEquals(100, intArray[0], "intArray[0]");
        assertEquals(200, intArray[1], "intArray[1]");
        assertEquals(300, intArray[2], "intArray[2]");
        final String[] stringArray = (String[]) bean.get("stringArray");
        assertNotNull(stringArray, "stringArray present");
        assertEquals(2, stringArray.length, "stringArray length");
        assertEquals("New 0", stringArray[0], "stringArray[0]");
        assertEquals("New 1", stringArray[1], "stringArray[1]");

    }

    /**
     * Test copyProperties() when the origin is a {@code Map}.
     */
    @Test
    public void testCopyPropertiesMap() throws Exception {

        final Map<String, Object> map = new HashMap<>();
        map.put("booleanProperty", "false");
        map.put("byteProperty", "111");
        map.put("doubleProperty", "333.0");
        map.put("dupProperty", new String[] { "New 0", "New 1", "New 2" });
        map.put("floatProperty", "222.0");
        map.put("intArray", new String[] { "0", "100", "200" });
        map.put("intProperty", "111");
        map.put("longProperty", "444");
        map.put("shortProperty", "555");
        map.put("stringProperty", "New String Property");

        BeanUtils.copyProperties(bean, map);

        // Scalar properties
        assertEquals(false, ((Boolean) bean.get("booleanProperty")).booleanValue(), "booleanProperty");
        assertEquals((byte) 111, ((Byte) bean.get("byteProperty")).byteValue(), "byteProperty");
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
     * Test the copyProperties() method from a standard JavaBean.
     */
    @Test
    public void testCopyPropertiesStandard() throws Exception {

        // Set up an origin bean with customized properties
        final TestBean orig = new TestBean();
        orig.setBooleanProperty(false);
        orig.setByteProperty((byte) 111);
        orig.setDoubleProperty(333.33);
        orig.setDupProperty(new String[] { "New 0", "New 1", "New 2" });
        orig.setIntArray(new int[] { 100, 200, 300 });
        orig.setIntProperty(333);
        orig.setLongProperty(3333);
        orig.setShortProperty((short) 33);
        orig.setStringArray(new String[] { "New 0", "New 1" });
        orig.setStringProperty("Custom string");

        // Copy the origin bean to our destination test bean
        BeanUtils.copyProperties(bean, orig);

        // Validate the results for scalar properties
        assertEquals(false, ((Boolean) bean.get("booleanProperty")).booleanValue(), "Copied boolean property");
        assertEquals((byte) 111, ((Byte) bean.get("byteProperty")).byteValue(), "Copied byte property");
        assertEquals(333.33, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005, "Copied double property");
        assertEquals(333, ((Integer) bean.get("intProperty")).intValue(), "Copied int property");
        assertEquals(3333, ((Long) bean.get("longProperty")).longValue(), "Copied long property");
        assertEquals((short) 33, ((Short) bean.get("shortProperty")).shortValue(), "Copied short property");
        assertEquals("Custom string", (String) bean.get("stringProperty"), "Copied string property");

        // Validate the results for array properties
        final String[] dupProperty = (String[]) bean.get("dupProperty");
        assertNotNull(dupProperty, "dupProperty present");
        assertEquals(3, dupProperty.length, "dupProperty length");
        assertEquals("New 0", dupProperty[0], "dupProperty[0]");
        assertEquals("New 1", dupProperty[1], "dupProperty[1]");
        assertEquals("New 2", dupProperty[2], "dupProperty[2]");
        final int[] intArray = (int[]) bean.get("intArray");
        assertNotNull(intArray, "intArray present");
        assertEquals(3, intArray.length, "intArray length");
        assertEquals(100, intArray[0], "intArray[0]");
        assertEquals(200, intArray[1], "intArray[1]");
        assertEquals(300, intArray[2], "intArray[2]");
        final String[] stringArray = (String[]) bean.get("stringArray");
        assertNotNull(stringArray, "stringArray present");
        assertEquals(2, stringArray.length, "stringArray length");
        assertEquals("New 0", stringArray[0], "stringArray[0]");
        assertEquals("New 1", stringArray[1], "stringArray[1]");

    }

    /**
     * Test narrowing and widening conversions on byte.
     */
    @Test
    public void testCopyPropertyByte() throws Exception {

        BeanUtils.setProperty(bean, "byteProperty", Byte.valueOf((byte) 123));
        assertEquals((byte) 123, ((Byte) bean.get("byteProperty")).byteValue());
        /*
         * BeanUtils.setProperty(bean, "byteProperty", new Double((double) 123)); assertEquals((byte) 123, ((Byte) bean.get("byteProperty")).byteValue());
         * BeanUtils.setProperty(bean, "byteProperty", new Float((float) 123)); assertEquals((byte) 123, ((Byte) bean.get("byteProperty")).byteValue());
         */
        BeanUtils.setProperty(bean, "byteProperty", Integer.valueOf(123));
        assertEquals((byte) 123, ((Byte) bean.get("byteProperty")).byteValue());
        BeanUtils.setProperty(bean, "byteProperty", Long.valueOf(123));
        assertEquals((byte) 123, ((Byte) bean.get("byteProperty")).byteValue());
        BeanUtils.setProperty(bean, "byteProperty", Short.valueOf((short) 123));
        assertEquals((byte) 123, ((Byte) bean.get("byteProperty")).byteValue());

    }

    /**
     * Test narrowing and widening conversions on double.
     */
    @Test
    public void testCopyPropertyDouble() throws Exception {

        BeanUtils.setProperty(bean, "doubleProperty", Byte.valueOf((byte) 123));
        assertEquals(123, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", Double.valueOf(123));
        assertEquals(123, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", Float.valueOf(123));
        assertEquals(123, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", Integer.valueOf(123));
        assertEquals(123, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", Long.valueOf(123));
        assertEquals(123, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", Short.valueOf((short) 123));
        assertEquals(123, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005);

    }

    /**
     * Test narrowing and widening conversions on float.
     */
    @Test
    public void testCopyPropertyFloat() throws Exception {

        BeanUtils.setProperty(bean, "floatProperty", Byte.valueOf((byte) 123));
        assertEquals(123, ((Float) bean.get("floatProperty")).floatValue(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", Double.valueOf(123));
        assertEquals(123, ((Float) bean.get("floatProperty")).floatValue(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", Float.valueOf(123));
        assertEquals(123, ((Float) bean.get("floatProperty")).floatValue(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", Integer.valueOf(123));
        assertEquals(123, ((Float) bean.get("floatProperty")).floatValue(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", Long.valueOf(123));
        assertEquals(123, ((Float) bean.get("floatProperty")).floatValue(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", Short.valueOf((short) 123));
        assertEquals(123, ((Float) bean.get("floatProperty")).floatValue(), 0.005);

    }

    /**
     * Test narrowing and widening conversions on int.
     */
    @Test
    public void testCopyPropertyInteger() throws Exception {

        BeanUtils.setProperty(bean, "longProperty", Byte.valueOf((byte) 123));
        assertEquals(123, ((Integer) bean.get("intProperty")).intValue());
        /*
         * BeanUtils.setProperty(bean, "longProperty", new Double((double) 123)); assertEquals((int) 123, ((Integer) bean.get("intProperty")).intValue());
         * BeanUtils.setProperty(bean, "longProperty", new Float((float) 123)); assertEquals((int) 123, ((Integer) bean.get("intProperty")).intValue());
         */
        BeanUtils.setProperty(bean, "longProperty", Integer.valueOf(123));
        assertEquals(123, ((Integer) bean.get("intProperty")).intValue());
        BeanUtils.setProperty(bean, "longProperty", Long.valueOf(123));
        assertEquals(123, ((Integer) bean.get("intProperty")).intValue());
        BeanUtils.setProperty(bean, "longProperty", Short.valueOf((short) 123));
        assertEquals(123, ((Integer) bean.get("intProperty")).intValue());

    }

    /**
     * Test narrowing and widening conversions on long.
     */
    @Test
    public void testCopyPropertyLong() throws Exception {

        BeanUtils.setProperty(bean, "longProperty", Byte.valueOf((byte) 123));
        assertEquals(123, ((Long) bean.get("longProperty")).longValue());
        /*
         * BeanUtils.setProperty(bean, "longProperty", new Double((double) 123)); assertEquals((long) 123, ((Long) bean.get("longProperty")).longValue());
         * BeanUtils.setProperty(bean, "longProperty", new Float((float) 123)); assertEquals((long) 123, ((Long) bean.get("longProperty")).longValue());
         */
        BeanUtils.setProperty(bean, "longProperty", Integer.valueOf(123));
        assertEquals(123, ((Long) bean.get("longProperty")).longValue());
        BeanUtils.setProperty(bean, "longProperty", Long.valueOf(123));
        assertEquals(123, ((Long) bean.get("longProperty")).longValue());
        BeanUtils.setProperty(bean, "longProperty", Short.valueOf((short) 123));
        assertEquals(123, ((Long) bean.get("longProperty")).longValue());

    }

    /**
     * Test copying a property using a nested indexed array expression, with and without conversions.
     */
    @Test
    public void testCopyPropertyNestedIndexedArray() throws Exception {

        final int[] origArray = { 0, 10, 20, 30, 40 };
        final int[] intArray = { 0, 0, 0 };
        ((TestBean) bean.get("nested")).setIntArray(intArray);
        final int[] intChanged = { 0, 0, 0 };

        // No conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", Integer.valueOf(1));
        checkIntArray((int[]) bean.get("intArray"), origArray);
        intChanged[1] = 1;
        checkIntArray(((TestBean) bean.get("nested")).getIntArray(), intChanged);

        // Widening conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", Byte.valueOf((byte) 2));
        checkIntArray((int[]) bean.get("intArray"), origArray);
        intChanged[1] = 2;
        checkIntArray(((TestBean) bean.get("nested")).getIntArray(), intChanged);

        // Narrowing conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", Long.valueOf(3));
        checkIntArray((int[]) bean.get("intArray"), origArray);
        intChanged[1] = 3;
        checkIntArray(((TestBean) bean.get("nested")).getIntArray(), intChanged);

        // String conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", "4");
        checkIntArray((int[]) bean.get("intArray"), origArray);
        intChanged[1] = 4;
        checkIntArray(((TestBean) bean.get("nested")).getIntArray(), intChanged);

    }

    /**
     * Test copying a property using a nested mapped map property.
     */
    @Test
    public void testCopyPropertyNestedMappedMap() throws Exception {

        final Map<String, Object> origMap = new HashMap<>();
        origMap.put("First Key", "First Value");
        origMap.put("Second Key", "Second Value");
        final Map<String, Object> changedMap = new HashMap<>();
        changedMap.put("First Key", "First Value");
        changedMap.put("Second Key", "Second Value");

        // No conversion required
        BeanUtils.copyProperty(bean, "nested.mapProperty(Second Key)", "New Second Value");
        checkMap((Map<?, ?>) bean.get("mapProperty"), origMap);
        changedMap.put("Second Key", "New Second Value");
        checkMap(((TestBean) bean.get("nested")).getMapProperty(), changedMap);

    }

    /**
     * Test copying a property using a nested simple expression, with and without conversions.
     */
    @Test
    public void testCopyPropertyNestedSimple() throws Exception {

        bean.set("intProperty", Integer.valueOf(0));
        nested.setIntProperty(0);

        // No conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", Integer.valueOf(1));
        assertEquals(0, ((Integer) bean.get("intProperty")).intValue());
        assertEquals(1, nested.getIntProperty());

        // Widening conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", Byte.valueOf((byte) 2));
        assertEquals(0, ((Integer) bean.get("intProperty")).intValue());
        assertEquals(2, nested.getIntProperty());

        // Narrowing conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", Long.valueOf(3));
        assertEquals(0, ((Integer) bean.get("intProperty")).intValue());
        assertEquals(3, nested.getIntProperty());

        // String conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", "4");
        assertEquals(0, ((Integer) bean.get("intProperty")).intValue());
        assertEquals(4, nested.getIntProperty());

    }

    /**
     * Test copying a null property value.
     */
    @Test
    public void testCopyPropertyNull() throws Exception {

        bean.set("nullProperty", "non-null value");
        BeanUtils.copyProperty(bean, "nullProperty", null);
        assertNull(bean.get("nullProperty"), "nullProperty is null");

    }

    /**
     * Test narrowing and widening conversions on short.
     */
    @Test
    public void testCopyPropertyShort() throws Exception {

        BeanUtils.setProperty(bean, "shortProperty", Byte.valueOf((byte) 123));
        assertEquals((short) 123, ((Short) bean.get("shortProperty")).shortValue());
        /*
         * BeanUtils.setProperty(bean, "shortProperty", new Double((double) 123)); assertEquals((short) 123, ((Short) bean.get("shortProperty")).shortValue());
         * BeanUtils.setProperty(bean, "shortProperty", new Float((float) 123)); assertEquals((short) 123, ((Short) bean.get("shortProperty")).shortValue());
         */
        BeanUtils.setProperty(bean, "shortProperty", Integer.valueOf(123));
        assertEquals((short) 123, ((Short) bean.get("shortProperty")).shortValue());
        BeanUtils.setProperty(bean, "shortProperty", Long.valueOf(123));
        assertEquals((short) 123, ((Short) bean.get("shortProperty")).shortValue());
        BeanUtils.setProperty(bean, "shortProperty", Short.valueOf((short) 123));
        assertEquals((short) 123, ((Short) bean.get("shortProperty")).shortValue());

    }

    /**
     * Test the describe() method.
     */
    @Test
    public void testDescribe() throws Exception {

        final Map<String, Object> map = PropertyUtils.describe(bean);

        // Verify existence of all the properties that should be present
        for (final String describe : describes) {
            assertTrue(map.containsKey(describe), "Property '" + describe + "' is present");
        }
        assertFalse(map.containsKey("writeOnlyProperty"), "Property 'writeOnlyProperty' is not present");

        // Verify the values of scalar properties
        assertEquals(Boolean.TRUE, map.get("booleanProperty"), "Value of 'booleanProperty'");
        assertEquals(Byte.valueOf((byte) 121), map.get("byteProperty"), "Value of 'byteProperty'");
        assertEquals(Double.valueOf(321.0), map.get("doubleProperty"), "Value of 'doubleProperty'");
        assertEquals(Float.valueOf((float) 123.0), map.get("floatProperty"), "Value of 'floatProperty'");
        assertEquals(Integer.valueOf(123), map.get("intProperty"), "Value of 'intProperty'");
        assertEquals(Long.valueOf(321), map.get("longProperty"), "Value of 'longProperty'");
        assertEquals(Short.valueOf((short) 987), map.get("shortProperty"), "Value of 'shortProperty'");
        assertEquals("This is a string", (String) map.get("stringProperty"), "Value of 'stringProperty'");

    }

    /**
     * tests the string and int arrays of TestBean
     */
    @Test
    public void testGetArrayProperty() throws Exception {
        String[] arr = BeanUtils.getArrayProperty(bean, "stringArray");
        final String[] comp = (String[]) bean.get("stringArray");

        assertEquals(comp.length, arr.length, "String array length = " + comp.length);

        arr = BeanUtils.getArrayProperty(bean, "intArray");
        final int[] iarr = (int[]) bean.get("intArray");

        assertEquals(iarr.length, arr.length, "String array length = " + iarr.length);
    }

    /**
     * tests getting a 'whatever' property
     */
    @Test
    public void testGetGeneralProperty() throws Exception {
        final String val = BeanUtils.getProperty(bean, "nested.intIndexed[2]");
        final String comp = String.valueOf(bean.get("intIndexed", 2));

        assertEquals(val, comp, "nested.intIndexed[2] == " + comp);
    }

    /**
     * tests getting an indexed property
     */
    @Test
    public void testGetIndexedProperty1() throws Exception {
        String val = BeanUtils.getIndexedProperty(bean, "intIndexed[3]");
        String comp = String.valueOf(bean.get("intIndexed", 3));
        assertEquals(val, comp, "intIndexed[3] == " + comp);

        val = BeanUtils.getIndexedProperty(bean, "stringIndexed[3]");
        comp = (String) bean.get("stringIndexed", 3);
        assertEquals(val, comp, "stringIndexed[3] == " + comp);
    }

    /**
     * tests getting an indexed property
     */
    @Test
    public void testGetIndexedProperty2() throws Exception {
        String val = BeanUtils.getIndexedProperty(bean, "intIndexed", 3);
        String comp = String.valueOf(bean.get("intIndexed", 3));

        assertEquals(val, comp, "intIndexed,3 == " + comp);

        val = BeanUtils.getIndexedProperty(bean, "stringIndexed", 3);
        comp = (String) bean.get("stringIndexed", 3);

        assertEquals(val, comp, "stringIndexed,3 == " + comp);
    }

    /**
     * tests getting a nested property
     */
    @Test
    public void testGetNestedProperty() throws Exception {
        final String val = BeanUtils.getNestedProperty(bean, "nested.stringProperty");
        final String comp = nested.getStringProperty();
        assertEquals(val, comp, "nested.StringProperty == " + comp);
    }

    /**
     * tests getting a 'whatever' property
     */
    @Test
    public void testGetSimpleProperty() throws Exception {
        final String val = BeanUtils.getSimpleProperty(bean, "shortProperty");
        final String comp = String.valueOf(bean.get("shortProperty"));

        assertEquals(val, comp, "shortProperty == " + comp);
    }

    /**
     * Test populate() method on individual array elements.
     */
    @Test
    public void testPopulateArrayElements() throws Exception {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("intIndexed[0]", "100");
        map.put("intIndexed[2]", "120");
        map.put("intIndexed[4]", "140");

        BeanUtils.populate(bean, map);
        final Integer intIndexed0 = (Integer) bean.get("intIndexed", 0);
        assertEquals(100, intIndexed0.intValue(), "intIndexed[0] is 100");
        final Integer intIndexed1 = (Integer) bean.get("intIndexed", 1);
        assertEquals(10, intIndexed1.intValue(), "intIndexed[1] is 10");
        final Integer intIndexed2 = (Integer) bean.get("intIndexed", 2);
        assertEquals(120, intIndexed2.intValue(), "intIndexed[2] is 120");
        final Integer intIndexed3 = (Integer) bean.get("intIndexed", 3);
        assertEquals(30, intIndexed3.intValue(), "intIndexed[3] is 30");
        final Integer intIndexed4 = (Integer) bean.get("intIndexed", 4);
        assertEquals(140, intIndexed4.intValue(), "intIndexed[4] is 140");

        map.clear();
        map.put("stringIndexed[1]", "New String 1");
        map.put("stringIndexed[3]", "New String 3");

        BeanUtils.populate(bean, map);

        assertEquals("String 0", (String) bean.get("stringIndexed", 0), "stringIndexed[0] is \"String 0\"");
        assertEquals("New String 1", (String) bean.get("stringIndexed", 1), "stringIndexed[1] is \"New String 1\"");
        assertEquals("String 2", (String) bean.get("stringIndexed", 2), "stringIndexed[2] is \"String 2\"");
        assertEquals("New String 3", (String) bean.get("stringIndexed", 3), "stringIndexed[3] is \"New String 3\"");
        assertEquals("String 4", (String) bean.get("stringIndexed", 4), "stringIndexed[4] is \"String 4\"");
    }

    /**
     * Test populate() method on array properties as a whole.
     */
    @Test
    public void testPopulateArrayProperties() throws Exception {
        final HashMap<String, Object> map = new HashMap<>();
        // int[] intArray = new int[] { 123, 456, 789 };
        final String[] intArrayIn = { "123", "456", "789" };
        map.put("intArray", intArrayIn);
        String[] stringArray = { "New String 0", "New String 1" };
        map.put("stringArray", stringArray);

        BeanUtils.populate(bean, map);

        final int[] intArray = (int[]) bean.get("intArray");
        assertNotNull(intArray, "intArray is present");
        assertEquals(3, intArray.length, "intArray length");
        assertEquals(123, intArray[0], "intArray[0]");
        assertEquals(456, intArray[1], "intArray[1]");
        assertEquals(789, intArray[2], "intArray[2]");
        stringArray = (String[]) bean.get("stringArray");
        assertNotNull(stringArray, "stringArray is present");
        assertEquals(2, stringArray.length, "stringArray length");
        assertEquals("New String 0", stringArray[0], "stringArray[0]");
        assertEquals("New String 1", stringArray[1], "stringArray[1]");
    }

    /**
     * Test populate() on mapped properties.
     */
    @Test
    public void testPopulateMapped() throws Exception {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("mappedProperty(First Key)", "New First Value");
        map.put("mappedProperty(Third Key)", "New Third Value");

        BeanUtils.populate(bean, map);

        assertEquals("New First Value", (String) bean.get("mappedProperty", "First Key"), "mappedProperty(First Key)");
        assertEquals("Second Value", (String) bean.get("mappedProperty", "Second Key"), "mappedProperty(Second Key)");
        assertEquals("New Third Value", (String) bean.get("mappedProperty", "Third Key"), "mappedProperty(Third Key)");
        assertNull(bean.get("mappedProperty", "Fourth Key"), "mappedProperty(Fourth Key");
    }

    /**
     * Test populate() method on nested properties.
     */
    @Test
    public void testPopulateNested() throws Exception {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("nested.booleanProperty", "false");
        // booleanSecond is left at true
        map.put("nested.doubleProperty", "432.0");
        // floatProperty is left at 123.0
        map.put("nested.intProperty", "543");
        // longProperty is left at 321
        map.put("nested.shortProperty", "654");
        // stringProperty is left at "This is a string"

        BeanUtils.populate(bean, map);

        final TestBean nested = (TestBean) bean.get("nested");
        assertFalse(nested.getBooleanProperty(), "booleanProperty is false");
        assertTrue(nested.isBooleanSecond(), "booleanSecond is true");
        assertEquals(432.0, nested.getDoubleProperty(), 0.005, "doubleProperty is 432.0");
        assertEquals((float) 123.0, nested.getFloatProperty(), (float) 0.005, "floatProperty is 123.0");
        assertEquals(543, nested.getIntProperty(), "intProperty is 543");
        assertEquals(321, nested.getLongProperty(), "longProperty is 321");
        assertEquals((short) 654, nested.getShortProperty(), "shortProperty is 654");
        assertEquals("This is a string", nested.getStringProperty(), "stringProperty is \"This is a string\"");
    }

    /**
     * Test populate() method on scalar properties.
     */
    @Test
    public void testPopulateScalar() throws Exception {
        bean.set("nullProperty", "non-null value");

        final HashMap<String, Object> map = new HashMap<>();
        map.put("booleanProperty", "false");
        // booleanSecond is left at true
        map.put("doubleProperty", "432.0");
        // floatProperty is left at 123.0
        map.put("intProperty", "543");
        // longProperty is left at 321
        map.put("nullProperty", null);
        map.put("shortProperty", "654");
        // stringProperty is left at "This is a string"

        BeanUtils.populate(bean, map);

        final Boolean booleanProperty = (Boolean) bean.get("booleanProperty");
        assertFalse(booleanProperty.booleanValue(), "booleanProperty is false");
        final Boolean booleanSecond = (Boolean) bean.get("booleanSecond");
        assertTrue(booleanSecond.booleanValue(), "booleanSecond is true");
        final Double doubleProperty = (Double) bean.get("doubleProperty");
        assertEquals(432.0, doubleProperty.doubleValue(), 0.005, "doubleProperty is 432.0");
        final Float floatProperty = (Float) bean.get("floatProperty");
        assertEquals((float) 123.0, floatProperty.floatValue(), (float) 0.005, "floatProperty is 123.0");
        final Integer intProperty = (Integer) bean.get("intProperty");
        assertEquals(543, intProperty.intValue(), "intProperty is 543");
        final Long longProperty = (Long) bean.get("longProperty");
        assertEquals(321, longProperty.longValue(), "longProperty is 321");
        assertNull(bean.get("nullProperty"), "nullProperty is null");
        final Short shortProperty = (Short) bean.get("shortProperty");
        assertEquals((short) 654, shortProperty.shortValue(), "shortProperty is 654");
        assertEquals("This is a string", (String) bean.get("stringProperty"), "stringProperty is \"This is a string\"");
    }

    /**
     * Test setting a null property value.
     */
    @Test
    public void testSetPropertyNull() throws Exception {

        bean.set("nullProperty", "non-null value");
        BeanUtils.setProperty(bean, "nullProperty", null);
        assertNull(bean.get("nullProperty"), "nullProperty is null");

    }

    /**
     * Test calling setProperty() with null property values.
     */
    @Test
    public void testSetPropertyNullValues() throws Exception {

        Object oldValue;
        Object newValue;

        // Scalar value into array
        oldValue = PropertyUtils.getSimpleProperty(bean, "stringArray");
        BeanUtils.setProperty(bean, "stringArray", null);
        newValue = PropertyUtils.getSimpleProperty(bean, "stringArray");
        assertNotNull(newValue, "stringArray is not null");
        assertInstanceOf(String[].class, newValue, "stringArray of correct type");
        assertEquals(1, ((String[]) newValue).length, "stringArray length");
        PropertyUtils.setProperty(bean, "stringArray", oldValue);

        // Indexed value into array
        oldValue = PropertyUtils.getSimpleProperty(bean, "stringArray");
        BeanUtils.setProperty(bean, "stringArray[2]", null);
        newValue = PropertyUtils.getSimpleProperty(bean, "stringArray");
        assertNotNull(newValue, "stringArray is not null");
        assertInstanceOf(String[].class, newValue, "stringArray of correct type");
        assertEquals(5, ((String[]) newValue).length, "stringArray length");
        assertNull(((String[]) newValue)[2], "stringArray[2] is null");
        PropertyUtils.setProperty(bean, "stringArray", oldValue);

        // Value into scalar
        BeanUtils.setProperty(bean, "stringProperty", null);
        assertNull(BeanUtils.getProperty(bean, "stringProperty"), "stringProperty is now null");

    }

    /**
     * Test converting to and from primitive wrapper types.
     */
    @Test
    public void testSetPropertyOnPrimitiveWrappers() throws Exception {

        BeanUtils.setProperty(bean, "intProperty", Integer.valueOf(1));
        assertEquals(1, ((Integer) bean.get("intProperty")).intValue());
        BeanUtils.setProperty(bean, "stringProperty", Integer.valueOf(1));
        assertEquals(1, Integer.parseInt((String) bean.get("stringProperty")));

    }

}
