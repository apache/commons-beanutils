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

package org.apache.commons.beanutils.locale;


import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.ConversionException;


/**
 * <p>
 *  Test Case for the LocaleConvertUtils class.
 *  See unimplemented functionality of the convert utils in the method begining with fixme
 * </p>
 *
 * @version $Id$
 */

public class LocaleConvertUtilsTestCase extends TestCase {

    // ---------------------------------------------------- Instance Variables

    private char m_decimalSeparator;

    // ---------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public LocaleConvertUtilsTestCase(final String name) {
        super(name);
    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() {

        LocaleConvertUtils.deregister();

        final NumberFormat nf = DecimalFormat.getNumberInstance();
        final String result = nf.format(1.1);

        // could be commas instead of stops in Europe.
        m_decimalSeparator = result.charAt(1);


    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(LocaleConvertUtilsTestCase.class));
    }


    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {
        // No action required
    }


    // ------------------------------------------------ Individual Test Methods


    /**
     * Negative String to primitive integer array tests.
     */
    public void fixmetestNegativeIntegerArray() {

        fail("Array conversions not implemented yet.");

        Object value = null;
        final int intArray[] = new int[0];

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
     * Negative scalar conversion tests.  These rely on the standard
     * default value conversions in LocaleConvertUtils.
     */
    public void testNegativeScalar() {

        /*  fixme Boolean converters not implemented at this point
        value = LocaleConvertUtils.convert("foo", Boolean.TYPE);
        ...

        value = LocaleConvertUtils.convert("foo", Boolean.class);
        ...
        */


        try {
            LocaleConvertUtils.convert("foo", Byte.TYPE);
            fail("Should have thrown conversion exception (1)");
        } catch (final ConversionException e) {
            // Expected result
        }

        try {
            LocaleConvertUtils.convert("foo", Byte.class);
            fail("Should have thrown conversion exception (2)");
        } catch (final ConversionException e) {
            // Expected result
        }

        /* fixme - not implemented
         try {
             value = LocaleConvertUtils.convert("org.apache.commons.beanutils.Undefined", Class.class);
             fail("Should have thrown conversion exception");
         } catch (ConversionException e) {
             ; // Expected result
         }
         */

        try {
            LocaleConvertUtils.convert("foo", Double.TYPE);
            fail("Should have thrown conversion exception (3)");
        } catch (final ConversionException e) {
            // Expected result
        }

        try {
            LocaleConvertUtils.convert("foo", Double.class);
            fail("Should have thrown conversion exception (4)");
        } catch (final ConversionException e) {
            // Expected result
        }

        try {
            LocaleConvertUtils.convert("foo", Float.TYPE);
            fail("Should have thrown conversion exception (5)");
        } catch (final ConversionException e) {
            // Expected result
        }

        try {
            LocaleConvertUtils.convert("foo", Float.class);
            fail("Should have thrown conversion exception (6)");
        } catch (final ConversionException e) {
            // Expected result
        }

        try {
            LocaleConvertUtils.convert("foo", Integer.TYPE);
            fail("Should have thrown conversion exception (7)");
        } catch (final ConversionException e) {
            // Expected result
        }

        try {
            LocaleConvertUtils.convert("foo", Integer.class);
            fail("Should have thrown conversion exception (8)");
        } catch (final ConversionException e) {
            // Expected result
        }

        try {
            LocaleConvertUtils.convert("foo", Byte.TYPE);
            fail("Should have thrown conversion exception (9)");
        } catch (final ConversionException e) {
            // Expected result
        }

        try {
            LocaleConvertUtils.convert("foo", Long.class);
            fail("Should have thrown conversion exception (10)");
        } catch (final ConversionException e) {
            // Expected result
        }

        try {
            LocaleConvertUtils.convert("foo", Short.TYPE);
            fail("Should have thrown conversion exception (11)");
        } catch (final ConversionException e) {
            // Expected result
        }

        try {
            LocaleConvertUtils.convert("foo", Short.class);
            fail("Should have thrown conversion exception (12)");
        } catch (final ConversionException e) {
            // Expected result
        }

    }


    /**
     * Negative String to String array tests.
     */
    public void fixmetestNegativeStringArray() {

        fail("Array conversions not implemented yet.");

        Object value = null;
        final String stringArray[] = new String[0];

        value = LocaleConvertUtils.convert((String) null, stringArray.getClass());
        checkStringArray(value, stringArray);
    }


    /**
     * Test conversion of object to string for arrays - .
     */
    public void fixmetestObjectToStringArray() {

        fail("Array conversions not implemented yet.");
        final int intArray0[] = new int[0];
        final int intArray1[] = {123};
        final int intArray2[] = {123, 456};
        final String stringArray0[] = new String[0];
        final String stringArray1[] = {"abc"};
        final String stringArray2[] = {"abc", "def"};

        assertEquals("intArray0", null,
                LocaleConvertUtils.convert(intArray0));
        assertEquals("intArray1", "123",
                LocaleConvertUtils.convert(intArray1));
        assertEquals("intArray2", "123",
                LocaleConvertUtils.convert(intArray2));

        assertEquals("stringArray0", null,
                LocaleConvertUtils.convert(stringArray0));
        assertEquals("stringArray1", "abc",
                LocaleConvertUtils.convert(stringArray1));
        assertEquals("stringArray2", "abc",
                LocaleConvertUtils.convert(stringArray2));

    }


    /**
     * Test conversion of object to string for scalars.
     */
    public void testObjectToStringScalar() {

        assertEquals("Boolean->String", "false",
                LocaleConvertUtils.convert(Boolean.FALSE));
        assertEquals("Boolean->String", "true",
                LocaleConvertUtils.convert(Boolean.TRUE));
        assertEquals("Byte->String", "123",
                LocaleConvertUtils.convert(new Byte((byte) 123)));
        assertEquals("Character->String", "a",
                LocaleConvertUtils.convert(new Character('a')));
        assertEquals("Double->String", "123" + m_decimalSeparator + "4",
                LocaleConvertUtils.convert(new Double(123.4)));
        assertEquals("Float->String", "123" + m_decimalSeparator + "4",
                LocaleConvertUtils.convert(new Float((float) 123.4)));
        assertEquals("Integer->String", "123",
                LocaleConvertUtils.convert(new Integer(123)));
        assertEquals("Long->String", "123",
                LocaleConvertUtils.convert(new Long(123)));
        assertEquals("Short->String", "123",
                LocaleConvertUtils.convert(new Short((short) 123)));
        assertEquals("String->String", "abc",
                LocaleConvertUtils.convert("abc"));
        assertEquals("String->String null", null,
                LocaleConvertUtils.convert(null));

    }


    /**
     * Positive array conversion tests.
     */
    public void fixmetestPositiveArray() {

        fail("Array conversions not implemented yet.");

        final String values1[] = {"10", "20", "30"};
        Object value = LocaleConvertUtils.convert(values1, Integer.TYPE);
        final int shape[] = new int[0];
        assertEquals(shape.getClass(), value.getClass());
        final int results1[] = (int[]) value;
        assertEquals(results1[0], 10);
        assertEquals(results1[1], 20);
        assertEquals(results1[2], 30);

        final String values2[] = {"100", "200", "300"};
        value = LocaleConvertUtils.convert(values2, shape.getClass());
        assertEquals(shape.getClass(), value.getClass());
        final int results2[] = (int[]) value;
        assertEquals(results2[0], 100);
        assertEquals(results2[1], 200);
        assertEquals(results2[2], 300);
    }


    /**
     * Positive String to primitive integer array tests.
     */
    public void fixmetestPositiveIntegerArray() {

        fail("Array conversions not implemented yet.");

        Object value = null;
        final int intArray[] = new int[0];
        final int intArray1[] = new int[]{0};
        final int intArray2[] = new int[]{0, 10};

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
     * Positive scalar conversion tests.
     */
    public void testPositiveScalar() {
        Object value = null;

        /* fixme Boolean converters not implemented
         value = LocaleConvertUtils.convert("true", Boolean.TYPE);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), true);

         value = LocaleConvertUtils.convert("true", Boolean.class);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), true);

         value = LocaleConvertUtils.convert("yes", Boolean.TYPE);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), true);

         value = LocaleConvertUtils.convert("yes", Boolean.class);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), true);

         value = LocaleConvertUtils.convert("y", Boolean.TYPE);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), true);

         value = LocaleConvertUtils.convert("y", Boolean.class);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), true);

         value = LocaleConvertUtils.convert("on", Boolean.TYPE);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), true);

         value = LocaleConvertUtils.convert("on", Boolean.class);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), true);

         value = LocaleConvertUtils.convert("false", Boolean.TYPE);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), false);

         value = LocaleConvertUtils.convert("false", Boolean.class);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), false);

         value = LocaleConvertUtils.convert("no", Boolean.TYPE);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), false);

         value = LocaleConvertUtils.convert("no", Boolean.class);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), false);

         value = LocaleConvertUtils.convert("n", Boolean.TYPE);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), false);

         value = LocaleConvertUtils.convert("n", Boolean.class);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), false);

         value = LocaleConvertUtils.convert("off", Boolean.TYPE);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), false);

         value = LocaleConvertUtils.convert("off", Boolean.class);
         assertTrue(value instanceof Boolean);
         assertEquals(((Boolean) value).booleanValue(), false);
         */

        value = LocaleConvertUtils.convert("123", Byte.TYPE);
        assertTrue(value instanceof Byte);
        assertEquals(((Byte) value).byteValue(), (byte) 123);

        value = LocaleConvertUtils.convert("123", Byte.class);
        assertTrue(value instanceof Byte);
        assertEquals(((Byte) value).byteValue(), (byte) 123);

        /*fixme Character conversion not implemented yet
        value = LocaleConvertUtils.convert("a", Character.TYPE);
        assertTrue(value instanceof Character);
        assertEquals(((Character) value).charValue(), 'a');

        value = LocaleConvertUtils.convert("a", Character.class);
        assertTrue(value instanceof Character);
        assertEquals(((Character) value).charValue(), 'a');
        */
        /* fixme - this is a discrepancy with standard converters ( probably not major issue )
        value = LocaleConvertUtils.convert("java.lang.String", Class.class);
        assertTrue(value instanceof Class);
        assertEquals(String.class, (Class) value);
        */

        value = LocaleConvertUtils.convert("123" + m_decimalSeparator + "456", Double.TYPE);
        assertTrue(value instanceof Double);
        assertEquals(((Double) value).doubleValue(), 123.456, 0.005);

        value = LocaleConvertUtils.convert("123" + m_decimalSeparator + "456", Double.class);
        assertTrue(value instanceof Double);
        assertEquals(((Double) value).doubleValue(), 123.456, 0.005);

        value = LocaleConvertUtils.convert("123" + m_decimalSeparator + "456", Float.TYPE);
        assertTrue(value instanceof Float);
        assertEquals(((Float) value).floatValue(), (float) 123.456,
                (float) 0.005);

        value = LocaleConvertUtils.convert("123" + m_decimalSeparator + "456", Float.class);
        assertTrue(value instanceof Float);
        assertEquals(((Float) value).floatValue(), (float) 123.456,
                (float) 0.005);

        value = LocaleConvertUtils.convert("123", Integer.TYPE);
        assertTrue(value instanceof Integer);
        assertEquals(((Integer) value).intValue(), 123);

        value = LocaleConvertUtils.convert("123", Integer.class);
        assertTrue(value instanceof Integer);
        assertEquals(((Integer) value).intValue(), 123);

        value = LocaleConvertUtils.convert("123", Long.TYPE);
        assertTrue(value instanceof Long);
        assertEquals(((Long) value).longValue(), 123);

        value = LocaleConvertUtils.convert("123456", Long.class);
        assertTrue(value instanceof Long);
        assertEquals(((Long) value).longValue(), 123456);

        /* fixme - Short conversion not implemented at this point
        value = LocaleConvertUtils.convert("123", Short.TYPE);
        assertTrue(value instanceof Short);
        assertEquals(((Short) value).shortValue(), (short) 123);

        value = LocaleConvertUtils.convert("123", Short.class);
        assertTrue(value instanceof Short);
        assertEquals(((Short) value).shortValue(), (short) 123);
        */

        String input = null;

        input = "2002-03-17";
        value = LocaleConvertUtils.convert(input, Date.class);
        assertTrue(value instanceof Date);
        assertEquals(input, value.toString());

        input = "20:30:40";
        value = LocaleConvertUtils.convert(input, Time.class);
        assertTrue(value instanceof Time);
        assertEquals(input, value.toString());

        input = "2002-03-17 20:30:40.0";
        value = LocaleConvertUtils.convert(input, Timestamp.class);
        assertTrue(value instanceof Timestamp);
        assertEquals(input, value.toString());

    }


    /**
     * Positive String to String array tests.
     */
    public void fixmetestPositiveStringArray() {

        fail("Array conversions not implemented yet.");

        Object value = null;
        final String stringArray[] = new String[0];
        final String stringArray1[] = new String[]
        {"abc"};
        final String stringArray2[] = new String[]
        {"abc", "de,f"};

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

        value = LocaleConvertUtils.convert("abc 'de,f'",
                stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = LocaleConvertUtils.convert("{abc, 'de,f'}",
                stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = LocaleConvertUtils.convert("\"abc\",\"de,f\"",
                stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = LocaleConvertUtils.convert("{\"abc\" 'de,f'}",
                stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = LocaleConvertUtils.convert("'abc' 'de,f'",
                stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = LocaleConvertUtils.convert("{'abc', \"de,f\"}",
                stringArray.getClass());
        checkStringArray(value, stringArray2);

    }

    /**
     * Test conversion of a String using a Locale and pattern.
     */
    public void testConvertStringLocaleNull() {
        Object result = null;
        try {
            result = LocaleConvertUtils.convert("123", Integer.class, (Locale)null, "#,###");
        } catch (final Exception e) {
            e.printStackTrace();
            fail("Threw: " + e);
        }
        assertNotNull("Null Result", result);
        assertEquals("Integer Type", Integer.class, result.getClass());
        assertEquals("Integer Value", new Integer(123), result);
    }

    /**
     * Test conversion of a String array using a Locale and pattern.
     */
    public void testConvertStringArrayLocaleNull() {
        Object result = null;
        try {
            result = LocaleConvertUtils.convert(new String[] {"123"}, Integer[].class, (Locale)null, "#,###");
        } catch (final Exception e) {
            e.printStackTrace();
            fail("Threw: " + e);
        }
        assertNotNull("Null Result", result);
        assertEquals("Integer Array Type", Integer[].class, result.getClass());
        assertEquals("Integer Array Length", 1, ((Integer[])result).length);
        assertEquals("Integer Array Value", new Integer(123), ((Integer[])result)[0]);
    }

    /**
     * Tests a conversion if there is no suitable converter registered. In this
     * case, the string converter is used, and the passed in target type is
     * ignored. (This test is added to prevent a regression after the locale
     * converters have been generified.)
     */
    public void testDefaultToStringConversionUnsupportedType() {
        final Integer value = 20131101;
        assertEquals("Wrong result", value.toString(),
                LocaleConvertUtils.convert(value.toString(), getClass()));
    }

    // -------------------------------------------------------- Private Methods


    private void checkIntegerArray(final Object value, final int intArray[]) {

        assertNotNull("Returned value is not null", value);
        assertEquals("Returned value is int[]",
                intArray.getClass(), value.getClass());
        final int results[] = (int[]) value;
        assertEquals("Returned array length", intArray.length, results.length);
        for (int i = 0; i < intArray.length; i++) {
            assertEquals("Returned array value " + i,
                    intArray[i], results[i]);
        }

    }


    private void checkStringArray(final Object value, final String stringArray[]) {

        assertNotNull("Returned value is not null", value);
        assertEquals("Returned value is String[]",
                stringArray.getClass(), value.getClass());
        final String results[] = (String[]) value;
        assertEquals("Returned array length",
                stringArray.length, results.length);
        for (int i = 0; i < stringArray.length; i++) {
            assertEquals("Returned array value " + i,
                    stringArray[i], results[i]);
        }

    }


}

