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
package org.apache.commons.beanutils2.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the ArrayConverter class.
 */
public class ArrayConverterTestCase {

    /**
     * Check that two arrays are the same.
     *
     * @param msg      Test prefix msg
     * @param expected Expected Array value
     * @param result   Result array value
     */
    private void checkArray(final String msg, final Object expected, final Object result) {
        assertNotNull(expected, msg + " Expected Null");
        assertNotNull(result, msg + " Result   Null");
        assertTrue(result.getClass().isArray(), msg + " Result   not array");
        assertTrue(expected.getClass().isArray(), msg + " Expected not array");
        final int resultLth = Array.getLength(result);
        assertEquals(Array.getLength(expected), resultLth, msg + " Size");
        assertEquals(expected.getClass(), result.getClass(), msg + " Type");
        for (int i = 0; i < resultLth; i++) {
            final Object expectElement = Array.get(expected, i);
            final Object resultElement = Array.get(result, i);
            assertEquals(expectElement, resultElement, msg + " Element " + i);
        }
    }

    /** Sets Up */
    @BeforeEach
    public void setUp() throws Exception {
        // empty
    }

    /** Tear Down */
    @AfterEach
    public void tearDown() throws Exception {
        // empty
    }

    /**
     * Test Converting using the IntegerConverter as the component Converter
     */
    @Test
    public void testComponentIntegerConverter() {

        final IntegerConverter intConverter = new IntegerConverter(Integer.valueOf(0));
        intConverter.setPattern("#,###");
        intConverter.setLocale(Locale.US);
        final ArrayConverter arrayConverter = new ArrayConverter(int[].class, intConverter, 0);
        arrayConverter.setAllowedChars(new char[] { ',', '-' });
        arrayConverter.setDelimiter(';');

        // Expected results
        final int[] intArray = { 1111, 2222, 3333, 4444 };
        final String stringA = "1,111; 2,222; 3,333; 4,444";
        final String stringB = intArray[0] + ";" + intArray[1] + ";" + intArray[2] + ";" + intArray[3];
        final String[] strArray = { "" + intArray[0], "" + intArray[1], "" + intArray[2], "" + intArray[3] };
        final long[] longArray = { intArray[0], intArray[1], intArray[2], intArray[3] };
        final Long[] LONGArray = { Long.valueOf(intArray[0]), Long.valueOf(intArray[1]), Long.valueOf(intArray[2]), Long.valueOf(intArray[3]) };
        final Integer[] IntegerArray = { Integer.valueOf(intArray[0]), Integer.valueOf(intArray[1]), Integer.valueOf(intArray[2]),
                Integer.valueOf(intArray[3]) };
        final ArrayList<String> strList = new ArrayList<>();
        final ArrayList<Long> longList = new ArrayList<>();
        for (int i = 0; i < strArray.length; i++) {
            strList.add(strArray[i]);
            longList.add(LONGArray[i]);
        }

        String msg = null;

        // String --> int[]
        msg = "String --> int[]";
        checkArray(msg, intArray, arrayConverter.convert(int[].class, stringA));

        // String --> int[] (with braces)
        msg = "String --> Integer[] (with braces)";
        checkArray(msg, IntegerArray, arrayConverter.convert(Integer[].class, "{" + stringA + "}"));

        // String[] --> int[]
        msg = "String[] --> int[]";
        checkArray(msg, intArray, arrayConverter.convert(int[].class, strArray));

        // String[] --> Integer[]
        msg = "String[] --> Integer[]";
        checkArray(msg, IntegerArray, arrayConverter.convert(Integer[].class, strArray));

        // long[] --> int[]
        msg = "long[] --> int[]";
        checkArray(msg, intArray, arrayConverter.convert(int[].class, longArray));

        // Long --> int[]
        msg = "Long --> int[]";
        checkArray(msg, new int[] { LONGArray[0].intValue() }, arrayConverter.convert(int[].class, LONGArray[0]));

        // LONG[] --> int[]
        msg = "LONG[] --> int[]";
        checkArray(msg, intArray, arrayConverter.convert(int[].class, LONGArray));

        // Long --> String
        msg = "Long --> String";
        assertEquals(LONGArray[0] + "", arrayConverter.convert(String.class, LONGArray[0]), msg);

        // LONG[] --> String (first)
        msg = "LONG[] --> String (first)";
        assertEquals(LONGArray[0] + "", arrayConverter.convert(String.class, LONGArray), msg);

        // LONG[] --> String (all)
        msg = "LONG[] --> String (all)";
        arrayConverter.setOnlyFirstToString(false);
        assertEquals(stringB, arrayConverter.convert(String.class, LONGArray), msg);

        // Collection of Long --> String
        msg = "Collection of Long --> String";
        assertEquals(stringB, arrayConverter.convert(String.class, longList), msg);

        // LONG[] --> String[]
        msg = "long[] --> String[]";
        checkArray(msg, strArray, arrayConverter.convert(String[].class, LONGArray));

        // Collection of String --> Integer[]
        msg = "Collection of String --> Integer[]";
        checkArray(msg, IntegerArray, arrayConverter.convert(Integer[].class, strList));

        // Collection of Long --> int[]
        msg = "Collection of Long --> int[]";
        checkArray(msg, intArray, arrayConverter.convert(int[].class, longList));
    }

    /**
     * Test Empty String
     */
    @Test
    public void testEmptyString() {
        final int[] zeroArray = {};
        final IntegerConverter intConverter = new IntegerConverter();

        checkArray("Empty String", zeroArray, new ArrayConverter(int[].class, intConverter, -1).convert(int[].class, ""));
        assertEquals(null, new ArrayConverter(int[].class, intConverter).convert(String.class, null), "Default String");
    }

    /**
     * Test Errors creating the converter
     */
    @Test
    public void testErrors() {
        assertThrows(NullPointerException.class, () -> new ArrayConverter(null, new DateConverter()));
        assertThrows(IllegalArgumentException.class, () -> new ArrayConverter(Boolean.class, new DateConverter()));
        assertThrows(NullPointerException.class, () -> new ArrayConverter(int[].class, null));
    }

    /**
     * Test Converting using the IntegerConverter as the component Converter
     */
    @Test
    public void testInvalidWithDefault() {
        final int[] zeroArray = {};
        final int[] oneArray = new int[1];
        final IntegerConverter intConverter = new IntegerConverter();

        assertEquals(null, new ArrayConverter(int[].class, intConverter, -1).convert(int[].class, null), "Null Default");
        checkArray("Zero Length", zeroArray, new ArrayConverter(int[].class, intConverter, 0).convert(int[].class, null));
        checkArray("One Length", oneArray, new ArrayConverter(Integer[].class, intConverter, 1).convert(int[].class, null));
    }

    /**
     * Test Converting a String[] to integer array (with leading/trailing whitespace)
     */
    @Test
    public void testStringArrayToNumber() {

        // Configure Converter
        final IntegerConverter intConverter = new IntegerConverter();
        final ArrayConverter arrayConverter = new ArrayConverter(int[].class, intConverter);

        // Test Data
        final String[] array = { "10", "  11", "12  ", "  13  " };
        final ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, array);

        // Expected results
        String msg = null;
        final int[] expectedInt = { 10, 11, 12, 13 };
        final Integer[] expectedInteger = { Integer.valueOf(expectedInt[0]), Integer.valueOf(expectedInt[1]), Integer.valueOf(expectedInt[2]),
                Integer.valueOf(expectedInt[3]) };

        // Test String[] --> int[]
        msg = "String[] --> int[]";
        checkArray(msg, expectedInt, arrayConverter.convert(int[].class, array));

        // Test String[] --> Integer[]
        msg = "String[] --> Integer[]";
        checkArray(msg, expectedInteger, arrayConverter.convert(Integer[].class, array));

        // Test List --> int[]
        msg = "List --> int[]";
        checkArray(msg, expectedInt, arrayConverter.convert(int[].class, list));

        // Test List --> Integer[]
        msg = "List --> Integer[]";
        checkArray(msg, expectedInteger, arrayConverter.convert(Integer[].class, list));
    }

    /**
     * Test the Matrix!!!! (parses a String into a 2 dimensional integer array or matrix)
     */
    @Test
    public void testTheMatrix() {

        // Test Date - create the Matrix!!
        // Following String uses two delimiter:
        // - comma (",") to separate individual numbers
        // - semicolon (";") to separate lists of numbers
        final String matrixString = "11,12,13 ; 21,22,23 ; 31,32,33 ; 41,42,43";
        final int[][] expected = { new int[] { 11, 12, 13 }, new int[] { 21, 22, 23 }, new int[] { 31, 32, 33 }, new int[] { 41, 42, 43 } };

        // Construct an Integer Converter
        final IntegerConverter integerConverter = new IntegerConverter();

        // Construct an array Converter for an integer array (i.e. int[]) using
        // an IntegerConverter as the element converter.
        // N.B. Uses the default comma (i.e. ",") as the delimiter between individual numbers
        final ArrayConverter arrayConverter = new ArrayConverter(int[].class, integerConverter);

        // Construct a "Matrix" Converter which converts arrays of integer arrays using
        // the first (int[]) Converter as the element Converter.
        // N.B. Uses a semicolon (i.e. ";") as the delimiter to separate the different sets of numbers.
        // Also the delimiter for the above array Converter needs to be added to this
        // array Converter's "allowed characters"
        final ArrayConverter matrixConverter = new ArrayConverter(int[][].class, arrayConverter);
        matrixConverter.setDelimiter(';');
        matrixConverter.setAllowedChars(new char[] { ',' });

        // Do the Conversion
        final Object result = matrixConverter.convert(int[][].class, matrixString);

        // Check it actually worked OK
        assertEquals(int[][].class, result.getClass(), "Check int[][].class");
        final int[][] matrix = (int[][]) result;
        assertEquals(expected.length, matrix.length, "Check int[][] length");
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].length, matrix[i].length, "Check int[" + i + "] length");
            for (int j = 0; j < expected[i].length; j++) {
                final String label = "Matrix int[" + i + "," + j + "] element";
                assertEquals(expected[i][j], matrix[i][j], label);
                // System.out.println(label + " = " + matrix[i][j]);
            }
        }
    }

    /**
     * Test for BEANUTILS-302 - throwing a NPE when underscore used
     */
    @Test
    public void testUnderscore_BEANUTILS_302() {
        final String value = "first_value,second_value";
        final ArrayConverter<String[]> converter = new ArrayConverter(String[].class, new StringConverter());

        // test underscore not allowed (the default)
        String[] result = converter.convert(String[].class, value);
        assertNotNull(result, "result.null");
        assertEquals(4, result.length, "result.length");
        assertEquals("first", result[0], "result[0]");
        assertEquals("value", result[1], "result[1]");
        assertEquals("second", result[2], "result[2]");
        assertEquals("value", result[3], "result[3]");

        // configure the converter to allow underscore
        converter.setAllowedChars(new char[] { '.', '-', '_' });

        // test underscore allowed
        result = converter.convert(String[].class, value);
        assertNotNull(result, "result.null");
        assertEquals(2, result.length, "result.length");
        assertEquals("first_value", result[0], "result[0]");
        assertEquals("second_value", result[1], "result[1]");
    }
}
