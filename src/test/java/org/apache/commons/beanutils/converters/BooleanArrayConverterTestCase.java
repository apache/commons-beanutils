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

import junit.framework.TestCase;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;

/**
 * Test conversions of String[]->boolean[] and String->boolean[].
 *
 * <p>Note that the tests here don't rigorously test conversions of individual
 * strings to booleans, as the BooleanArrayConverter class uses a
 * BooleanConverter instance to do those conversions, and the BooleanConverter
 * class has its own unit tests. Here, the tests focus on the array-related
 * behaviour.</p>
 *
 * @version $Id$
 */
public class BooleanArrayConverterTestCase extends TestCase {

    public static final String[] STANDARD_TRUES = new String[] {
            "yes", "y", "true", "on", "1"
        };

    public static final String[] STANDARD_FALSES = new String[] {
            "no", "n", "false", "off", "0"
        };


    public BooleanArrayConverterTestCase(final String name) {
        super(name);
    }

    /**
     * Check that an object of type String[] with valid boolean string
     * values gets converted nicely.
     */
    public void testStandardStringArrayConversion() {
        final String[] values = {
            "true", "false",
            "yes", "no",
            "y", "n",
            "1", "0",
        };

        final BooleanArrayConverter converter = new BooleanArrayConverter();
        final boolean[] results = (boolean[]) converter.convert(null, values);

        assertNotNull(results);
        assertEquals(8, results.length);
        assertTrue(results[0]);
        assertFalse(results[1]);
        assertTrue(results[2]);
        assertFalse(results[3]);
        assertTrue(results[4]);
        assertFalse(results[5]);
        assertTrue(results[6]);
        assertFalse(results[7]);
    }

    /**
     * Check that an object whose toString method returns a list of boolean
     * values gets converted nicely.
     */
    public void testStandardStringConversion() {
        final BooleanArrayConverter converter = new BooleanArrayConverter();

        final StringBuilder input = new StringBuilder();
        boolean[] results;

        // string has {}
        input.setLength(0);
        input.append("{true, 'yes', Y, 1, 'FALSE', \"no\", 'n', 0}");
        results = (boolean[]) converter.convert(null, input);

        assertNotNull(results);
        assertEquals(8, results.length);
        assertTrue(results[0]);
        assertTrue(results[1]);
        assertTrue(results[2]);
        assertTrue(results[3]);
        assertFalse(results[4]);
        assertFalse(results[5]);
        assertFalse(results[6]);
        assertFalse(results[7]);

        // string does not have {}
        input.setLength(0);
        input.append("'falsE', 'no', 'N', 0, \"truE\", yeS, 'y', '1'");
        results = (boolean[]) converter.convert(null, input);

        assertNotNull(results);
        assertEquals(8, results.length);
        assertFalse(results[0]);
        assertFalse(results[1]);
        assertFalse(results[2]);
        assertFalse(results[3]);
        assertTrue(results[4]);
        assertTrue(results[5]);
        assertTrue(results[6]);
        assertTrue(results[7]);

        // string has only one element, non-quoted
        input.setLength(0);
        input.append("y");
        results = (boolean[]) converter.convert(null, input);

        assertNotNull(results);
        assertEquals(1, results.length);
        assertTrue(results[0]);

        // string has only one element, quoted with ".
        input.setLength(0);
        input.append("\"1\"");
        results = (boolean[]) converter.convert(null, input);

        assertNotNull(results);
        assertEquals(1, results.length);
        assertTrue(results[0]);

        // string has only one element, quoted with '
        // Here we also pass an object of type String rather than the
        // StringBuilder
        results = (boolean[]) converter.convert(null, "'yes'");

        assertNotNull(results);
        assertEquals(1, results.length);
        assertTrue(results[0]);

    }

    /**
     * Check that the user can specify non-standard true/false values by
     * providing a customised BooleanConverter.
     */
    public void testAdditionalStrings() {
        final String[] trueStrings = {"sure"};
        final String[] falseStrings = {"nope"};
        final BooleanConverter bc = new BooleanConverter(
            trueStrings, falseStrings, BooleanConverter.NO_DEFAULT);
        final BooleanArrayConverter converter = new BooleanArrayConverter(
            bc, BooleanArrayConverter.NO_DEFAULT);

        final boolean[] results = (boolean[]) converter.convert(null, "NOPE, sure, sure");
        assertNotNull(results);
        assertEquals(3, results.length);
        assertFalse(results[0]);
        assertTrue(results[1]);
        assertTrue(results[2]);

        try {
            // the literal string 'true' should no longer be recognized as
            // a true value..
            converter.convert(null, "true");
            fail("Converting invalid string should have generated an exception");
        } catch(final Exception ex) {
            // ok, expected
        }
    }

    /**
     * Check that when the input string cannot be split into a String[], and
     * there is no default value then an exception is thrown.
     */
    public void testInvalidStringWithoutDefault() {
        final BooleanArrayConverter converter = new BooleanArrayConverter();
        try {
            converter.convert(null, "true!");
            fail("Converting invalid string should have generated an exception");
        } catch (final ConversionException expected) {
            // Exception is successful test
        }
    }

    /**
     * Check that when the input string cannot be split into a String[], and
     * there is a default value then that default is returned.
     */
    public void testInvalidStringWithDefault() {
        final boolean[] defaults = new boolean[1];
        final BooleanArrayConverter converter = new BooleanArrayConverter(defaults);
        final Object o = converter.convert(null, "true!");
        assertSame("Unexpected object returned for failed conversion", o, defaults);
    }

    /**
     * Check that when one of the elements in a comma-separated string is not
     * a valid boolean, and there is no default value then an exception is thrown.
     */
    public void testInvalidElementWithoutDefault() {
        final BooleanArrayConverter converter = new BooleanArrayConverter();
        try {
            converter.convert(null, "true,bogus");
            fail("Converting invalid string should have generated an exception");
        } catch (final ConversionException expected) {
            // Exception is successful test
        }
    }

    /**
     * Check that when one of the elements in a comma-separated string is not
     * a valid boolean, and there is a default value then the default value
     * is returned.
     * <p>
     * Note that the default value is for the complete array object returned,
     * not for the failed element.
     */
    public void testInvalidElementWithDefault() {
        final boolean[] defaults = new boolean[1];
        final BooleanArrayConverter converter = new BooleanArrayConverter(defaults);
        final Object o = converter.convert(null, "true,bogus");
        assertSame("Unexpected object returned for failed conversion", o, defaults);
    }

    /**
     * Check that when a custom BooleanConverter is used, and that converter
     * has a (per-element) default, then that element (and just that element)
     * is assigned the default value.
     * <p>
     * With the standard BooleanArrayConverter, if <i>any</i> of the elements
     * in the array are bad, then the array-wide default value is returned.
     * However by specifying a custom BooleanConverter which has a per-element
     * default, the unrecognized elements get that per-element default but the
     * others are converted as expected.
     */
    public void testElementDefault() {
        final boolean[] defaults = new boolean[1];
        final BooleanConverter bc = new BooleanConverter(Boolean.TRUE);
        final BooleanArrayConverter converter = new BooleanArrayConverter(bc, defaults);
        final boolean[] results = (boolean[]) converter.convert(null, "true,bogus");

        assertEquals(2, results.length);
        assertTrue(results[0]);
        assertTrue(results[1]);
    }

    /**
     * Check that registration of a custom converter works.
     */
    public void testRegistration() {
        final String[] trueStrings = {"sure"};
        final String[] falseStrings = {"nope"};
        final BooleanConverter bc = new BooleanConverter(
            trueStrings, falseStrings, BooleanConverter.NO_DEFAULT);

        final BooleanArrayConverter converter = new BooleanArrayConverter(
            bc, BooleanArrayConverter.NO_DEFAULT);

        ConvertUtils.register(converter, BooleanArrayConverter.MODEL);
        final boolean[] sample = new boolean[0];
        final boolean[] results = (boolean[]) ConvertUtils.convert("sure,nope", sample.getClass());

        assertEquals(2, results.length);
        assertTrue(results[0]);
        assertFalse(results[1]);
    }
}
