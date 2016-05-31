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
package org.apache.commons.beanutils.converters;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test Case for the ArrayConverter class.
 *
 * @version $Id$
 */
public class ArrayConverterTestCase extends TestCase {

    /**
     * Construct a new Array Converter test case.
     * @param name Test Name
     */
    public ArrayConverterTestCase(final String name) {
        super(name);
    }

    // ------------------------------------------------------------------------

    /**
     * Create Test Suite
     * @return test suite
     */
    public static TestSuite suite() {
        return new TestSuite(ArrayConverterTestCase.class);
    }

    /** Set Up */
    @Override
    public void setUp() throws Exception {
    }

    /** Tear Down */
    @Override
    public void tearDown() throws Exception {
    }


    // ------------------------------------------------------------------------

    /**
     * Test Converting using the IntegerConverter as the component Converter
     */
    public void testComponentIntegerConverter() {

        final IntegerConverter intConverter = new IntegerConverter(new Integer(0));
        intConverter.setPattern("#,###");
        intConverter.setLocale(Locale.US);
        final ArrayConverter arrayConverter = new ArrayConverter(int[].class, intConverter, 0);
        arrayConverter.setAllowedChars(new char[] {',', '-'});
        arrayConverter.setDelimiter(';');

        // Expected results
        final int[]     intArray     = new int[] {1111, 2222, 3333, 4444};
        final String    stringA      = "1,111; 2,222; 3,333; 4,444";
        final String    stringB      = intArray[0]+ ";" + intArray[1] + ";" + intArray[2] + ";" +intArray[3];
        final String[]  strArray     = new String[] {""+intArray[0], ""+intArray[1], ""+intArray[2], ""+intArray[3]};
        final long[]    longArray    = new long[] {intArray[0], intArray[1], intArray[2], intArray[3]};
        final Long[]    LONGArray    = new Long[]    {new Long(intArray[0]),    new Long(intArray[1]),    new Long(intArray[2]),    new Long(intArray[3])};
        final Integer[] IntegerArray = new Integer[] {new Integer(intArray[0]), new Integer(intArray[1]), new Integer(intArray[2]), new Integer(intArray[3])};
        final ArrayList<String> strList = new ArrayList<String>();
        final ArrayList<Long> longList = new ArrayList<Long>();
        for (int i = 0; i < strArray.length; i++) {
            strList.add(strArray[i]);
            longList.add(LONGArray[i]);
        }


        String msg = null;

        // String --> int[]
        try {
            msg = "String --> int[]";
            checkArray(msg, intArray, arrayConverter.convert(int[].class, stringA));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // String --> int[] (with braces)
        try {
            msg = "String --> Integer[] (with braces)";
            checkArray(msg, IntegerArray, arrayConverter.convert(Integer[].class, "{" + stringA + "}"));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // String[] --> int[]
        try {
            msg = "String[] --> int[]";
            checkArray(msg, intArray, arrayConverter.convert(int[].class, strArray));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // String[] --> Integer[]
        try {
            msg = "String[] --> Integer[]";
            checkArray(msg, IntegerArray, arrayConverter.convert(Integer[].class, strArray));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // long[] --> int[]
        try {
            msg = "long[] --> int[]";
            checkArray(msg, intArray, arrayConverter.convert(int[].class, longArray));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // Long --> int[]
        try {
            msg = "Long --> int[]";
            checkArray(msg, new int[] {LONGArray[0].intValue()}, arrayConverter.convert(int[].class, LONGArray[0]));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // LONG[] --> int[]
        try {
            msg = "LONG[] --> int[]";
            checkArray(msg, intArray, arrayConverter.convert(int[].class, LONGArray));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // Long --> String
        try {
            msg = "Long --> String";
            assertEquals(msg, LONGArray[0] + "", arrayConverter.convert(String.class, LONGArray[0]));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // LONG[] --> String (first)
        try {
            msg = "LONG[] --> String (first)";
            assertEquals(msg, LONGArray[0] + "", arrayConverter.convert(String.class, LONGArray));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // LONG[] --> String (all)
        try {
            msg = "LONG[] --> String (all)";
            arrayConverter.setOnlyFirstToString(false);
            assertEquals(msg, stringB, arrayConverter.convert(String.class, LONGArray));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // Collection of Long --> String
        try {
            msg = "Collection of Long --> String";
            assertEquals(msg, stringB, arrayConverter.convert(String.class, longList));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // LONG[] --> String[]
        try {
            msg = "long[] --> String[]";
            checkArray(msg, strArray, arrayConverter.convert(String[].class, LONGArray));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // Collection of String --> Integer[]
        try {
            msg = "Collection of String --> Integer[]";
            checkArray(msg, IntegerArray, arrayConverter.convert(Integer[].class, strList));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // Collection of Long --> int[]
        try {
            msg = "Collection of Long --> int[]";
            checkArray(msg, intArray, arrayConverter.convert(int[].class, longList));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }
    }

    /**
     * Test Converting a String[] to integer array (with leading/trailing whitespace)
     */
    public void testStringArrayToNumber() {

        // Configure Converter
        final IntegerConverter intConverter = new IntegerConverter();
        final ArrayConverter arrayConverter = new ArrayConverter(int[].class, intConverter);

        // Test Data
        final String[] array = new String[] {"10", "  11", "12  ", "  13  "};
        final ArrayList<String> list = new ArrayList<String>();
        for (String element : array) {
            list.add(element);
        }

        // Expected results
        String msg = null;
        final int[]     expectedInt     = new int[] {10, 11, 12, 13};
        final Integer[] expectedInteger = new Integer[] {new Integer(expectedInt[0]), new Integer(expectedInt[1]), new Integer(expectedInt[2]), new Integer(expectedInt[3])};

        // Test String[] --> int[]
        try {
            msg = "String[] --> int[]";
            checkArray(msg, expectedInt, arrayConverter.convert(int[].class, array));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // Test String[] --> Integer[]
        try {
            msg = "String[] --> Integer[]";
            checkArray(msg, expectedInteger, arrayConverter.convert(Integer[].class, array));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // Test List --> int[]
        try {
            msg = "List --> int[]";
            checkArray(msg, expectedInt, arrayConverter.convert(int[].class, list));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }

        // Test List --> Integer[]
        try {
            msg = "List --> Integer[]";
            checkArray(msg, expectedInteger, arrayConverter.convert(Integer[].class, list));
        } catch (final Exception e) {
            fail(msg + " failed " + e);
        }
   }

    /**
     * Test the Matrix!!!! (parses a String into a 2 dimensional integer array or matrix)
     */
    public void testTheMatrix() {

        // Test Date - create the Matrix!!
        // Following String uses two delimiter:
        //     - comma (",") to separate individual numbers
        //     - semi-colon (";") to separate lists of numbers
        final String matrixString = "11,12,13 ; 21,22,23 ; 31,32,33 ; 41,42,43";
        final int[][] expected = new int[][] {new int[] {11, 12, 13},
                                        new int[] {21, 22, 23},
                                        new int[] {31, 32, 33},
                                        new int[] {41, 42, 43}};

        // Construct an Integer Converter
        final IntegerConverter integerConverter = new IntegerConverter();

        // Construct an array Converter for an integer array (i.e. int[]) using
        // an IntegerConverter as the element converter.
        // N.B. Uses the default comma (i.e. ",") as the delimiter between individual numbers
        final ArrayConverter arrayConverter = new ArrayConverter(int[].class, integerConverter);

        // Construct a "Matrix" Converter which converts arrays of integer arrays using
        // the first (int[]) Converter as the element Converter.
        // N.B. Uses a semi-colon (i.e. ";") as the delimiter to separate the different sets of numbers.
        //      Also the delimiter for the above array Converter needs to be added to this
        //      array Converter's "allowed characters"
        final ArrayConverter matrixConverter = new ArrayConverter(int[][].class, arrayConverter);
        matrixConverter.setDelimiter(';');
        matrixConverter.setAllowedChars(new char[] {','});

        try {
            // Do the Conversion
            final Object result = matrixConverter.convert(int[][].class, matrixString);

            // Check it actually worked OK
            assertEquals("Check int[][].class", int[][].class, result.getClass());
            final int[][] matrix = (int[][])result;
            assertEquals("Check int[][] length", expected.length, matrix.length);
            for (int i = 0; i < expected.length; i++) {
                assertEquals("Check int[" + i + "] length", expected[i].length, matrix[i].length);
                for (int j = 0; j < expected[i].length; j++) {
                    final String label = "Matrix int[" + i + "," + j + "] element";
                    assertEquals(label, expected[i][j], matrix[i][j]);
                    // System.out.println(label + " = " + matrix[i][j]);
                }
            }
        } catch (final Exception e) {
            fail("Matrix Conversion threw " + e);
        }
    }

    /**
     * Test Converting using the IntegerConverter as the component Converter
     */
    public void testInvalidWithDefault() {
        final int[]  zeroArray  = new int[0];
        final int[]  oneArray   = new int[1];
        final IntegerConverter intConverter = new IntegerConverter();

        assertEquals("Null Default", null,   new ArrayConverter(int[].class, intConverter, -1).convert(int[].class, null));
        checkArray("Zero Length",  zeroArray, new ArrayConverter(int[].class, intConverter, 0).convert(int[].class, null));
        checkArray("One Length",   oneArray,  new ArrayConverter(Integer[].class, intConverter, 1).convert(int[].class, null));
    }

    /**
     * Test Empty String
     */
    public void testEmptyString() {
        final int[]  zeroArray  = new int[0];
        final IntegerConverter intConverter = new IntegerConverter();

        checkArray("Empty String",  zeroArray, new ArrayConverter(int[].class, intConverter, -1).convert(int[].class, ""));
        assertEquals("Default String",  null, new ArrayConverter(int[].class, intConverter).convert(String.class, null));
    }

    /**
     * Test Errors creating the converter
     */
    public void testErrors() {
        try {
            new ArrayConverter(null, new DateConverter());
            fail("Default Type missing - expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // expected result
        }
        try {
            new ArrayConverter(Boolean.class, new DateConverter());
            fail("Default Type not an array - expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // expected result
        }
        try {
            new ArrayConverter(int[].class, null);
            fail("Component Converter missing - expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // expected result
        }
    }

    /**
     * Test for BEANUTILS-302 - throwing a NPE when underscore used
     */
    public void testUnderscore_BEANUTILS_302() {
        final String value = "first_value,second_value";
        final ArrayConverter converter = new ArrayConverter(String[].class, new StringConverter());

        // test underscore not allowed (the default)
        String[] result = converter.convert(String[].class, value);
        assertNotNull("result.null", result);
        assertEquals("result.length", 4, result.length);
        assertEquals("result[0]", "first", result[0]);
        assertEquals("result[1]", "value", result[1]);
        assertEquals("result[2]", "second", result[2]);
        assertEquals("result[3]", "value", result[3]);

        // configure the converter to allow underscore
        converter.setAllowedChars(new char[] {'.', '-', '_'});

        // test underscore allowed
        result = converter.convert(String[].class, value);
        assertNotNull("result.null", result);
        assertEquals("result.length", 2, result.length);
        assertEquals("result[0]", "first_value", result[0]);
        assertEquals("result[1]", "second_value", result[1]);
    }

    /**
     * Check that two arrays are the same.
     * @param msg Test prefix msg
     * @param expected Expected Array value
     * @param result Result array value
     */
    private void checkArray(final String msg, final Object expected, final Object result) {
        assertNotNull(msg + " Expected Null", expected);
        assertNotNull(msg + " Result   Null", result);
        assertTrue(msg + " Result   not array", result.getClass().isArray());
        assertTrue(msg + " Expected not array", expected.getClass().isArray());
        final int resultLth = Array.getLength(result);
        assertEquals(msg + " Size", Array.getLength(expected), resultLth);
        assertEquals(msg + " Type", expected.getClass(), result.getClass());
        for (int i = 0; i < resultLth; i++) {
            final Object expectElement = Array.get(expected, i);
            final Object resultElement = Array.get(result, i);
            assertEquals(msg + " Element " + i, expectElement, resultElement);
        }
    }
}
