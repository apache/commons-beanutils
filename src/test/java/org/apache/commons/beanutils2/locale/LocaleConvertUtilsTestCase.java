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

package org.apache.commons.beanutils2.locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;

import org.apache.commons.beanutils2.ConversionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test Case for the LocaleConvertUtils class. See unimplemented functionality of the convert utils in the method begining with fixme
 * </p>
 */
public class LocaleConvertUtilsTestCase {

    private char decimalSeparator;

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
     * Negative String to primitive integer array tests.
     */
    @Test
    @Disabled("Array conversions not implemented yet.")
    public void fixmetestNegativeIntegerArray() {

        Object value;
        final int[] intArray = {};

        value = LocaleConvertUtils.convert((String) null, intArray.getClass());
        checkIntegerArray(value, intArray);
        value = LocaleConvertUtils.convert("a", intArray.getClass());
        checkIntegerArray(value, intArray);
        value = LocaleConvertUtils.convert("{ a }", intArray.getClass());
        checkIntegerArray(value, intArray);
        value = LocaleConvertUtils.convert("1a3", intArray.getClass());
        checkIntegerArray(value, intArray);
        value = LocaleConvertUtils.convert("{ 1a3 }", intArray.getClass());
        checkIntegerArray(value, intArray);
        value = LocaleConvertUtils.convert("0,1a3", intArray.getClass());
        checkIntegerArray(value, intArray);
        value = LocaleConvertUtils.convert("{ 0, 1a3 }", intArray.getClass());
        checkIntegerArray(value, intArray);

    }

    /**
     * Negative String to String array tests.
     */
    @Test
    @Disabled("Array conversions not implemented yet.")
    public void fixmetestNegativeStringArray() {

        Object value;
        final String[] stringArray = {};

        value = LocaleConvertUtils.convert((String) null, stringArray.getClass());
        checkStringArray(value, stringArray);
    }

    /**
     * Test conversion of object to string for arrays - .
     */
    @Test
    @Disabled("Array conversions not implemented yet.")
    public void fixmetestObjectToStringArray() {

        final int[] intArray0 = {};
        final int[] intArray1 = { 123 };
        final int[] intArray2 = { 123, 456 };
        final String[] stringArray0 = {};
        final String[] stringArray1 = { "abc" };
        final String[] stringArray2 = { "abc", "def" };

        assertEquals(null, LocaleConvertUtils.convert(intArray0), "intArray0");
        assertEquals("123", LocaleConvertUtils.convert(intArray1), "intArray1");
        assertEquals("123", LocaleConvertUtils.convert(intArray2), "intArray2");

        assertEquals(null, LocaleConvertUtils.convert(stringArray0), "stringArray0");
        assertEquals("abc", LocaleConvertUtils.convert(stringArray1), "stringArray1");
        assertEquals("abc", LocaleConvertUtils.convert(stringArray2), "stringArray2");

    }

    /**
     * Positive array conversion tests.
     */
    @Test
    @Disabled("Array conversions not implemented yet.")
    public void fixmetestPositiveArray() {

        final String[] values1 = { "10", "20", "30" };
        Object value = LocaleConvertUtils.convert(values1, Integer.TYPE);
        final int[] shape = {};
        assertEquals(shape.getClass(), value.getClass());
        final int[] results1 = (int[]) value;
        assertEquals(results1[0], 10);
        assertEquals(results1[1], 20);
        assertEquals(results1[2], 30);

        final String[] values2 = { "100", "200", "300" };
        value = LocaleConvertUtils.convert(values2, shape.getClass());
        assertEquals(shape.getClass(), value.getClass());
        final int[] results2 = (int[]) value;
        assertEquals(results2[0], 100);
        assertEquals(results2[1], 200);
        assertEquals(results2[2], 300);
    }

    /**
     * Positive String to primitive integer array tests.
     */
    @Test
    @Disabled("Array conversions not implemented yet.")
    public void fixmetestPositiveIntegerArray() {

        Object value;
        final int[] intArray = {};
        final int[] intArray1 = { 0 };
        final int[] intArray2 = { 0, 10 };

        value = LocaleConvertUtils.convert("{  }", intArray.getClass());
        checkIntegerArray(value, intArray);

        value = LocaleConvertUtils.convert("0", intArray.getClass());
        checkIntegerArray(value, intArray1);
        value = LocaleConvertUtils.convert(" 0 ", intArray.getClass());
        checkIntegerArray(value, intArray1);
        value = LocaleConvertUtils.convert("{ 0 }", intArray.getClass());
        checkIntegerArray(value, intArray1);

        value = LocaleConvertUtils.convert("0,10", intArray.getClass());
        checkIntegerArray(value, intArray2);
        value = LocaleConvertUtils.convert("0 10", intArray.getClass());
        checkIntegerArray(value, intArray2);
        value = LocaleConvertUtils.convert("{0,10}", intArray.getClass());
        checkIntegerArray(value, intArray2);
        value = LocaleConvertUtils.convert("{0 10}", intArray.getClass());
        checkIntegerArray(value, intArray2);
        value = LocaleConvertUtils.convert("{ 0, 10 }", intArray.getClass());
        checkIntegerArray(value, intArray2);
        value = LocaleConvertUtils.convert("{ 0 10 }", intArray.getClass());
        checkIntegerArray(value, intArray2);
    }

    /**
     * Positive String to String array tests.
     */
    @Test
    @Disabled("Array conversions not implemented yet.")
    public void fixmetestPositiveStringArray() {

        Object value;
        final String[] stringArray = {};
        final String[] stringArray1 = { "abc" };
        final String[] stringArray2 = { "abc", "de,f" };

        value = LocaleConvertUtils.convert("", stringArray.getClass());
        checkStringArray(value, stringArray);
        value = LocaleConvertUtils.convert(" ", stringArray.getClass());
        checkStringArray(value, stringArray);
        value = LocaleConvertUtils.convert("{}", stringArray.getClass());
        checkStringArray(value, stringArray);
        value = LocaleConvertUtils.convert("{  }", stringArray.getClass());
        checkStringArray(value, stringArray);

        value = LocaleConvertUtils.convert("abc", stringArray.getClass());
        checkStringArray(value, stringArray1);
        value = LocaleConvertUtils.convert("{abc}", stringArray.getClass());
        checkStringArray(value, stringArray1);
        value = LocaleConvertUtils.convert("\"abc\"", stringArray.getClass());
        checkStringArray(value, stringArray1);
        value = LocaleConvertUtils.convert("{\"abc\"}", stringArray.getClass());
        checkStringArray(value, stringArray1);
        value = LocaleConvertUtils.convert("'abc'", stringArray.getClass());
        checkStringArray(value, stringArray1);
        value = LocaleConvertUtils.convert("{'abc'}", stringArray.getClass());
        checkStringArray(value, stringArray1);

        value = LocaleConvertUtils.convert("abc 'de,f'", stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = LocaleConvertUtils.convert("{abc, 'de,f'}", stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = LocaleConvertUtils.convert("\"abc\",\"de,f\"", stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = LocaleConvertUtils.convert("{\"abc\" 'de,f'}", stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = LocaleConvertUtils.convert("'abc' 'de,f'", stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = LocaleConvertUtils.convert("{'abc', \"de,f\"}", stringArray.getClass());
        checkStringArray(value, stringArray2);

    }

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() {

        LocaleConvertUtils.deregister();

        final NumberFormat nf = NumberFormat.getNumberInstance();
        final String result = nf.format(1.1);

        // could be commas instead of stops in Europe.
        decimalSeparator = result.charAt(1);

    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        // No action required
    }

    /**
     * Test conversion of a String array using a Locale and pattern.
     */
    @Test
    public void testConvertStringArrayLocaleNull() {
        final Object result = LocaleConvertUtils.convert(new String[] { "123" }, Integer[].class, null, "#,###");
        assertNotNull(result, "Null Result");
        assertEquals(Integer[].class, result.getClass(), "Integer Array Type");
        assertEquals(1, ((Integer[]) result).length, "Integer Array Length");
        assertEquals(Integer.valueOf(123), ((Integer[]) result)[0], "Integer Array Value");
    }

    /**
     * Test conversion of a String using a Locale and pattern.
     */
    @Test
    public void testConvertStringLocaleNull() {
        final Object result = LocaleConvertUtils.convert("123", Integer.class, null, "#,###");
        assertNotNull(result, "Null Result");
        assertEquals(Integer.class, result.getClass(), "Integer Type");
        assertEquals(Integer.valueOf(123), result, "Integer Value");
    }

    /**
     * Tests a conversion if there is no suitable converter registered. In this case, the string converter is used, and the passed in target type is ignored.
     * (This test is added to prevent a regression after the locale converters have been generified.)
     */
    @Test
    public void testDefaultToStringConversionUnsupportedType() {
        final Integer value = 20131101;
        assertEquals(value.toString(), LocaleConvertUtils.convert(value.toString(), getClass()), "Wrong result");
    }

    /**
     * Negative scalar conversion tests. These rely on the standard default value conversions in LocaleConvertUtils.
     */
    @Test
    public void testNegativeScalar() {
        /*
         * fixme Boolean converters not implemented at this point value = LocaleConvertUtils.convert("foo", Boolean.TYPE); ...
         *
         * value = LocaleConvertUtils.convert("foo", Boolean.class); ...
         */
        assertThrows(ConversionException.class, () -> LocaleConvertUtils.convert("foo", Byte.TYPE));
        assertThrows(ConversionException.class, () -> LocaleConvertUtils.convert("foo", Byte.class));
        /*
         * fixme - not implemented try { value = LocaleConvertUtils.convert("org.apache.commons.beanutils2.Undefined", Class.class);
         * fail("Should have thrown conversion exception"); } catch (ConversionException e) { ; // Expected result }
         */
        assertThrows(ConversionException.class, () -> LocaleConvertUtils.convert("foo", Double.TYPE));
        assertThrows(ConversionException.class, () -> LocaleConvertUtils.convert("foo", Double.class));
        assertThrows(ConversionException.class, () -> LocaleConvertUtils.convert("foo", Float.TYPE));
        assertThrows(ConversionException.class, () -> LocaleConvertUtils.convert("foo", Float.class));
        assertThrows(ConversionException.class, () -> LocaleConvertUtils.convert("foo", Integer.TYPE));
        assertThrows(ConversionException.class, () -> LocaleConvertUtils.convert("foo", Integer.class));
        assertThrows(ConversionException.class, () -> LocaleConvertUtils.convert("foo", Byte.TYPE));
        assertThrows(ConversionException.class, () -> LocaleConvertUtils.convert("foo", Long.class));
        assertThrows(ConversionException.class, () -> LocaleConvertUtils.convert("foo", Short.TYPE));
        assertThrows(ConversionException.class, () -> LocaleConvertUtils.convert("foo", Short.class));
    }

    /**
     * Test conversion of object to string for scalars.
     */
    @Test
    public void testObjectToStringScalar() {

        assertEquals("false", LocaleConvertUtils.convert(Boolean.FALSE), "Boolean->String");
        assertEquals("true", LocaleConvertUtils.convert(Boolean.TRUE), "Boolean->String");
        assertEquals("123", LocaleConvertUtils.convert(Byte.valueOf((byte) 123)), "Byte->String");
        assertEquals("a", LocaleConvertUtils.convert(Character.valueOf('a')), "Character->String");
        assertEquals("123" + decimalSeparator + "4", LocaleConvertUtils.convert(Double.valueOf(123.4)), "Double->String");
        assertEquals("123" + decimalSeparator + "4", LocaleConvertUtils.convert(Float.valueOf((float) 123.4)), "Float->String");
        assertEquals("123", LocaleConvertUtils.convert(Integer.valueOf(123)), "Integer->String");
        assertEquals("123", LocaleConvertUtils.convert(Long.valueOf(123)), "Long->String");
        assertEquals("123", LocaleConvertUtils.convert(Short.valueOf((short) 123)), "Short->String");
        assertEquals("abc", LocaleConvertUtils.convert("abc"), "String->String");
        assertEquals(null, LocaleConvertUtils.convert(null), "String->String null");

    }

    /**
     * Positive scalar conversion tests.
     */
    @Test
    public void testPositiveScalar() {
        Object value;

        /*
         * fixme Boolean converters not implemented value = LocaleConvertUtils.convert("true", Boolean.TYPE); assertInstanceOf(Boolean.class, value);
         * assertEquals(((Boolean) value).booleanValue(), true);
         *
         * value = LocaleConvertUtils.convert("true", Boolean.class); assertInstanceOf(Boolean.class, value); assertEquals(((Boolean) value).booleanValue(),
         * true);
         *
         * value = LocaleConvertUtils.convert("yes", Boolean.TYPE); assertInstanceOf(Boolean.class, value); assertEquals(((Boolean) value).booleanValue(),
         * true);
         *
         * value = LocaleConvertUtils.convert("yes", Boolean.class); assertInstanceOf(Boolean.class, value); assertEquals(((Boolean) value).booleanValue(),
         * true);
         *
         * value = LocaleConvertUtils.convert("y", Boolean.TYPE); assertInstanceOf(Boolean.class, value); assertEquals(((Boolean) value).booleanValue(), true);
         *
         * value = LocaleConvertUtils.convert("y", Boolean.class); assertInstanceOf(Boolean.class, value); assertEquals(((Boolean) value).booleanValue(), true);
         *
         * value = LocaleConvertUtils.convert("on", Boolean.TYPE); assertInstanceOf(Boolean.class, value); assertEquals(((Boolean) value).booleanValue(), true);
         *
         * value = LocaleConvertUtils.convert("on", Boolean.class); assertInstanceOf(Boolean.class, value); assertEquals(((Boolean) value).booleanValue(),
         * true);
         *
         * value = LocaleConvertUtils.convert("false", Boolean.TYPE); assertInstanceOf(Boolean.class, value); assertEquals(((Boolean) value).booleanValue(),
         * false);
         *
         * value = LocaleConvertUtils.convert("false", Boolean.class); assertInstanceOf(Boolean.class, value); assertEquals(((Boolean) value).booleanValue(),
         * false);
         *
         * value = LocaleConvertUtils.convert("no", Boolean.TYPE); assertInstanceOf(Boolean.class, value); assertEquals(((Boolean) value).booleanValue(),
         * false);
         *
         * value = LocaleConvertUtils.convert("no", Boolean.class); assertInstanceOf(Boolean.class, value); assertEquals(((Boolean) value).booleanValue(),
         * false);
         *
         * value = LocaleConvertUtils.convert("n", Boolean.TYPE); assertInstanceOf(Boolean.class, value); assertEquals(((Boolean) value).booleanValue(), false);
         *
         * value = LocaleConvertUtils.convert("n", Boolean.class); assertInstanceOf(Boolean.class, value); assertEquals(((Boolean) value).booleanValue(),
         * false);
         *
         * value = LocaleConvertUtils.convert("off", Boolean.TYPE); assertInstanceOf(Boolean.class, value); assertEquals(((Boolean) value).booleanValue(),
         * false);
         *
         * value = LocaleConvertUtils.convert("off", Boolean.class); assertInstanceOf(Boolean.class, value); assertEquals(((Boolean) value).booleanValue(),
         * false);
         */

        value = LocaleConvertUtils.convert("123", Byte.TYPE);
        assertInstanceOf(Byte.class, value);
        assertEquals(((Byte) value).byteValue(), (byte) 123);

        value = LocaleConvertUtils.convert("123", Byte.class);
        assertInstanceOf(Byte.class, value);
        assertEquals(((Byte) value).byteValue(), (byte) 123);

        /*
         * fixme Character conversion not implemented yet value = LocaleConvertUtils.convert("a", Character.TYPE); assertInstanceOf(Character.class, value);
         * assertEquals(((Character) value).charValue(), 'a');
         *
         * value = LocaleConvertUtils.convert("a", Character.class); assertInstanceOf(Character.class, value); assertEquals(((Character) value).charValue(),
         * 'a');
         */
        /*
         * fixme - this is a discrepancy with standard converters ( probably not major issue ) value = LocaleConvertUtils.convert("java.lang.String",
         * Class.class); assertInstanceOf(Class.class, value); assertEquals(String.class, (Class) value);
         */

        value = LocaleConvertUtils.convert("123" + decimalSeparator + "456", Double.TYPE);
        assertInstanceOf(Double.class, value);
        assertEquals(((Double) value).doubleValue(), 123.456, 0.005);

        value = LocaleConvertUtils.convert("123" + decimalSeparator + "456", Double.class);
        assertInstanceOf(Double.class, value);
        assertEquals(((Double) value).doubleValue(), 123.456, 0.005);

        value = LocaleConvertUtils.convert("123" + decimalSeparator + "456", Float.TYPE);
        assertInstanceOf(Float.class, value);
        assertEquals(((Float) value).floatValue(), (float) 123.456, (float) 0.005);

        value = LocaleConvertUtils.convert("123" + decimalSeparator + "456", Float.class);
        assertInstanceOf(Float.class, value);
        assertEquals(((Float) value).floatValue(), (float) 123.456, (float) 0.005);

        value = LocaleConvertUtils.convert("123", Integer.TYPE);
        assertInstanceOf(Integer.class, value);
        assertEquals(((Integer) value).intValue(), 123);

        value = LocaleConvertUtils.convert("123", Integer.class);
        assertInstanceOf(Integer.class, value);
        assertEquals(((Integer) value).intValue(), 123);

        value = LocaleConvertUtils.convert("123", Long.TYPE);
        assertInstanceOf(Long.class, value);
        assertEquals(((Long) value).longValue(), 123);

        value = LocaleConvertUtils.convert("123456", Long.class);
        assertInstanceOf(Long.class, value);
        assertEquals(((Long) value).longValue(), 123456);

        /*
         * fixme - Short conversion not implemented at this point value = LocaleConvertUtils.convert("123", Short.TYPE); assertInstanceOf(Short.class, value);
         * assertEquals(((Short) value).shortValue(), (short) 123);
         *
         * value = LocaleConvertUtils.convert("123", Short.class); assertInstanceOf(Short.class, value); assertEquals(((Short) value).shortValue(), (short)
         * 123);
         */

        String input;

        input = "2002-03-17";
        value = LocaleConvertUtils.convert(input, Date.class);
        assertInstanceOf(Date.class, value);
        assertEquals(input, value.toString());

        input = "20:30:40";
        value = LocaleConvertUtils.convert(input, Time.class);
        assertInstanceOf(Time.class, value);
        assertEquals(input, value.toString());

        input = "2002-03-17 20:30:40.0";
        value = LocaleConvertUtils.convert(input, Timestamp.class);
        assertInstanceOf(Timestamp.class, value);
        assertEquals(input, value.toString());

    }

}
