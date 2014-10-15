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


import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.converters.DateConverter;


/**
 * <p>
 *  Test Case for the ConvertUtils class.
 * </p>
 *
 * @version $Id$
 */

public class ConvertUtilsTestCase extends TestCase {

    // ---------------------------------------------------- Instance Variables


    // ---------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public ConvertUtilsTestCase(final String name) {
        super(name);
    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() {

        ConvertUtils.deregister();

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(ConvertUtilsTestCase.class));
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
    public void testNegativeIntegerArray() {

        Object value = null;
        final int intArray[] = new int[0];

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
     * Negative scalar conversion tests.  These rely on the standard
     * default value conversions in ConvertUtils.
     */
    public void testNegativeScalar() {

        Object value = null;

        value = ConvertUtils.convert("foo", Boolean.TYPE);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("foo", Boolean.class);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("foo", Byte.TYPE);
        assertTrue(value instanceof Byte);
        assertEquals(((Byte) value).byteValue(), (byte) 0);

        value = ConvertUtils.convert("foo", Byte.class);
        assertTrue(value instanceof Byte);
        assertEquals(((Byte) value).byteValue(), (byte) 0);

        try {
            value = ConvertUtils.convert
                ("org.apache.commons.beanutils.Undefined", Class.class);
            fail("Should have thrown conversion exception");
        } catch (final ConversionException e) {
            // Expected result
        }

        value = ConvertUtils.convert("foo", Double.TYPE);
        assertTrue(value instanceof Double);
        assertEquals(((Double) value).doubleValue(), 0.0,
                     0.005);

        value = ConvertUtils.convert("foo", Double.class);
        assertTrue(value instanceof Double);
        assertEquals(((Double) value).doubleValue(), 0.0, 0.005);

        value = ConvertUtils.convert("foo", Float.TYPE);
        assertTrue(value instanceof Float);
        assertEquals(((Float) value).floatValue(), (float) 0.0,
                     (float) 0.005);

        value = ConvertUtils.convert("foo", Float.class);
        assertTrue(value instanceof Float);
        assertEquals(((Float) value).floatValue(), (float) 0.0,
                     (float) 0.005);

        value = ConvertUtils.convert("foo", Integer.TYPE);
        assertTrue(value instanceof Integer);
        assertEquals(((Integer) value).intValue(), 0);

        value = ConvertUtils.convert("foo", Integer.class);
        assertTrue(value instanceof Integer);
        assertEquals(((Integer) value).intValue(), 0);

        value = ConvertUtils.convert("foo", Byte.TYPE);
        assertTrue(value instanceof Byte);
        assertEquals(((Byte) value).byteValue(), (byte) 0);

        value = ConvertUtils.convert("foo", Long.class);
        assertTrue(value instanceof Long);
        assertEquals(((Long) value).longValue(), 0);

        value = ConvertUtils.convert("foo", Short.TYPE);
        assertTrue(value instanceof Short);
        assertEquals(((Short) value).shortValue(), (short) 0);

        value = ConvertUtils.convert("foo", Short.class);
        assertTrue(value instanceof Short);
        assertEquals(((Short) value).shortValue(), (short) 0);

    }


    /**
     * Negative String to String array tests.
     */
    public void testNegativeStringArray() {

        Object value = null;
        final String stringArray[] = new String[0];

        value = ConvertUtils.convert((String) null, stringArray.getClass());
        checkStringArray(value, stringArray);

    }


    /**
     * Test conversion of object to string for arrays.
     */
    public void testObjectToStringArray() {

        final int intArray0[] = new int[0];
        final int intArray1[] = { 123 };
        final int intArray2[] = { 123, 456 };
        final String stringArray0[] = new String[0];
        final String stringArray1[] = { "abc" };
        final String stringArray2[] = { "abc", "def" };

        assertEquals("intArray0", null,
                     ConvertUtils.convert(intArray0));
        assertEquals("intArray1", "123",
                     ConvertUtils.convert(intArray1));
        assertEquals("intArray2", "123",
                     ConvertUtils.convert(intArray2));

        assertEquals("stringArray0", null,
                     ConvertUtils.convert(stringArray0));
        assertEquals("stringArray1", "abc",
                     ConvertUtils.convert(stringArray1));
        assertEquals("stringArray2", "abc",
                     ConvertUtils.convert(stringArray2));

    }


    /**
     * Test conversion of object to string for scalars.
     */
    public void testObjectToStringScalar() {

        assertEquals("Boolean->String", "false",
                     ConvertUtils.convert(Boolean.FALSE));
        assertEquals("Boolean->String", "true",
                     ConvertUtils.convert(Boolean.TRUE));
        assertEquals("Byte->String", "123",
                     ConvertUtils.convert(new Byte((byte) 123)));
        assertEquals("Character->String", "a",
                     ConvertUtils.convert(new Character('a')));
        assertEquals("Double->String", "123.0",
                     ConvertUtils.convert(new Double(123.0)));
        assertEquals("Float->String", "123.0",
                     ConvertUtils.convert(new Float((float) 123.0)));
        assertEquals("Integer->String", "123",
                     ConvertUtils.convert(new Integer(123)));
        assertEquals("Long->String", "123",
                     ConvertUtils.convert(new Long(123)));
        assertEquals("Short->String", "123",
                     ConvertUtils.convert(new Short((short) 123)));
        assertEquals("String->String", "abc",
                     ConvertUtils.convert("abc"));
        assertEquals("String->String null", null,
                     ConvertUtils.convert(null));

    }


    /**
     * Positive array conversion tests.
     */
    public void testPositiveArray() {

        final String values1[] = { "10", "20", "30" };
        Object value = ConvertUtils.convert(values1, Integer.TYPE);
        final int shape[] = new int[0];
        assertEquals(shape.getClass(), value.getClass());
        final int results1[] = (int[]) value;
        assertEquals(results1[0], 10);
        assertEquals(results1[1], 20);
        assertEquals(results1[2], 30);

        final String values2[] = { "100", "200", "300" };
        value = ConvertUtils.convert(values2, shape.getClass());
        assertEquals(shape.getClass(), value.getClass());
        final int results2[] = (int[]) value;
        assertEquals(results2[0], 100);
        assertEquals(results2[1], 200);
        assertEquals(results2[2], 300);

    }


    /**
     * Positive String to primitive integer array tests.
     */
    public void testPositiveIntegerArray() {

        Object value = null;
        final int intArray[] = new int[0];
        final int intArray1[] = new int[] { 0 };
        final int intArray2[] = new int[] { 0, 10 };

        value = ConvertUtils.convert("{  }", intArray.getClass());
        checkIntegerArray(value, intArray);

        value = ConvertUtils.convert("0", intArray.getClass());
        checkIntegerArray(value, intArray1);
        value = ConvertUtils.convert(" 0 ", intArray.getClass());
        checkIntegerArray(value, intArray1);
        value = ConvertUtils.convert("{ 0 }", intArray.getClass());
        checkIntegerArray(value, intArray1);

        value = ConvertUtils.convert("0,10", intArray.getClass());
        checkIntegerArray(value, intArray2);
        value = ConvertUtils.convert("0 10", intArray.getClass());
        checkIntegerArray(value, intArray2);
        value = ConvertUtils.convert("{0,10}", intArray.getClass());
        checkIntegerArray(value, intArray2);
        value = ConvertUtils.convert("{0 10}", intArray.getClass());
        checkIntegerArray(value, intArray2);
        value = ConvertUtils.convert("{ 0, 10 }", intArray.getClass());
        checkIntegerArray(value, intArray2);
        value = ConvertUtils.convert("{ 0 10 }", intArray.getClass());
        checkIntegerArray(value, intArray2);

    }


    /**
     * Positive scalar conversion tests.
     */
    public void testPositiveScalar() {

        Object value = null;

        value = ConvertUtils.convert("true", Boolean.TYPE);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("true", Boolean.class);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("yes", Boolean.TYPE);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("yes", Boolean.class);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("y", Boolean.TYPE);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("y", Boolean.class);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("on", Boolean.TYPE);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("on", Boolean.class);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), true);

        value = ConvertUtils.convert("false", Boolean.TYPE);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("false", Boolean.class);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("no", Boolean.TYPE);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("no", Boolean.class);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("n", Boolean.TYPE);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("n", Boolean.class);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("off", Boolean.TYPE);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("off", Boolean.class);
        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean) value).booleanValue(), false);

        value = ConvertUtils.convert("123", Byte.TYPE);
        assertTrue(value instanceof Byte);
        assertEquals(((Byte) value).byteValue(), (byte) 123);

        value = ConvertUtils.convert("123", Byte.class);
        assertTrue(value instanceof Byte);
        assertEquals(((Byte) value).byteValue(), (byte) 123);

        value = ConvertUtils.convert("a", Character.TYPE);
        assertTrue(value instanceof Character);
        assertEquals(((Character) value).charValue(), 'a');

        value = ConvertUtils.convert("a", Character.class);
        assertTrue(value instanceof Character);
        assertEquals(((Character) value).charValue(), 'a');

        value = ConvertUtils.convert("java.lang.String", Class.class);
        assertTrue(value instanceof Class);
        assertEquals(String.class, value);

        value = ConvertUtils.convert("123.456", Double.TYPE);
        assertTrue(value instanceof Double);
        assertEquals(((Double) value).doubleValue(), 123.456, 0.005);

        value = ConvertUtils.convert("123.456", Double.class);
        assertTrue(value instanceof Double);
        assertEquals(((Double) value).doubleValue(), 123.456, 0.005);

        value = ConvertUtils.convert("123.456", Float.TYPE);
        assertTrue(value instanceof Float);
        assertEquals(((Float) value).floatValue(), (float) 123.456,
                     (float) 0.005);

        value = ConvertUtils.convert("123.456", Float.class);
        assertTrue(value instanceof Float);
        assertEquals(((Float) value).floatValue(), (float) 123.456,
                     (float) 0.005);

        value = ConvertUtils.convert("123", Integer.TYPE);
        assertTrue(value instanceof Integer);
        assertEquals(((Integer) value).intValue(), 123);

        value = ConvertUtils.convert("123", Integer.class);
        assertTrue(value instanceof Integer);
        assertEquals(((Integer) value).intValue(), 123);

        value = ConvertUtils.convert("123", Long.TYPE);
        assertTrue(value instanceof Long);
        assertEquals(((Long) value).longValue(), 123);

        value = ConvertUtils.convert("123", Long.class);
        assertTrue(value instanceof Long);
        assertEquals(((Long) value).longValue(), 123);

        value = ConvertUtils.convert("123", Short.TYPE);
        assertTrue(value instanceof Short);
        assertEquals(((Short) value).shortValue(), (short) 123);

        value = ConvertUtils.convert("123", Short.class);
        assertTrue(value instanceof Short);
        assertEquals(((Short) value).shortValue(), (short) 123);

        String input = null;

        input = "2002-03-17";
        value = ConvertUtils.convert(input, Date.class);
        assertTrue(value instanceof Date);
        assertEquals(input, value.toString());

        input = "20:30:40";
        value = ConvertUtils.convert(input, Time.class);
        assertTrue(value instanceof Time);
        assertEquals(input, value.toString());

        input = "2002-03-17 20:30:40.0";
        value = ConvertUtils.convert(input, Timestamp.class);
        assertTrue(value instanceof Timestamp);
        assertEquals(input, value.toString());

    }


    /**
     * Positive String to String array tests.
     */
    public void testPositiveStringArray() {

        Object value = null;
        final String stringArray[] = new String[0];
        final String stringArray1[] = new String[]
            { "abc" };
        final String stringArray2[] = new String[]
            { "abc", "de,f" };

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

        value = ConvertUtils.convert("abc 'de,f'",
                                     stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = ConvertUtils.convert("{abc, 'de,f'}",
                                     stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = ConvertUtils.convert("\"abc\",\"de,f\"",
                                     stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = ConvertUtils.convert("{\"abc\" 'de,f'}",
                                     stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = ConvertUtils.convert("'abc' 'de,f'",
                                     stringArray.getClass());
        checkStringArray(value, stringArray2);
        value = ConvertUtils.convert("{'abc', \"de,f\"}",
                                     stringArray.getClass());
        checkStringArray(value, stringArray2);


    }

    public void testSeparateConvertInstances() throws Exception {
        final ConvertUtilsBean utilsOne = new ConvertUtilsBean();
        final ConvertUtilsBean utilsTwo = new ConvertUtilsBean();

        // make sure that the test work ok before anything's changed
        Object
        value = utilsOne.convert("true", Boolean.TYPE);
        assertTrue(value instanceof Boolean);
        assertEquals(
                    "Standard conversion failed (1)",
                    ((Boolean) value).booleanValue(),
                    true);

        value = utilsTwo.convert("true", Boolean.TYPE);
        assertTrue(value instanceof Boolean);
        assertEquals(
                    "Standard conversion failed (2)",
                    ((Boolean) value).booleanValue(),
                    true);

        // now register a test

        utilsOne.register(new ThrowExceptionConverter(), Boolean.TYPE);
        try {

            utilsOne.convert("true", Boolean.TYPE);
            fail("Register converter failed.");

        } catch (final PassTestException e) { /* This shows that the registration has worked */ }

        try {
            // nothing should have changed
            value = utilsTwo.convert("true", Boolean.TYPE);
            assertTrue(value instanceof Boolean);
            assertEquals(
                        "Standard conversion failed (3)",
                        ((Boolean) value).booleanValue(),
                        true);

        } catch (final PassTestException e) {
            // This is a failure since utilsTwo should still have
            // standard converters registered
            fail("Registering a converter for an instance should not effect another instance.");
        }

        // nothing we'll test deregister
        utilsOne.deregister();
        value = utilsOne.convert("true", Boolean.TYPE);
        assertTrue(value instanceof Boolean);
        assertEquals("Instance deregister failed.", ((Boolean) value).booleanValue(), true);

        value = utilsTwo.convert("true", Boolean.TYPE);
        assertTrue(value instanceof Boolean);
        assertEquals(
                    "Standard conversion failed (4)",
                    ((Boolean) value).booleanValue(),
                    true);
    }

    public void testDeregisteringSingleConverter() throws Exception {
        // make sure that the test work ok before anything's changed
        final Object
        value = ConvertUtils.convert("true", Boolean.TYPE);
        assertTrue(value instanceof Boolean);
        assertEquals(
                    "Standard conversion failed (1)",
                    ((Boolean) value).booleanValue(),
                    true);

        // we'll test deregister
        ConvertUtils.deregister(Boolean.TYPE);
        assertNull("Converter should be null",ConvertUtils.lookup(Boolean.TYPE));

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    // We need to use raw types in order to test legacy converters
    public void testConvertToString() throws Exception {
        final Converter dummyConverter = new Converter() {
            public Object convert(final Class type, final Object value) {
                return value;
            }
        };

        final Converter fooConverter = new Converter() {
            public Object convert(final Class type, final Object value) {
                return "Foo-Converter";
            }
        };

        final DateConverter dateConverter = new DateConverter();
        dateConverter.setLocale(Locale.US);

        final ConvertUtilsBean utils = new ConvertUtilsBean();
        utils.register(dateConverter, java.util.Date.class);
        utils.register(fooConverter, String.class);

        // Convert using registerd DateConverter
        final java.util.Date today = new java.util.Date();
        final DateFormat fmt = new SimpleDateFormat("M/d/yy"); /* US Short Format */
        final String expected = fmt.format(today);
        assertEquals("DateConverter M/d/yy", expected, utils.convert(today, String.class));

        // Date converter doesn't do String conversion - use String Converter
        utils.register(dummyConverter, java.util.Date.class);
        assertEquals("Date Converter doesn't do String conversion", "Foo-Converter", utils.convert(today, String.class));

        // No registered Date converter - use String Converter
        utils.deregister(java.util.Date.class);
        assertEquals("No registered Date converter", "Foo-Converter", utils.convert(today, String.class));

        // String Converter doesn't do Strings!!!
        utils.register(dummyConverter, String.class);
        assertEquals("String Converter doesn't do Strings!!!", today.toString(), utils.convert(today, String.class));

        // No registered Date or String converter - use Object's toString()
        utils.deregister(String.class);
        assertEquals("Object's toString()", today.toString(), utils.convert(today, String.class));

    }

    /**
     * Tests a conversion to an unsupported target type.
     */
    public void testConvertUnsupportedTargetType() {
        final ConvertUtilsBean utils = new ConvertUtilsBean();
        final Object value = "A test value";
        assertSame("Got different object", value,
                utils.convert(value, getClass()));
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

