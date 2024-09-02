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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.beanutils2.converters.DateConverter;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConvertUtilsTestCase {

    private void checkIntegerArray(final Object value, final int[] intArray) {

        assertNotNull(value, "Returned value is not null");
        assertEquals(intArray.getClass(), value.getClass(), "Returned value is int[]");
        final int[] results = (int[]) value;
        assertEquals(intArray.length, results.length, "Returned array length");
        for (int i = 0; i < intArray.length; i++) {
            assertEquals(intArray[i], results[i], "Returned array value " + i);
        }

    }

    private void checkStringArray(final Object value, final String[] stringArray) {

        assertNotNull(value, "Returned value is not null");
        assertEquals(stringArray.getClass(), value.getClass(), "Returned value is String[]");
        final String[] results = (String[]) value;
        assertEquals(stringArray.length, results.length, "Returned array length");
        for (int i = 0; i < stringArray.length; i++) {
            assertEquals(stringArray[i], results[i], "Returned array value " + i);
        }

    }

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() {
        BeanUtilsBean.setInstance(new BeanUtilsBean());
        ConvertUtils.deregister();

    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        // No action required
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    // We need to use raw types in order to test legacy converters
    public void testConvertToString() throws Exception {
        final Converter dummyConverter = (type, value) -> value;

        final Converter fooConverter = (type, value) -> "Foo-Converter";

        final DateConverter dateConverter = new DateConverter();
        dateConverter.setLocale(Locale.US);

        final ConvertUtilsBean utils = new ConvertUtilsBean();
        utils.register(dateConverter, java.util.Date.class);
        utils.register(fooConverter, String.class);

        // Convert using registered DateConverter
        final java.util.Date today = new java.util.Date();
        final DateFormat fmt = new SimpleDateFormat("M/d/yy"); /* US Short Format */
        final String expected = fmt.format(today);
        assertEquals(expected, utils.convert(today, String.class), "DateConverter M/d/yy");

        // Date converter doesn't do String conversion - use String Converter
        utils.register(dummyConverter, java.util.Date.class);
        assertEquals("Foo-Converter", utils.convert(today, String.class), "Date Converter doesn't do String conversion");

        // No registered Date converter - use String Converter
        utils.deregister(java.util.Date.class);
        assertEquals("Foo-Converter", utils.convert(today, String.class), "No registered Date converter");

        // String Converter doesn't do Strings!!!
        utils.register(dummyConverter, String.class);
        assertEquals(today.toString(), utils.convert(today, String.class), "String Converter doesn't do Strings!!!");

        // No registered Date or String converter - use Object's toString()
        utils.deregister(String.class);
        assertEquals(today.toString(), utils.convert(today, String.class), "Object's toString()");

    }

    /**
     * Tests a conversion to an unsupported target type.
     */
    @Test
    public void testConvertUnsupportedTargetType() {
        final ConvertUtilsBean utils = new ConvertUtilsBean();
        final Object value = "A test value";
        assertSame(value, utils.convert(value, getClass()), "Got different object");
    }

    @Test
    public void testDeregisteringSingleConverter() throws Exception {
        // make sure that the test work ok before anything's changed
        final Object value = ConvertUtils.convert("true", Boolean.TYPE);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), true, "Standard conversion failed (1)");

        // we'll test deregister
        ConvertUtils.deregister(Boolean.TYPE);
        assertNull(ConvertUtils.lookup(Boolean.TYPE), "Converter should be null");

    }

    /**
     * Negative String to primitive integer array tests.
     */
    @Test
    public void testNegativeIntegerArray() {

        Object value;
        final int[] intArray = {};

        value = ConvertUtils.convert((String) null, intArray.getClass());
        checkIntegerArray(value, intArray);
        value = ConvertUtils.convert("a", intArray.getClass());
        checkIntegerArray(value, intArray);
        value = ConvertUtils.convert("{ a }", intArray.getClass());
        checkIntegerArray(value, intArray);
        value = ConvertUtils.convert("1a3", intArray.getClass());
        checkIntegerArray(value, intArray);
        value = ConvertUtils.convert("{ 1a3 }", intArray.getClass());
        checkIntegerArray(value, intArray);
        value = ConvertUtils.convert("0,1a3", intArray.getClass());
        checkIntegerArray(value, intArray);
        value = ConvertUtils.convert("{ 0, 1a3 }", intArray.getClass());
        checkIntegerArray(value, intArray);

    }

    /**
     * Negative scalar conversion tests. These rely on the standard default value conversions in ConvertUtils.
     */
    @Test
    public void testNegativeScalar() {

        Object value;

        value = ConvertUtils.convert("foo", Boolean.TYPE);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("foo", Boolean.class);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("foo", Byte.TYPE);
        assertInstanceOf(Byte.class, value);
        assertEquals(((Byte) value).byteValue(), (byte) 0);

        value = ConvertUtils.convert("foo", Byte.class);
        assertInstanceOf(Byte.class, value);
        assertEquals(((Byte) value).byteValue(), (byte) 0);

        assertThrows(ConversionException.class, () -> ConvertUtils.convert("org.apache.commons.beanutils2.Undefined", Class.class));

        value = ConvertUtils.convert("foo", Double.TYPE);
        assertInstanceOf(Double.class, value);
        assertEquals(((Double) value).doubleValue(), 0.0, 0.005);

        value = ConvertUtils.convert("foo", Double.class);
        assertInstanceOf(Double.class, value);
        assertEquals(((Double) value).doubleValue(), 0.0, 0.005);

        value = ConvertUtils.convert("foo", Float.TYPE);
        assertInstanceOf(Float.class, value);
        assertEquals(((Float) value).floatValue(), (float) 0.0, (float) 0.005);

        value = ConvertUtils.convert("foo", Float.class);
        assertInstanceOf(Float.class, value);
        assertEquals(((Float) value).floatValue(), (float) 0.0, (float) 0.005);

        value = ConvertUtils.convert("foo", Integer.TYPE);
        assertInstanceOf(Integer.class, value);
        assertEquals(((Integer) value).intValue(), 0);

        value = ConvertUtils.convert("foo", Integer.class);
        assertInstanceOf(Integer.class, value);
        assertEquals(((Integer) value).intValue(), 0);

        value = ConvertUtils.convert("foo", Byte.TYPE);
        assertInstanceOf(Byte.class, value);
        assertEquals(((Byte) value).byteValue(), (byte) 0);

        value = ConvertUtils.convert("foo", Long.class);
        assertInstanceOf(Long.class, value);
        assertEquals(((Long) value).longValue(), 0);

        value = ConvertUtils.convert("foo", Short.TYPE);
        assertInstanceOf(Short.class, value);
        assertEquals(((Short) value).shortValue(), (short) 0);

        value = ConvertUtils.convert("foo", Short.class);
        assertInstanceOf(Short.class, value);
        assertEquals(((Short) value).shortValue(), (short) 0);

    }

    /**
     * Negative String to String array tests.
     */
    @Test
    public void testNegativeStringArray() {

        Object value;
        final String[] stringArray = {};

        value = ConvertUtils.convert((String) null, stringArray.getClass());
        checkStringArray(value, stringArray);

    }

    /**
     * Test conversion of object to string for arrays.
     */
    @Test
    public void testObjectToStringArray() {

        final int[] intArray0 = {};
        final int[] intArray1 = { 123 };
        final int[] intArray2 = { 123, 456 };
        final String[] stringArray0 = {};
        final String[] stringArray1 = { "abc" };
        final String[] stringArray2 = { "abc", "def" };

        assertEquals(null, ConvertUtils.convert(intArray0), "intArray0");
        assertEquals("123", ConvertUtils.convert(intArray1), "intArray1");
        assertEquals("123", ConvertUtils.convert(intArray2), "intArray2");

        assertEquals(null, ConvertUtils.convert(stringArray0), "stringArray0");
        assertEquals("abc", ConvertUtils.convert(stringArray1), "stringArray1");
        assertEquals("abc", ConvertUtils.convert(stringArray2), "stringArray2");

    }

    /**
     * Test conversion of object to string for scalars.
     */
    @Test
    public void testObjectToStringScalar() {

        assertEquals("false", ConvertUtils.convert(Boolean.FALSE), "Boolean->String");
        assertEquals("true", ConvertUtils.convert(Boolean.TRUE), "Boolean->String");
        assertEquals("123", ConvertUtils.convert(Byte.valueOf((byte) 123)), "Byte->String");
        assertEquals("a", ConvertUtils.convert(Character.valueOf('a')), "Character->String");
        assertEquals("123.0", ConvertUtils.convert(Double.valueOf(123.0)), "Double->String");
        assertEquals("123.0", ConvertUtils.convert(Float.valueOf((float) 123.0)), "Float->String");
        assertEquals("123", ConvertUtils.convert(Integer.valueOf(123)), "Integer->String");
        assertEquals("123", ConvertUtils.convert(Long.valueOf(123)), "Long->String");
        assertEquals("123", ConvertUtils.convert(Short.valueOf((short) 123)), "Short->String");
        assertEquals("abc", ConvertUtils.convert("abc"), "String->String");
        assertEquals(null, ConvertUtils.convert(null), "String->String null");

    }

    /**
     * Positive array conversion tests.
     */
    @Test
    public void testPositiveArray() {
        // check 1
        final String[] values1 = { "10", "20", "30" };
        final int[] shape = ArrayUtils.EMPTY_INT_ARRAY;
        Object value = ConvertUtils.convert(values1, shape.getClass());
        assertEquals(shape.getClass(), value.getClass());
        final int[] results1 = (int[]) value;
        assertEquals(10, results1[0]);
        assertEquals(20, results1[1]);
        assertEquals(30, results1[2]);
        // check 2
        final String[] values2 = { "100", "200", "300" };
        value = ConvertUtils.convert(values2, shape.getClass());
        assertEquals(shape.getClass(), value.getClass());
        final int[] results2 = (int[]) value;
        assertEquals(100, results2[0]);
        assertEquals(200, results2[1]);
        assertEquals(300, results2[2]);
        // check 3
        value = ConvertUtils.convert(values1, Integer.TYPE);
        assertEquals(Integer.class, value.getClass());
        assertEquals(Integer.valueOf(10), value);
    }

    /**
     * Positive String to primitive integer array tests.
     */
    @Test
    public void testPositiveIntegerArray() {
        Object value;
        final int[] intArray = {};
        final int[] intArray1 = { 0 };
        final int[] intArray2 = { 0, 10 };

        final Class<? extends int[]> intArrayClass = intArray.getClass();
        value = ConvertUtils.convert("{  }", intArrayClass);
        checkIntegerArray(value, intArray);

        value = ConvertUtils.convert("0", intArrayClass);
        checkIntegerArray(value, intArray1);
        value = ConvertUtils.convert(" 0 ", intArrayClass);
        checkIntegerArray(value, intArray1);
        value = ConvertUtils.convert("{ 0 }", intArrayClass);
        checkIntegerArray(value, intArray1);

        value = ConvertUtils.convert("0,10", intArrayClass);
        checkIntegerArray(value, intArray2);
        value = ConvertUtils.convert("0 10", intArrayClass);
        checkIntegerArray(value, intArray2);
        value = ConvertUtils.convert("{0,10}", intArrayClass);
        checkIntegerArray(value, intArray2);
        value = ConvertUtils.convert("{0 10}", intArrayClass);
        checkIntegerArray(value, intArray2);
        value = ConvertUtils.convert("{ 0, 10 }", intArrayClass);
        checkIntegerArray(value, intArray2);
        value = ConvertUtils.convert("{ 0 10 }", intArrayClass);
        checkIntegerArray(value, intArray2);
    }

    /**
     * Positive scalar conversion tests.
     */
    @Test
    public void testPositiveScalar() {

        Object value;

        value = ConvertUtils.convert("true", Boolean.TYPE);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("true", Boolean.class);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("yes", Boolean.TYPE);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("yes", Boolean.class);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("y", Boolean.TYPE);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("y", Boolean.class);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("on", Boolean.TYPE);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("on", Boolean.class);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("false", Boolean.TYPE);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("false", Boolean.class);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("no", Boolean.TYPE);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("no", Boolean.class);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("n", Boolean.TYPE);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("n", Boolean.class);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("off", Boolean.TYPE);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("off", Boolean.class);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("123", Byte.TYPE);
        assertInstanceOf(Byte.class, value);
        assertEquals(((Byte) value).byteValue(), (byte) 123);

        value = ConvertUtils.convert("123", Byte.class);
        assertInstanceOf(Byte.class, value);
        assertEquals(((Byte) value).byteValue(), (byte) 123);

        value = ConvertUtils.convert("a", Character.TYPE);
        assertInstanceOf(Character.class, value);
        assertEquals(((Character) value).charValue(), 'a');

        value = ConvertUtils.convert("a", Character.class);
        assertInstanceOf(Character.class, value);
        assertEquals(((Character) value).charValue(), 'a');

        value = ConvertUtils.convert("java.lang.String", Class.class);
        assertInstanceOf(Class.class, value);
        assertEquals(String.class, value);

        value = ConvertUtils.convert("123.456", Double.TYPE);
        assertInstanceOf(Double.class, value);
        assertEquals(((Double) value).doubleValue(), 123.456, 0.005);

        value = ConvertUtils.convert("123.456", Double.class);
        assertInstanceOf(Double.class, value);
        assertEquals(((Double) value).doubleValue(), 123.456, 0.005);

        value = ConvertUtils.convert("123.456", Float.TYPE);
        assertInstanceOf(Float.class, value);
        assertEquals(((Float) value).floatValue(), (float) 123.456, (float) 0.005);

        value = ConvertUtils.convert("123.456", Float.class);
        assertInstanceOf(Float.class, value);
        assertEquals(((Float) value).floatValue(), (float) 123.456, (float) 0.005);

        value = ConvertUtils.convert("123", Integer.TYPE);
        assertInstanceOf(Integer.class, value);
        assertEquals(((Integer) value).intValue(), 123);

        value = ConvertUtils.convert("123", Integer.class);
        assertInstanceOf(Integer.class, value);
        assertEquals(((Integer) value).intValue(), 123);

        value = ConvertUtils.convert("123", Long.TYPE);
        assertInstanceOf(Long.class, value);
        assertEquals(((Long) value).longValue(), 123);

        value = ConvertUtils.convert("123", Long.class);
        assertInstanceOf(Long.class, value);
        assertEquals(((Long) value).longValue(), 123);

        value = ConvertUtils.convert("123", Short.TYPE);
        assertInstanceOf(Short.class, value);
        assertEquals(((Short) value).shortValue(), (short) 123);

        value = ConvertUtils.convert("123", Short.class);
        assertInstanceOf(Short.class, value);
        assertEquals(((Short) value).shortValue(), (short) 123);

        String input;

        input = "2002-03-17";
        value = ConvertUtils.convert(input, Date.class);
        assertInstanceOf(Date.class, value);
        assertEquals(input, value.toString());

        input = "20:30:40";
        value = ConvertUtils.convert(input, Time.class);
        assertInstanceOf(Time.class, value);
        assertEquals(input, value.toString());

        input = "2002-03-17 20:30:40.0";
        value = ConvertUtils.convert(input, Timestamp.class);
        assertInstanceOf(Timestamp.class, value);
        assertEquals(input, value.toString());

    }

    /**
     * Positive String to String array tests.
     */
    @Test
    public void testPositiveStringArray() {

        Object value;
        final String[] stringArray = {};
        final String[] stringArray1 = { "abc" };
        final String[] stringArray2 = { "abc", "de,f" };

        value = ConvertUtils.convert("", stringArray.getClass());
        checkStringArray(value, stringArray);
        value = ConvertUtils.convert(" ", stringArray.getClass());
        checkStringArray(value, stringArray);
        value = ConvertUtils.convert("{}", stringArray.getClass());
        checkStringArray(value, stringArray);
        value = ConvertUtils.convert("{  }", stringArray.getClass());
        checkStringArray(value, stringArray);

        value = ConvertUtils.convert("abc", stringArray.getClass());
        checkStringArray(value, stringArray1);
        value = ConvertUtils.convert("{abc}", stringArray.getClass());
        checkStringArray(value, stringArray1);
        value = ConvertUtils.convert("\"abc\"", stringArray.getClass());
        checkStringArray(value, stringArray1);
        value = ConvertUtils.convert("{\"abc\"}", stringArray.getClass());
        checkStringArray(value, stringArray1);
        value = ConvertUtils.convert("'abc'", stringArray.getClass());
        checkStringArray(value, stringArray1);
        value = ConvertUtils.convert("{'abc'}", stringArray.getClass());
        checkStringArray(value, stringArray1);

        value = ConvertUtils.convert("abc 'de,f'", stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = ConvertUtils.convert("{abc, 'de,f'}", stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = ConvertUtils.convert("\"abc\",\"de,f\"", stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = ConvertUtils.convert("{\"abc\" 'de,f'}", stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = ConvertUtils.convert("'abc' 'de,f'", stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = ConvertUtils.convert("{'abc', \"de,f\"}", stringArray.getClass());
        checkStringArray(value, stringArray2);

    }

    @Test
    public void testSeparateConvertInstances() throws Exception {
        final ConvertUtilsBean utilsOne = new ConvertUtilsBean();
        final ConvertUtilsBean utilsTwo = new ConvertUtilsBean();

        // make sure that the test work ok before anything's changed
        Object value = utilsOne.convert("true", Boolean.TYPE);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), true, "Standard conversion failed (1)");

        value = utilsTwo.convert("true", Boolean.TYPE);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), true, "Standard conversion failed (2)");

        // now register a test
        utilsOne.register(new ThrowExceptionConverter(), Boolean.TYPE);
        assertThrows(PassTestException.class, () -> utilsOne.convert("true", Boolean.TYPE));

        // nothing should have changed
        value = utilsTwo.convert("true", Boolean.TYPE);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), true, "Standard conversion failed (3)");

        // nothing we'll test deregister
        utilsOne.deregister();
        value = utilsOne.convert("true", Boolean.TYPE);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), true, "Instance deregister failed.");

        value = utilsTwo.convert("true", Boolean.TYPE);
        assertInstanceOf(Boolean.class, value);
        assertEquals(((Boolean) value).booleanValue(), true, "Standard conversion failed (4)");
    }

}
