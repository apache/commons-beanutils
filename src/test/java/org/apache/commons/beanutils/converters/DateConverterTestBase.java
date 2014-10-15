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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import junit.framework.TestCase;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * Abstract base for &lt;Date&gt;Converter classes.
 *
 * @version $Id$
 */

public abstract class DateConverterTestBase extends TestCase {

    // ------------------------------------------------------------------------

    /**
     * Construct a new test case.
     * @param name Name of the test
     */
    public DateConverterTestBase(final String name) {
        super(name);
    }

    // ------------------------------------------------------------------------

    /**
     * Create the Converter with no default value.
     * @return A new Converter
     */
    protected abstract DateTimeConverter makeConverter();

    /**
     * Create the Converter with a default value.
     * @param defaultValue The default value
     * @return A new Converter
     */
    protected abstract DateTimeConverter makeConverter(Object defaultValue);

    /**
     * Return the expected type
     * @return The expected type
     */
    protected abstract Class<?> getExpectedType();

    /**
     * Convert from a Calendar to the appropriate Date type
     *
     * @param value The Calendar value to convert
     * @return The converted value
     */
    protected abstract Object toType(Calendar value);

    // ------------------------------------------------------------------------

    /**
     * Assumes ConversionException in response to covert(getExpectedType(), null).
     */
    public void testConvertNull() {
        try {
            makeConverter().convert(getExpectedType(), null);
            fail("Expected ConversionException");
        } catch(final ConversionException e) {
            // expected
        }
    }

    /**
     * Assumes convert() returns some non-null
     * instance of getExpectedType().
     */
    public void testConvertDate() {
        final String[] message= {
            "from Date",
            "from Calendar",
            "from SQL Date",
            "from SQL Time",
            "from SQL Timestamp"
        };

        final long now = System.currentTimeMillis();

        final Object[] date = {
            new Date(now),
            new java.util.GregorianCalendar(),
            new java.sql.Date(now),
            new java.sql.Time(now),
            new java.sql.Timestamp(now)
        };

        // Initialize calendar also with same ms to avoid a failing test in a new time slice
        ((GregorianCalendar)date[1]).setTime(new Date(now));

        for (int i = 0; i < date.length; i++) {
            final Object val = makeConverter().convert(getExpectedType(), date[i]);
            assertNotNull("Convert " + message[i] + " should not be null", val);
            assertTrue("Convert " + message[i] + " should return a " + getExpectedType().getName(),
                       getExpectedType().isInstance(val));
            assertEquals("Convert " + message[i] + " should return a " + date[0],
                         now, getTimeInMillis(val));
        }
    }

    /**
     * Test Default Type conversion (i.e. don't specify target type)
     */
    public void testDefaultType() {
        final String pattern = "yyyy-MM-dd";

        // Create & Configure the Converter
        final DateTimeConverter converter = makeConverter();
        converter.setPattern(pattern);

        // Valid String --> Type Conversion
        final String testString = "2006-10-29";
        final Calendar calendar = toCalendar(testString, pattern, null);
        final Object expected   = toType(calendar);

        final Object result = converter.convert(null, testString);
        if (getExpectedType().equals(Calendar.class)) {
            assertTrue("TYPE ", getExpectedType().isAssignableFrom(result.getClass()));
        } else {
            assertEquals("TYPE ", getExpectedType(), result.getClass());
        }
        assertEquals("VALUE ", expected, result);
    }

    /**
     * Test default String to type conversion
     *
     * N.B. This method is overridden by test case
     * implementations for java.sql.Date/Time/Timestamp
     */
    public void testDefaultStringToTypeConvert() {

        // Create & Configure the Converter
        final DateTimeConverter converter = makeConverter();
        converter.setUseLocaleFormat(false);
        try {
            converter.convert(getExpectedType(), "2006-10-23");
            fail("Expected Conversion exception");
        } catch (final ConversionException e) {
            // expected result
        }

    }

    /**
     * Test Conversion to String
     */
    public void testStringConversion() {

        final String pattern = "yyyy-MM-dd";

        // Create & Configure the Converter
        final DateTimeConverter converter = makeConverter();
        converter.setPattern(pattern);

        // Create Values
        final String expected = "2006-10-29";
        final Calendar calendar = toCalendar(expected, pattern, null);

        // Type --> String Conversion
        stringConversion(converter, expected, toType(calendar));

        // Calendar --> String Conversion
        stringConversion(converter, expected, calendar);

        // java.util.Date --> String Conversion
        stringConversion(converter, expected, toDate(calendar));

        // java.sql.Date --> String Conversion
        stringConversion(converter, expected, toSqlDate(calendar));

        // java.sql.Timestamp --> String Conversion
        stringConversion(converter, expected, toSqlTimestamp(calendar));

        // java.sql.Time --> String Conversion
        stringConversion(converter, expected, toSqlTime(calendar));

        stringConversion(converter, null, null);
        stringConversion(converter, "", "");

    }

    /**
     * Test Converter with no default value
     */
    public void testPatternNoDefault() {

        final String pattern = "yyyy-MM-dd";

        // Create & Configure the Converter
        final DateTimeConverter converter = makeConverter();
        converter.setPattern(pattern);

        // Valid String --> Type Conversion
        final String testString = "2006-10-29";
        final Calendar calendar = toCalendar(testString, pattern, null);
        final Object expected   = toType(calendar);
        validConversion(converter, expected, testString);

        // Valid java.util.Date --> Type Conversion
        validConversion(converter, expected, calendar);

        // Valid Calendar --> Type Conversion
        validConversion(converter, expected, toDate(calendar));

        // Test java.sql.Date --> Type Conversion
        validConversion(converter, expected, toSqlDate(calendar));

        // java.sql.Timestamp --> String Conversion
        validConversion(converter, expected, toSqlTimestamp(calendar));

        // java.sql.Time --> String Conversion
        validConversion(converter, expected, toSqlTime(calendar));

        // Invalid Conversions
        invalidConversion(converter, null);
        invalidConversion(converter, "");
        invalidConversion(converter, "2006-10-2X");
        invalidConversion(converter, "2006/10/01");
        invalidConversion(converter, "02/10/2006");
        invalidConversion(converter, "02/10/06");
        invalidConversion(converter, new Integer(2));

    }

    /**
     * Test Converter with no default value
     */
    public void testPatternDefault() {

        final String pattern = "yyyy-MM-dd";

        // Create & Configure the Converter
        final Object defaultValue = toType("2000-01-01", pattern, null);
        assertNotNull("Check default date", defaultValue);
        final DateTimeConverter converter = makeConverter(defaultValue);
        converter.setPattern(pattern);

        // Valid String --> Type Conversion
        final String testString = "2006-10-29";
        final Object expected = toType(testString, pattern, null);
        validConversion(converter, expected, testString);

        // Invalid Values, expect default value
        validConversion(converter, defaultValue, null);
        validConversion(converter, defaultValue, "");
        validConversion(converter, defaultValue, "2006-10-2X");
        validConversion(converter, defaultValue, "2006/10/01");
        validConversion(converter, defaultValue, "02/10/06");
        validConversion(converter, defaultValue, new Integer(2));

    }

    /**
     * Test Converter with no default value
     */
    public void testPatternNullDefault() {

        final String pattern = "yyyy-MM-dd";

        // Create & Configure the Converter
        final Object defaultValue = null;
        final DateTimeConverter converter = makeConverter(defaultValue);
        converter.setPattern(pattern);

        // Valid String --> Type Conversion
        final String testString = "2006-10-29";
        final Object expected = toType(testString, pattern, null);
        validConversion(converter, expected, testString);

        // Invalid Values, expect default --> null
        validConversion(converter, defaultValue, null);
        validConversion(converter, defaultValue, "");
        validConversion(converter, defaultValue, "2006-10-2X");
        validConversion(converter, defaultValue, "2006/10/01");
        validConversion(converter, defaultValue, "02/10/06");
        validConversion(converter, defaultValue, new Integer(2));

    }

    /**
     * Test Converter with multiple patterns
     */
    public void testMultiplePatterns() {
        String testString = null;
        Object expected = null;

        // Create & Configure the Converter
        final String[] patterns = new String[] {"yyyy-MM-dd", "yyyy/MM/dd"};
        final DateTimeConverter converter = makeConverter();
        converter.setPatterns(patterns);

        // First Pattern
        testString = "2006-10-28";
        expected = toType(testString, patterns[0], null);
        validConversion(converter, expected, testString);

        // Second pattern
        testString = "2006/10/18";
        expected = toType(testString, patterns[1], null);
        validConversion(converter, expected, testString);

        // Invalid Conversion
        invalidConversion(converter, "17/03/2006");
        invalidConversion(converter, "17.03.2006");

    }

    /**
     * Test Date Converter with no default value
     */
    public void testLocale() {

        // Re-set the default Locale to Locale.US
        final Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        final String pattern = "M/d/yy"; // SHORT style date format for US Locale

        // Create & Configure the Converter
        final DateTimeConverter converter = makeConverter();
        converter.setUseLocaleFormat(true);

        // Valid String --> Type Conversion
        final String testString = "10/28/06";
        final Object expected = toType(testString, pattern, null);
        validConversion(converter, expected, testString);

        // Invalid Conversions
        invalidConversion(converter, null);
        invalidConversion(converter, "");
        invalidConversion(converter, "2006-10-2X");
        invalidConversion(converter, "10.28.06");
        invalidConversion(converter, "10-28-06");
        invalidConversion(converter, new Integer(2));

        // Restore the default Locale
        Locale.setDefault(defaultLocale);

    }

    /**
     * Test Converter with types it can't handle
     */
    public void testInvalidType() {

        // Create & Configure the Converter
        final DateTimeConverter converter = makeConverter();

        // Invalid Class Type
        try {
            converter.convert(Character.class, new Date());
            fail("Requested Character.class conversion, expected ConversionException");
        } catch (final ConversionException e) {
            // Expected result
        }
    }

    /**
     * Test Conversion to the required type
     * @param converter The converter to use
     * @param expected The expected result
     * @param value The value to convert
     */
    void validConversion(final Converter converter, final Object expected, final Object value) {
        final String valueType = (value == null ? "null" : value.getClass().getName());
        final String msg = "Converting '" + valueType + "' value '" + value + "'";
        try {
            final Object result = converter.convert(getExpectedType(), value);
            final Class<?> resultType = (result   == null ? null : result.getClass());
            final Class<?> expectType = (expected == null ? null : expected.getClass());
            assertEquals("TYPE "  + msg, expectType, resultType);
            assertEquals("VALUE " + msg, expected, result);
        } catch (final Exception ex) {
            fail(msg + " threw " + ex.toString());
        }
    }

    /**
     * Test Conversion to String
     * @param converter The converter to use
     * @param expected The expected result
     * @param value The value to convert
     */
    void stringConversion(final Converter converter, final String expected, final Object value) {
        final String valueType = (value == null ? "null" : value.getClass().getName());
        final String msg = "Converting '" + valueType + "' value '" + value + "' to String";
        try {
            final Object result = converter.convert(String.class, value);
            final Class<?> resultType = (result   == null ? null : result.getClass());
            final Class<?> expectType = (expected == null ? null : expected.getClass());
            assertEquals("TYPE "  + msg, expectType, resultType);
            assertEquals("VALUE " + msg, expected, result);
        } catch (final Exception ex) {
            fail(msg + " threw " + ex.toString());
        }
    }

    /**
     * Test Conversion Error
     * @param converter The converter to use
     * @param value The value to convert
     */
    void invalidConversion(final Converter converter, final Object value) {
        final String valueType = (value == null ? "null" : value.getClass().getName());
        final String msg = "Converting '" + valueType + "' value '" + value + "'";
        try {
            final Object result = converter.convert(getExpectedType(), value);
            fail(msg + ", expected ConversionException, but result = '" + result + "'");
        } catch (final ConversionException ex) {
            // Expected Result
        }
    }

    /**
     * Parse a String value to the required type
     * @param value The String value to parse
     * @param pattern The date pattern
     * @param locale The locale to use (or null)
     * @return parsed Calendar value
     */
    Object toType(final String value, final String pattern, final Locale locale) {
        final Calendar calendar = toCalendar(value, pattern, locale);
        return toType(calendar);
    }

    /**
     * Parse a String value to a Calendar
     * @param value The String value to parse
     * @param pattern The date pattern
     * @param locale The locale to use (or null)
     * @return parsed Calendar value
     */
    Calendar toCalendar(final String value, final String pattern, final Locale locale) {
        Calendar calendar = null;
        try {
            final DateFormat format = (locale == null)
                           ? new SimpleDateFormat(pattern)
                           : new SimpleDateFormat(pattern, locale);
            format.setLenient(false);
            format.parse(value);
            calendar = format.getCalendar();
        } catch (final Exception e) {
            fail("Error creating Calendar value ='"
                    + value + ", pattern='" + pattern + "' " + e.toString());
        }
        return calendar;
    }

    /**
     * Convert a Calendar to a java.util.Date
     * @param calendar The calendar object to convert
     * @return The converted java.util.Date
     */
    Date toDate(final Calendar calendar) {
        return calendar.getTime();
    }

    /**
     * Convert a Calendar to a java.sql.Date
     * @param calendar The calendar object to convert
     * @return The converted java.sql.Date
     */
    java.sql.Date toSqlDate(final Calendar calendar) {
        return new java.sql.Date(getTimeInMillis(calendar));
    }

    /**
     * Convert a Calendar to a java.sql.Time
     * @param calendar The calendar object to convert
     * @return The converted java.sql.Time
     */
    java.sql.Time toSqlTime(final Calendar calendar) {
        return new java.sql.Time(getTimeInMillis(calendar));
    }

    /**
     * Convert a Calendar to a java.sql.Timestamp
     * @param calendar The calendar object to convert
     * @return The converted java.sql.Timestamp
     */
    java.sql.Timestamp toSqlTimestamp(final Calendar calendar) {
        return new java.sql.Timestamp(getTimeInMillis(calendar));
    }

    /**
     * Convert a Date or Calendar objects to the time in millisconds
     * @param date The date or calendar object
     * @return The time in milliseconds
     */
    long getTimeInMillis(final Object date) {

        if (date instanceof java.sql.Timestamp) {
            // ---------------------- JDK 1.3 Fix ----------------------
            // N.B. Prior to JDK 1.4 the Timestamp's getTime() method
            //      didn't include the milliseconds. The following code
            //      ensures it works consistently accross JDK versions
            final java.sql.Timestamp timestamp = (java.sql.Timestamp)date;
            long timeInMillis = ((timestamp.getTime() / 1000) * 1000);
            timeInMillis += timestamp.getNanos() / 1000000;
            return timeInMillis;
        }

        if (date instanceof Calendar) {
            return ((Calendar)date).getTime().getTime();
        } else {
            return ((Date)date).getTime();
        }
    }
}
