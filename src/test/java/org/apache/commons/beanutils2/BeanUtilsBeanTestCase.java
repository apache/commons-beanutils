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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils2.converters.ArrayConverter;
import org.apache.commons.beanutils2.converters.DateConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test Case for the BeanUtils class. The majority of these tests use instances of the TestBean class, so be sure to update the tests if you change the
 * characteristics of that class.
 * </p>
 *
 * <p>
 * Template for this stolen from Craigs PropertyUtilsTestCase
 * </p>
 *
 * <p>
 * Note that the tests are dependant upon the static aspects (such as array sizes...) of the TestBean.java class, so ensure than all changes to TestBean are
 * reflected here.
 * </p>
 *
 * <p>
 * So far, this test case has tests for the following methods of the {@code BeanUtils} class:
 * </p>
 * <ul>
 * <li>getArrayProperty(Object bean, String name)</li>
 * </ul>
 */
public class BeanUtilsBeanTestCase {

    /**
     * The test bean for each test.
     */
    protected TestBean bean;

    /**
     * The set of properties that should be described.
     */
    protected String[] describes = { "booleanProperty", "booleanSecond", "byteProperty", "doubleProperty", "dupProperty", "floatProperty", "intArray",
            // "intIndexed",
            "longProperty", "listIndexed", "longProperty",
            // "mappedProperty",
            // "mappedIntProperty",
            "nested", "nullProperty", "readOnlyProperty", "shortProperty", "stringArray",
            // "stringIndexed",
            "stringProperty" };

    /** Test Calendar value */
    protected java.util.Calendar testCalendar;

    /** Test java.util.Date value */
    protected java.util.Date testUtilDate;

    /** Test String Date value */
    protected String testStringDate;

    // Ensure that the actual int[] matches the expected int[]
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
     * Use reflection to get the cause
     */
    private Throwable getCause(final Throwable t) throws Throwable {
        return (Throwable) PropertyUtils.getProperty(t, "cause");
    }

    /**
     * Catch a cause, initialize using BeanUtils.initCause() and throw new exception
     */
    private void initCauseAndThrowException(final String parent, final String cause) throws Throwable {
        try {
            throwException(cause);
        } catch (final Throwable e) {
            throw new Exception(parent, e);
        }
    }

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() {
        ConvertUtils.deregister();
        BeanUtilsBean.setInstance(new BeanUtilsBean());
        setUpShared();
    }

    /**
     * Shared Set up.
     */
    protected void setUpShared() {
        bean = new TestBean();

        final DateConverter dateConverter = new DateConverter(null);
        dateConverter.setLocale(Locale.US);
        dateConverter.setPattern("dd.MM.yyyy");
        ConvertUtils.register(dateConverter, java.util.Date.class);

        final ArrayConverter dateArrayConverter = new ArrayConverter(java.util.Date[].class, dateConverter, 0);
        ConvertUtils.register(dateArrayConverter, java.util.Date[].class);

        testCalendar = Calendar.getInstance();
        testCalendar.set(1992, 11, 28, 0, 0, 0);
        testCalendar.set(Calendar.MILLISECOND, 0);
        testUtilDate = testCalendar.getTime();
        testStringDate = "28.12.1992";
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        bean = null;
    }

    @Test
    public void testArrayPropertyConversion() throws Exception {
        final BeanUtilsBean beanUtils = new BeanUtilsBean(new ConvertUtilsBean(), new PropertyUtilsBean());

        final TestBean bean = new TestBean();
        final String[] results = beanUtils.getArrayProperty(bean, "intArray");

        final int[] values = bean.getIntArray();
        assertEquals(results.length, values.length, "Converted array size not equal to property array size.");
        for (int i = 0, size = values.length; i < size; i++) {
            assertEquals(values[i] + "", results[i], "Value " + i + " incorrectly converted ");
        }
    }

    /**
     * Test the copyProperties() method from a DynaBean.
     */
    @Test
    public void testCopyPropertiesDynaBean() throws Exception {

        // Set up an origin bean with customized properties
        final DynaClass dynaClass = DynaBeanUtilsTestCase.createDynaClass();
        DynaBean orig = null;
        orig = dynaClass.newInstance();
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
        assertEquals(false, bean.getBooleanProperty(), "Copied boolean property");
        assertEquals((byte) 111, bean.getByteProperty(), "Copied byte property");
        assertEquals(333.33, bean.getDoubleProperty(), 0.005, "Copied double property");
        assertEquals(333, bean.getIntProperty(), "Copied int property");
        assertEquals(3333, bean.getLongProperty(), "Copied long property");
        assertEquals((short) 33, bean.getShortProperty(), "Copied short property");
        assertEquals("Custom string", bean.getStringProperty(), "Copied string property");

        // Validate the results for array properties
        final String[] dupProperty = bean.getDupProperty();
        assertNotNull(dupProperty, "dupProperty present");
        assertEquals(3, dupProperty.length, "dupProperty length");
        assertEquals("New 0", dupProperty[0], "dupProperty[0]");
        assertEquals("New 1", dupProperty[1], "dupProperty[1]");
        assertEquals("New 2", dupProperty[2], "dupProperty[2]");
        final int[] intArray = bean.getIntArray();
        assertNotNull(intArray, "intArray present");
        assertEquals(3, intArray.length, "intArray length");
        assertEquals(100, intArray[0], "intArray[0]");
        assertEquals(200, intArray[1], "intArray[1]");
        assertEquals(300, intArray[2], "intArray[2]");
        final String[] stringArray = bean.getStringArray();
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
        assertEquals(false, bean.getBooleanProperty(), "booleanProperty");
        assertEquals((byte) 111, bean.getByteProperty(), "byteProperty");
        assertEquals(333.0, bean.getDoubleProperty(), 0.005, "doubleProperty");
        assertEquals((float) 222.0, bean.getFloatProperty(), (float) 0.005, "floatProperty");
        assertEquals(111, bean.getIntProperty(), "longProperty");
        assertEquals(444, bean.getLongProperty(), "longProperty");
        assertEquals((short) 555, bean.getShortProperty(), "shortProperty");
        assertEquals("New String Property", bean.getStringProperty(), "stringProperty");

        // Indexed Properties
        final String[] dupProperty = bean.getDupProperty();
        assertNotNull(dupProperty, "dupProperty present");
        assertEquals(3, dupProperty.length, "dupProperty length");
        assertEquals("New 0", dupProperty[0], "dupProperty[0]");
        assertEquals("New 1", dupProperty[1], "dupProperty[1]");
        assertEquals("New 2", dupProperty[2], "dupProperty[2]");
        final int[] intArray = bean.getIntArray();
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
        assertEquals(false, bean.getBooleanProperty(), "Copied boolean property");
        assertEquals((byte) 111, bean.getByteProperty(), "Copied byte property");
        assertEquals(333.33, bean.getDoubleProperty(), 0.005, "Copied double property");
        assertEquals(333, bean.getIntProperty(), "Copied int property");
        assertEquals(3333, bean.getLongProperty(), "Copied long property");
        assertEquals((short) 33, bean.getShortProperty(), "Copied short property");
        assertEquals("Custom string", bean.getStringProperty(), "Copied string property");

        // Validate the results for array properties
        final String[] dupProperty = bean.getDupProperty();
        assertNotNull(dupProperty, "dupProperty present");
        assertEquals(3, dupProperty.length, "dupProperty length");
        assertEquals("New 0", dupProperty[0], "dupProperty[0]");
        assertEquals("New 1", dupProperty[1], "dupProperty[1]");
        assertEquals("New 2", dupProperty[2], "dupProperty[2]");
        final int[] intArray = bean.getIntArray();
        assertNotNull(intArray, "intArray present");
        assertEquals(3, intArray.length, "intArray length");
        assertEquals(100, intArray[0], "intArray[0]");
        assertEquals(200, intArray[1], "intArray[1]");
        assertEquals(300, intArray[2], "intArray[2]");
        final String[] stringArray = bean.getStringArray();
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

        BeanUtils.copyProperty(bean, "byteProperty", Byte.valueOf((byte) 123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.copyProperty(bean, "byteProperty", Double.valueOf(123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.copyProperty(bean, "byteProperty", Float.valueOf(123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.copyProperty(bean, "byteProperty", Integer.valueOf(123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.copyProperty(bean, "byteProperty", Long.valueOf(123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.copyProperty(bean, "byteProperty", Short.valueOf((short) 123));
        assertEquals((byte) 123, bean.getByteProperty());

    }

    /**
     * Test {@code copyProperty()} conversion.
     */
    @Test
    public void testCopyPropertyConvert() throws Exception {
        BeanUtils.copyProperty(bean, "dateProperty", testCalendar);
        assertEquals(testUtilDate, bean.getDateProperty(), "Calendar --> java.util.Date");
    }

    /**
     * Test {@code copyProperty()} converting from a String.
     */
    @Test
    public void testCopyPropertyConvertFromString() throws Exception {
        BeanUtils.copyProperty(bean, "dateProperty", testStringDate);
        assertEquals(testUtilDate, bean.getDateProperty(), "String --> java.util.Date");
    }

    /**
     * Test {@code copyProperty()} converting to a String.
     */
    @Test
    public void testCopyPropertyConvertToString() throws Exception {
        BeanUtils.copyProperty(bean, "stringProperty", testUtilDate);
        assertEquals(testStringDate, bean.getStringProperty(), "java.util.Date --> String");
    }

    /**
     * Test {@code copyProperty()} converting to a String.
     */
    @Test
    public void testCopyPropertyConvertToStringArray() throws Exception {
        bean.setStringArray(null);
        BeanUtils.copyProperty(bean, "stringArray", new java.util.Date[] { testUtilDate });
        assertEquals(1, bean.getStringArray().length, "java.util.Date[] --> String[] length");
        assertEquals(testStringDate, bean.getStringArray()[0], "java.util.Date[] --> String[] value ");
    }

    /**
     * Test {@code copyProperty()} converting to a String on indexed property
     */
    @Test
    public void testCopyPropertyConvertToStringIndexed() throws Exception {
        bean.setStringArray(new String[1]);
        BeanUtils.copyProperty(bean, "stringArray[0]", testUtilDate);
        assertEquals(1, bean.getStringArray().length, "java.util.Date[] --> String[] length");
        assertEquals(testStringDate, bean.getStringArray()[0], "java.util.Date[] --> String[] value ");
    }

    /**
     * Test narrowing and widening conversions on double.
     */
    @Test
    public void testCopyPropertyDouble() throws Exception {

        BeanUtils.copyProperty(bean, "doubleProperty", Byte.valueOf((byte) 123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.copyProperty(bean, "doubleProperty", Double.valueOf(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.copyProperty(bean, "doubleProperty", Float.valueOf(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.copyProperty(bean, "doubleProperty", Integer.valueOf(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.copyProperty(bean, "doubleProperty", Long.valueOf(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.copyProperty(bean, "doubleProperty", Short.valueOf((short) 123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);

    }

    /**
     * Test narrowing and widening conversions on float.
     */
    @Test
    public void testCopyPropertyFloat() throws Exception {

        BeanUtils.copyProperty(bean, "floatProperty", Byte.valueOf((byte) 123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.copyProperty(bean, "floatProperty", Double.valueOf(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.copyProperty(bean, "floatProperty", Float.valueOf(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.copyProperty(bean, "floatProperty", Integer.valueOf(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.copyProperty(bean, "floatProperty", Long.valueOf(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.copyProperty(bean, "floatProperty", Short.valueOf((short) 123));
        assertEquals(123, bean.getFloatProperty(), 0.005);

    }

    /**
     * Test narrowing and widening conversions on int.
     */
    @Test
    public void testCopyPropertyInteger() throws Exception {

        BeanUtils.copyProperty(bean, "longProperty", Byte.valueOf((byte) 123));
        assertEquals(123, bean.getIntProperty());
        BeanUtils.copyProperty(bean, "longProperty", Double.valueOf(123));
        assertEquals(123, bean.getIntProperty());
        BeanUtils.copyProperty(bean, "longProperty", Float.valueOf(123));
        assertEquals(123, bean.getIntProperty());
        BeanUtils.copyProperty(bean, "longProperty", Integer.valueOf(123));
        assertEquals(123, bean.getIntProperty());
        BeanUtils.copyProperty(bean, "longProperty", Long.valueOf(123));
        assertEquals(123, bean.getIntProperty());
        BeanUtils.copyProperty(bean, "longProperty", Short.valueOf((short) 123));
        assertEquals(123, bean.getIntProperty());

    }

    /**
     * Test narrowing and widening conversions on long.
     */
    @Test
    public void testCopyPropertyLong() throws Exception {

        BeanUtils.copyProperty(bean, "longProperty", Byte.valueOf((byte) 123));
        assertEquals(123, bean.getLongProperty());
        BeanUtils.copyProperty(bean, "longProperty", Double.valueOf(123));
        assertEquals(123, bean.getLongProperty());
        BeanUtils.copyProperty(bean, "longProperty", Float.valueOf(123));
        assertEquals(123, bean.getLongProperty());
        BeanUtils.copyProperty(bean, "longProperty", Integer.valueOf(123));
        assertEquals(123, bean.getLongProperty());
        BeanUtils.copyProperty(bean, "longProperty", Long.valueOf(123));
        assertEquals(123, bean.getLongProperty());
        BeanUtils.copyProperty(bean, "longProperty", Short.valueOf((short) 123));
        assertEquals(123, bean.getLongProperty());

    }

    /**
     * Test copying a property using a nested indexed array expression, with and without conversions.
     */
    @Test
    public void testCopyPropertyNestedIndexedArray() throws Exception {

        final int[] origArray = { 0, 10, 20, 30, 40 };
        final int[] intArray = { 0, 0, 0 };
        bean.getNested().setIntArray(intArray);
        final int[] intChanged = { 0, 0, 0 };

        // No conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", Integer.valueOf(1));
        checkIntArray(bean.getIntArray(), origArray);
        intChanged[1] = 1;
        checkIntArray(bean.getNested().getIntArray(), intChanged);

        // Widening conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", Byte.valueOf((byte) 2));
        checkIntArray(bean.getIntArray(), origArray);
        intChanged[1] = 2;
        checkIntArray(bean.getNested().getIntArray(), intChanged);

        // Narrowing conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", Long.valueOf(3));
        checkIntArray(bean.getIntArray(), origArray);
        intChanged[1] = 3;
        checkIntArray(bean.getNested().getIntArray(), intChanged);

        // String conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", "4");
        checkIntArray(bean.getIntArray(), origArray);
        intChanged[1] = 4;
        checkIntArray(bean.getNested().getIntArray(), intChanged);

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
        checkMap(bean.getMapProperty(), origMap);
        changedMap.put("Second Key", "New Second Value");
        checkMap(bean.getNested().getMapProperty(), changedMap);

    }

    /**
     * Test copying a property using a nested simple expression, with and without conversions.
     */
    @Test
    public void testCopyPropertyNestedSimple() throws Exception {

        bean.setIntProperty(0);
        bean.getNested().setIntProperty(0);

        // No conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", Integer.valueOf(1));
        assertNotNull(bean.getNested());
        assertEquals(0, bean.getIntProperty());
        assertEquals(1, bean.getNested().getIntProperty());

        // Widening conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", Byte.valueOf((byte) 2));
        assertNotNull(bean.getNested());
        assertEquals(0, bean.getIntProperty());
        assertEquals(2, bean.getNested().getIntProperty());

        // Narrowing conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", Long.valueOf(3));
        assertNotNull(bean.getNested());
        assertEquals(0, bean.getIntProperty());
        assertEquals(3, bean.getNested().getIntProperty());

        // String conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", "4");
        assertNotNull(bean.getNested());
        assertEquals(0, bean.getIntProperty());
        assertEquals(4, bean.getNested().getIntProperty());

    }

    /**
     * Test copying a null property value.
     */
    @Test
    public void testCopyPropertyNull() throws Exception {

        bean.setNullProperty("non-null value");
        BeanUtils.copyProperty(bean, "nullProperty", null);
        assertNull(bean.getNullProperty(), "nullProperty is null");

    }

    /**
     * Test narrowing and widening conversions on short.
     */
    @Test
    public void testCopyPropertyShort() throws Exception {

        BeanUtils.copyProperty(bean, "shortProperty", Byte.valueOf((byte) 123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.copyProperty(bean, "shortProperty", Double.valueOf(123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.copyProperty(bean, "shortProperty", Float.valueOf(123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.copyProperty(bean, "shortProperty", Integer.valueOf(123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.copyProperty(bean, "shortProperty", Long.valueOf(123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.copyProperty(bean, "shortProperty", Short.valueOf((short) 123));
        assertEquals((short) 123, bean.getShortProperty());

    }

    /**
     * Test copying a new value to a write-only property, with and without conversions.
     */
    @Test
    public void testCopyPropertyWriteOnly() throws Exception {

        bean.setWriteOnlyProperty("Original value");

        // No conversion required
        BeanUtils.copyProperty(bean, "writeOnlyProperty", "New value");
        assertEquals("New value", bean.getWriteOnlyPropertyValue());

        // Integer->String conversion required
        BeanUtils.copyProperty(bean, "writeOnlyProperty", Integer.valueOf(123));
        assertEquals("123", bean.getWriteOnlyPropertyValue());

    }

    /**
     * Test the describe() method.
     */
    @Test
    public void testDescribe() throws Exception {
        assertTrue(BeanUtils.describe(null).isEmpty());
        Map<String, String> map = null;
        map = BeanUtils.describe(bean);
        // Verify existence of all the properties that should be present
        for (final String describe : describes) {
            assertTrue(map.containsKey(describe), "Property '" + describe + "' is present");
        }
        assertTrue(!map.containsKey("writeOnlyProperty"), "Property 'writeOnlyProperty' is not present");
        // Verify the values of scalar properties
        assertEquals("true", map.get("booleanProperty"), "Value of 'booleanProperty'");
        assertEquals("121", map.get("byteProperty"), "Value of 'byteProperty'");
        assertEquals("321.0", map.get("doubleProperty"), "Value of 'doubleProperty'");
        assertEquals("123.0", map.get("floatProperty"), "Value of 'floatProperty'");
        assertEquals("123", map.get("intProperty"), "Value of 'intProperty'");
        assertEquals("321", map.get("longProperty"), "Value of 'longProperty'");
        assertEquals("987", map.get("shortProperty"), "Value of 'shortProperty'");
        assertEquals("This is a string", map.get("stringProperty"), "Value of 'stringProperty'");
    }

    /**
     * tests the string and int arrays of TestBean
     */
    @Test
    public void testGetArrayProperty() throws Exception {
        String[] arr = BeanUtils.getArrayProperty(bean, "stringArray");
        final String[] comp = bean.getStringArray();

        assertEquals(comp.length, arr.length, "String array length = " + comp.length);

        arr = BeanUtils.getArrayProperty(bean, "intArray");
        final int[] iarr = bean.getIntArray();

        assertEquals(iarr.length, arr.length, "String array length = " + iarr.length);

        // Test property which isn't array or collection
        arr = BeanUtils.getArrayProperty(bean, "shortProperty");
        final String shortAsString = "" + bean.getShortProperty();
        assertEquals(1, arr.length, "Short List Test lth");
        assertEquals(shortAsString, arr[0], "Short Test value");

        // Test comma delimited list
        bean.setStringProperty("ABC");
        arr = BeanUtils.getArrayProperty(bean, "stringProperty");
        assertEquals(1, arr.length, "Delimited List Test lth");
        assertEquals("ABC", arr[0], "Delimited List Test value1");
    }

    /**
     * Test {@code getArrayProperty()} converting to a String.
     */
    @Test
    public void testGetArrayPropertyDate() throws Exception {
        String[] value = null;
        bean.setDateArrayProperty(new java.util.Date[] { testUtilDate });
        value = BeanUtils.getArrayProperty(bean, "dateArrayProperty");
        assertEquals(1, value.length, "java.util.Date[] --> String[] length");
        assertEquals(testStringDate, value[0], "java.util.Date[] --> String[] value ");
    }

    /**
     * tests getting a 'whatever' property
     */
    @Test
    public void testGetGeneralProperty() throws Exception {
        final String val = BeanUtils.getProperty(bean, "nested.intIndexed[2]");
        final String comp = String.valueOf(bean.getIntIndexed(2));

        assertEquals(val, comp, "nested.intIndexed[2] == " + comp);
    }

    /**
     * tests getting an indexed property
     */
    @Test
    public void testGetIndexedProperty1() throws Exception {
        String val = BeanUtils.getIndexedProperty(bean, "intIndexed[3]");
        String comp = String.valueOf(bean.getIntIndexed(3));
        assertEquals(val, comp, "intIndexed[3] == " + comp);

        val = BeanUtils.getIndexedProperty(bean, "stringIndexed[3]");
        comp = bean.getStringIndexed(3);
        assertEquals(val, comp, "stringIndexed[3] == " + comp);
    }

    /**
     * tests getting an indexed property
     */
    @Test
    public void testGetIndexedProperty2() throws Exception {
        String val = BeanUtils.getIndexedProperty(bean, "intIndexed", 3);
        String comp = String.valueOf(bean.getIntIndexed(3));

        assertEquals(val, comp, "intIndexed,3 == " + comp);

        val = BeanUtils.getIndexedProperty(bean, "stringIndexed", 3);
        comp = bean.getStringIndexed(3);

        assertEquals(val, comp, "stringIndexed,3 == " + comp);
    }

    /**
     * Test {@code getArrayProperty()} converting to a String.
     */
    @Test
    public void testGetIndexedPropertyDate() throws Exception {
        String value = null;
        bean.setDateArrayProperty(new java.util.Date[] { testUtilDate });
        value = BeanUtils.getIndexedProperty(bean, "dateArrayProperty[0]");
        assertEquals(testStringDate, value, "java.util.Date[0] --> String");
    }

    @Test
    public void testGetMappedProperty2Args() throws Exception {
        assertThrows(NullPointerException.class, () -> BeanUtils.getMappedProperty(null, null));
        assertThrows(NullPointerException.class, () -> BeanUtils.getMappedProperty(null, ""));
        assertThrows(NullPointerException.class, () -> BeanUtils.getMappedProperty("", null));
    }

    @Test
    public void testGetMappedProperty3Args() throws Exception {
        assertThrows(NullPointerException.class, () -> BeanUtils.getMappedProperty(null, null));
        assertThrows(NullPointerException.class, () -> BeanUtils.getMappedProperty(null, "", null));
        assertThrows(NullPointerException.class, () -> BeanUtils.getMappedProperty("", null, null));
    }

    /**
     * tests getting a nested property
     */
    @Test
    public void testGetNestedProperty() throws Exception {
        final String val = BeanUtils.getNestedProperty(bean, "nested.stringProperty");
        final String comp = bean.getNested().getStringProperty();
        assertEquals(val, comp, "nested.StringProperty == " + comp);
    }

    /**
     * tests getting a 'whatever' property
     */
    @Test
    public void testGetSimpleProperty() throws Exception {
        final String val = BeanUtils.getSimpleProperty(bean, "shortProperty");
        final String comp = String.valueOf(bean.getShortProperty());

        assertEquals(val, comp, "shortProperty == " + comp);
    }

    /**
     * Test {@code getSimpleProperty()} converting to a String.
     */
    @Test
    public void testGetSimplePropertyDate() throws Exception {
        String value = null;
        bean.setDateProperty(testUtilDate);
        value = BeanUtils.getSimpleProperty(bean, "dateProperty");
        assertEquals(testStringDate, value, "java.util.Date --> String");
    }

    @Test
    public void testMappedProperty() throws Exception {
        final MappedPropertyTestBean bean = new MappedPropertyTestBean();
        BeanUtils.setProperty(bean, "mapproperty(this.that.the-other)", "some.dotty.value");
        assertEquals("some.dotty.value", bean.getMapproperty("this.that.the-other"), "Mapped property set correctly");
    }

    @Test
    public void testPopulate() throws Exception {
        BeanUtilsBean.getInstance().populate(null, null);
        BeanUtilsBean.getInstance().populate("", null);
        BeanUtilsBean.getInstance().populate(null, new HashMap<>());
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

        assertEquals(100, bean.getIntIndexed(0), "intIndexed[0] is 100");
        assertEquals(10, bean.getIntIndexed(1), "intIndexed[1] is 10");
        assertEquals(120, bean.getIntIndexed(2), "intIndexed[2] is 120");
        assertEquals(30, bean.getIntIndexed(3), "intIndexed[3] is 30");
        assertEquals(140, bean.getIntIndexed(4), "intIndexed[4] is 140");

        map.clear();
        map.put("stringIndexed[1]", "New String 1");
        map.put("stringIndexed[3]", "New String 3");

        BeanUtils.populate(bean, map);

        assertEquals("String 0", bean.getStringIndexed(0), "stringIndexed[0] is \"String 0\"");
        assertEquals("New String 1", bean.getStringIndexed(1), "stringIndexed[1] is \"New String 1\"");
        assertEquals("String 2", bean.getStringIndexed(2), "stringIndexed[2] is \"String 2\"");
        assertEquals("New String 3", bean.getStringIndexed(3), "stringIndexed[3] is \"New String 3\"");
        assertEquals("String 4", bean.getStringIndexed(4), "stringIndexed[4] is \"String 4\"");
    }

    /**
     * Test populate() method on array properties as a whole.
     */
    @Test
    public void testPopulateArrayProperties() throws Exception {
        final HashMap<String, Object> map = new HashMap<>();
        int[] intArray = { 123, 456, 789 };
        map.put("intArray", intArray);
        String[] stringArray = { "New String 0", "New String 1" };
        map.put("stringArray", stringArray);

        BeanUtils.populate(bean, map);

        intArray = bean.getIntArray();
        assertNotNull(intArray, "intArray is present");
        assertEquals(3, intArray.length, "intArray length");
        assertEquals(123, intArray[0], "intArray[0]");
        assertEquals(456, intArray[1], "intArray[1]");
        assertEquals(789, intArray[2], "intArray[2]");
        stringArray = bean.getStringArray();
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

        assertEquals("New First Value", bean.getMappedProperty("First Key"), "mappedProperty(First Key)");
        assertEquals("Second Value", bean.getMappedProperty("Second Key"), "mappedProperty(Second Key)");
        assertEquals("New Third Value", bean.getMappedProperty("Third Key"), "mappedProperty(Third Key)");
        assertNull(bean.getMappedProperty("Fourth Key"), "mappedProperty(Fourth Key");
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
        map.put("nested.writeOnlyProperty", "New writeOnlyProperty value");

        BeanUtils.populate(bean, map);

        assertTrue(!bean.getNested().getBooleanProperty(), "booleanProperty is false");
        assertTrue(bean.getNested().isBooleanSecond(), "booleanSecond is true");
        assertEquals(432.0, bean.getNested().getDoubleProperty(), 0.005, "doubleProperty is 432.0");
        assertEquals((float) 123.0, bean.getNested().getFloatProperty(), (float) 0.005, "floatProperty is 123.0");
        assertEquals(543, bean.getNested().getIntProperty(), "intProperty is 543");
        assertEquals(321, bean.getNested().getLongProperty(), "longProperty is 321");
        assertEquals((short) 654, bean.getNested().getShortProperty(), "shortProperty is 654");
        assertEquals("This is a string", bean.getNested().getStringProperty(), "stringProperty is \"This is a string\"");
        assertEquals("New writeOnlyProperty value", bean.getNested().getWriteOnlyPropertyValue(), "writeOnlyProperty is \"New writeOnlyProperty value\"");
    }

    /**
     * Test populate() method on scalar properties.
     */
    @Test
    public void testPopulateScalar() throws Exception {
        bean.setNullProperty("Non-null value");

        final HashMap<String, Object> map = new HashMap<>();
        map.put("booleanProperty", "false");
        // booleanSecond is left at true
        map.put("byteProperty", "111");
        map.put("doubleProperty", "432.0");
        // floatProperty is left at 123.0
        map.put("intProperty", "543");
        map.put("longProperty", "");
        map.put("nullProperty", null);
        map.put("shortProperty", "654");
        // stringProperty is left at "This is a string"
        map.put("writeOnlyProperty", "New writeOnlyProperty value");
        map.put("readOnlyProperty", "New readOnlyProperty value");

        BeanUtils.populate(bean, map);

        assertTrue(!bean.getBooleanProperty(), "booleanProperty is false");
        assertTrue(bean.isBooleanSecond(), "booleanSecond is true");
        assertEquals((byte) 111, bean.getByteProperty(), "byteProperty is 111");
        assertEquals(432.0, bean.getDoubleProperty(), 0.005, "doubleProperty is 432.0");
        assertEquals((float) 123.0, bean.getFloatProperty(), (float) 0.005, "floatProperty is 123.0");
        assertEquals(543, bean.getIntProperty(), "intProperty is 543");
        assertEquals(0, bean.getLongProperty(), "longProperty is 0");
        assertNull(bean.getNullProperty(), "nullProperty is null");
        assertEquals((short) 654, bean.getShortProperty(), "shortProperty is 654");
        assertEquals("This is a string", bean.getStringProperty(), "stringProperty is \"This is a string\"");
        assertEquals("New writeOnlyProperty value", bean.getWriteOnlyPropertyValue(), "writeOnlyProperty is \"New writeOnlyProperty value\"");
        assertEquals("Read Only String Property", bean.getReadOnlyProperty(), "readOnlyProperty is \"Read Only String Property\"");
    }

    /** Tests that separate instances can register separate instances */
    @Test
    public void testSeparateInstances() throws Exception {
        final BeanUtilsBean utilsOne = new BeanUtilsBean(new ConvertUtilsBean(), new PropertyUtilsBean());
        final BeanUtilsBean utilsTwo = new BeanUtilsBean(new ConvertUtilsBean(), new PropertyUtilsBean());

        final TestBean bean = new TestBean();

        // Make sure what we're testing works
        bean.setBooleanProperty(false);
        utilsOne.setProperty(bean, "booleanProperty", "true");
        assertEquals(bean.getBooleanProperty(), true, "Set property failed (1)");

        bean.setBooleanProperty(false);
        utilsTwo.setProperty(bean, "booleanProperty", "true");
        assertEquals(bean.getBooleanProperty(), true, "Set property failed (2)");

        // now change the registered conversion

        utilsOne.getConvertUtils().register(new ThrowExceptionConverter(), Boolean.TYPE);
        bean.setBooleanProperty(false);
        assertThrows(PassTestException.class, () ->        utilsOne.setProperty(bean, "booleanProperty", "true"));

        // make sure that this conversion has no been registered in the other instance
        bean.setBooleanProperty(false);
        utilsTwo.setProperty(bean, "booleanProperty", "true");
        assertEquals(bean.getBooleanProperty(), true, "Set property failed (3)");
    }

    /**
     * Test setting a value out of a mapped Map
     */
    @Test
    public void testSetMappedMap() throws Exception {
        final TestBean bean = new TestBean();
        final Map<String, Object> map = new HashMap<>();
        map.put("sub-key-1", "sub-value-1");
        map.put("sub-key-2", "sub-value-2");
        map.put("sub-key-3", "sub-value-3");
        bean.getMapProperty().put("mappedMap", map);

        assertEquals("sub-value-3", ((Map<?, ?>) bean.getMapProperty().get("mappedMap")).get("sub-key-3"), "BEFORE");
        BeanUtils.setProperty(bean, "mapProperty(mappedMap)(sub-key-3)", "SUB-KEY-3-UPDATED");
        assertEquals("SUB-KEY-3-UPDATED", ((Map<?, ?>) bean.getMapProperty().get("mappedMap")).get("sub-key-3"), "AFTER");
    }

    /**
     * Test narrowing and widening conversions on byte.
     */
    @Test
    public void testSetPropertyByte() throws Exception {

        BeanUtils.setProperty(bean, "byteProperty", Byte.valueOf((byte) 123));
        assertEquals((byte) 123, bean.getByteProperty());
        /*
         * BeanUtils.setProperty(bean, "byteProperty", new Double((double) 123)); assertEquals((byte) 123, bean.getByteProperty()); BeanUtils.setProperty(bean,
         * "byteProperty", new Float((float) 123)); assertEquals((byte) 123, bean.getByteProperty());
         */
        BeanUtils.setProperty(bean, "byteProperty", Integer.valueOf(123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.setProperty(bean, "byteProperty", Long.valueOf(123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.setProperty(bean, "byteProperty", Short.valueOf((short) 123));
        assertEquals((byte) 123, bean.getByteProperty());

    }

    /**
     * Test {@code setProperty()} conversion.
     */
    @Test
    public void testSetPropertyConvert() throws Exception {
        BeanUtils.setProperty(bean, "dateProperty", testCalendar);
        assertEquals(testUtilDate, bean.getDateProperty(), "Calendar --> java.util.Date");
    }

    /**
     * Test {@code setProperty()} converting from a String.
     */
    @Test
    public void testSetPropertyConvertFromString() throws Exception {
        BeanUtils.setProperty(bean, "dateProperty", testStringDate);
        assertEquals(testUtilDate, bean.getDateProperty(), "String --> java.util.Date");
    }

    /**
     * Test {@code setProperty()} converting to a String.
     */
    @Test
    public void testSetPropertyConvertToString() throws Exception {
        BeanUtils.setProperty(bean, "stringProperty", testUtilDate);
        assertEquals(testStringDate, bean.getStringProperty(), "java.util.Date --> String");
    }

    /**
     * Test {@code setProperty()} converting to a String array.
     */
    @Test
    public void testSetPropertyConvertToStringArray() throws Exception {
        bean.setStringArray(null);
        BeanUtils.setProperty(bean, "stringArray", new java.util.Date[] { testUtilDate });
        assertEquals(1, bean.getStringArray().length, "java.util.Date[] --> String[] length");
        assertEquals(testStringDate, bean.getStringArray()[0], "java.util.Date[] --> String[] value ");
    }

    /**
     * Test {@code setProperty()} converting to a String on indexed property
     */
    @Test
    public void testSetPropertyConvertToStringIndexed() throws Exception {
        bean.setStringArray(new String[1]);
        BeanUtils.setProperty(bean, "stringArray[0]", testUtilDate);
        assertEquals(testStringDate, bean.getStringArray()[0], "java.util.Date --> String[]");
    }

    /**
     * Test narrowing and widening conversions on double.
     */
    @Test
    public void testSetPropertyDouble() throws Exception {

        BeanUtils.setProperty(bean, "doubleProperty", Byte.valueOf((byte) 123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", Double.valueOf(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", Float.valueOf(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", Integer.valueOf(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", Long.valueOf(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", Short.valueOf((short) 123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);

    }

    /**
     * Test narrowing and widening conversions on float.
     */
    @Test
    public void testSetPropertyFloat() throws Exception {

        BeanUtils.setProperty(bean, "floatProperty", Byte.valueOf((byte) 123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", Double.valueOf(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", Float.valueOf(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", Integer.valueOf(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", Long.valueOf(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", Short.valueOf((short) 123));
        assertEquals(123, bean.getFloatProperty(), 0.005);

    }

    /**
     * Test narrowing and widening conversions on int.
     */
    @Test
    public void testSetPropertyInteger() throws Exception {

        BeanUtils.setProperty(bean, "longProperty", Byte.valueOf((byte) 123));
        assertEquals(123, bean.getIntProperty());
        /*
         * BeanUtils.setProperty(bean, "longProperty", new Double((double) 123)); assertEquals((int) 123, bean.getIntProperty()); BeanUtils.setProperty(bean,
         * "longProperty", new Float((float) 123)); assertEquals((int) 123, bean.getIntProperty());
         */
        BeanUtils.setProperty(bean, "longProperty", Integer.valueOf(123));
        assertEquals(123, bean.getIntProperty());
        BeanUtils.setProperty(bean, "longProperty", Long.valueOf(123));
        assertEquals(123, bean.getIntProperty());
        BeanUtils.setProperty(bean, "longProperty", Short.valueOf((short) 123));
        assertEquals(123, bean.getIntProperty());

    }

    /**
     * Test narrowing and widening conversions on long.
     */
    @Test
    public void testSetPropertyLong() throws Exception {

        BeanUtils.setProperty(bean, "longProperty", Byte.valueOf((byte) 123));
        assertEquals(123, bean.getLongProperty());
        /*
         * BeanUtils.setProperty(bean, "longProperty", new Double((double) 123)); assertEquals((long) 123, bean.getLongProperty()); BeanUtils.setProperty(bean,
         * "longProperty", new Float((float) 123)); assertEquals((long) 123, bean.getLongProperty());
         */
        BeanUtils.setProperty(bean, "longProperty", Integer.valueOf(123));
        assertEquals(123, bean.getLongProperty());
        BeanUtils.setProperty(bean, "longProperty", Long.valueOf(123));
        assertEquals(123, bean.getLongProperty());
        BeanUtils.setProperty(bean, "longProperty", Short.valueOf((short) 123));
        assertEquals(123, bean.getLongProperty());

    }

    /**
     * Test setting a null property value.
     */
    @Test
    public void testSetPropertyNull() throws Exception {

        bean.setNullProperty("non-null value");
        BeanUtils.setProperty(bean, "nullProperty", null);
        assertNull(bean.getNullProperty(), "nullProperty is null");

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
        assertEquals(1, bean.getIntProperty());
        BeanUtils.setProperty(bean, "stringProperty", Integer.valueOf(1));
        assertEquals(1, Integer.parseInt(bean.getStringProperty()));

    }

    /**
     * Test narrowing and widening conversions on short.
     */
    @Test
    public void testSetPropertyShort() throws Exception {

        BeanUtils.setProperty(bean, "shortProperty", Byte.valueOf((byte) 123));
        assertEquals((short) 123, bean.getShortProperty());
        /*
         * BeanUtils.setProperty(bean, "shortProperty", new Double((double) 123)); assertEquals((short) 123, bean.getShortProperty());
         * BeanUtils.setProperty(bean, "shortProperty", new Float((float) 123)); assertEquals((short) 123, bean.getShortProperty());
         */
        BeanUtils.setProperty(bean, "shortProperty", Integer.valueOf(123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.setProperty(bean, "shortProperty", Long.valueOf(123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.setProperty(bean, "shortProperty", Short.valueOf((short) 123));
        assertEquals((short) 123, bean.getShortProperty());

    }

    /**
     * Test setting a String value to a String array property
     */
    @Test
    public void testSetPropertyStringToArray() throws Exception {
        BeanUtils.setProperty(bean, "stringArray", "ABC,DEF,GHI");
        final String[] strArray = bean.getStringArray();
        assertEquals(3, strArray.length, "length");
        assertEquals("ABC", strArray[0], "value[0]");
        assertEquals("DEF", strArray[1], "value[1]");
        assertEquals("GHI", strArray[2], "value[2]");

        BeanUtils.setProperty(bean, "intArray", "0, 10, 20, 30, 40");
        final int[] intArray = bean.getIntArray();
        assertEquals(5, intArray.length, "length");
        assertEquals(0, intArray[0], "value[0]");
        assertEquals(10, intArray[1], "value[1]");
        assertEquals(20, intArray[2], "value[2]");
        assertEquals(30, intArray[3], "value[3]");
        assertEquals(40, intArray[4], "value[4]");
    }

    /**
     * Test setting a new value to a write-only property, with and without conversions.
     */
    @Test
    public void testSetPropertyWriteOnly() throws Exception {

        bean.setWriteOnlyProperty("Original value");

        // No conversion required
        BeanUtils.setProperty(bean, "writeOnlyProperty", "New value");
        assertEquals("New value", bean.getWriteOnlyPropertyValue());

        // Integer->String conversion required
        BeanUtils.setProperty(bean, "writeOnlyProperty", Integer.valueOf(123));
        assertEquals("123", bean.getWriteOnlyPropertyValue());

    }

    /**
     * Throw an exception with the specified message.
     */
    private void throwException(final String msg) throws Throwable {
        throw new Exception(msg);
    }
}
