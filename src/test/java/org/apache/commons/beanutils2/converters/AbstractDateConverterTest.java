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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.Converter;
import org.junit.jupiter.api.Test;

/**
 * Abstract base for &lt;Date&gt;Converter classes.
 *
 * @param <T> type to test
 */
public abstract class AbstractDateConverterTest<T> {

    /**
     * Gets the expected type
     *
     * @return The expected type
     */
    protected abstract Class<T> getExpectedType();

    /**
     * Converts a Date or Calendar objects to the time in milliseconds
     *
     * @param date The date or calendar object
     * @return The time in milliseconds
     */
    protected long getTimeInMillis(final Object date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).getTime();
        }

        if (date instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) date).getTime();
        }

        if (date instanceof LocalDate) {
            return ((LocalDate) date).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        if (date instanceof LocalDateTime) {
            return ((LocalDateTime) date).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        if (date instanceof ZonedDateTime) {
            return ((ZonedDateTime) date).toInstant().toEpochMilli();
        }

        if (date instanceof OffsetDateTime) {
            return ((OffsetDateTime) date).toInstant().toEpochMilli();
        }

        if (date instanceof Calendar) {
            return ((Calendar) date).getTime().getTime();
        }

        if (date instanceof Date) {
            return ((Date) date).getTime();
        }
        throw new IllegalArgumentException(Objects.toString(date));
    }

    /**
     * Test Conversion Error
     *
     * @param converter The converter to use
     * @param value     The value to convert
     */
    protected void invalidConversion(final Converter<T> converter, final Object value) {
        final String valueType = value == null ? "null" : value.getClass().getName();
        final String msg = "Converting '" + valueType + "' value '" + value + "'";
        try {
            final T result = converter.convert(getExpectedType(), value);
            fail(msg + ", expected ConversionException, but result = '" + result + "'");
        } catch (final ConversionException ex) {
            // Expected Result
        }
    }

    /**
     * Create the Converter with no default value.
     *
     * @return A new Converter
     */
    protected abstract DateTimeConverter<T> makeConverter();

    /**
     * Create the Converter with a default value.
     *
     * @param defaultValue The default value
     * @return A new Converter
     */
    protected abstract DateTimeConverter<T> makeConverter(T defaultValue);

    /**
     * Test Conversion to String
     *
     * @param converter The converter to use
     * @param expected  The expected result
     * @param value     The value to convert
     */
    protected void stringConversion(final Converter<T> converter, final String expected, final Object value) {
        final String valueType = value == null ? "null" : value.getClass().getName();
        final String msg = "Converting '" + valueType + "' value '" + value + "' to String";
        try {
            final String result = converter.convert(String.class, value);
            final Class<?> resultType = result == null ? null : result.getClass();
            final Class<?> expectType = expected == null ? null : expected.getClass();
            assertEquals(expectType, resultType, () -> "TYPE " + msg);
            assertEquals(expected, result, () -> "VALUE " + msg);
        } catch (final Exception ex) {
            throw new IllegalStateException(msg + " threw " + ex.toString(), ex);
        }
    }

    /**
     * Assumes convert() returns some non-null instance of getExpectedType().
     */
    @Test
    public void testConvertDate() {
        final String[] message = { "from Date", "from Calendar", "from SQL Date", "from SQL Time", "from SQL Timestamp", "from LocalDate", "from LocalDateTime",
                "from ZonedDateTime", "from OffsetDateTime" };

        final long nowMillis = System.currentTimeMillis();

        final Object[] date = { new Date(nowMillis), new java.util.GregorianCalendar(), new java.sql.Date(nowMillis), new java.sql.Time(nowMillis),
                new java.sql.Timestamp(nowMillis),
                Instant.ofEpochMilli(nowMillis).atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toLocalDate(),
                Instant.ofEpochMilli(nowMillis).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(nowMillis), ZoneId.systemDefault()),
                OffsetDateTime.ofInstant(Instant.ofEpochMilli(nowMillis), ZoneId.systemDefault()) };

        // Initialize calendar also with same ms to avoid a failing test in a new time slice
        ((GregorianCalendar) date[1]).setTime(new Date(nowMillis));

        for (int i = 0; i < date.length; i++) {
            final Class<T> expectedType = getExpectedType();
            final Object val = makeConverter().convert(expectedType, date[i]);
            assertNotNull(val, "Convert " + message[i] + " should not be null");
            assertInstanceOf(expectedType, val, "Convert " + message[i] + " should return a " + expectedType.getName());

            long test = nowMillis;
            if (date[i] instanceof LocalDate || val instanceof LocalDate) {
                test = Instant.ofEpochMilli(nowMillis).atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
                        .toEpochMilli();
            }

            assertEquals(test, getTimeInMillis(val), "Convert " + message[i] + " should return a " + date[0]);
        }
    }

    /**
     * Assumes ConversionException in response to covert(getExpectedType(), null).
     */
    @Test
    public void testConvertNull() {
        assertThrows(ConversionException.class,
                     () -> makeConverter().convert(getExpectedType(), null),
                     "Expected ConversionException");
    }

    /**
     * Test default String to type conversion
     *
     * N.B. This method is overridden by test case implementations for java.sql.Date/Time/Timestamp
     */
    @Test
    public void testDefaultStringToTypeConvert() {

        // Create & Configure the Converter
        final DateTimeConverter<T> converter = makeConverter();
        converter.setUseLocaleFormat(false);
        assertThrows(ConversionException.class,
                     () -> converter.convert(getExpectedType(), "2006-10-23"),
                     "Expected Conversion exception");

    }

    /**
     * Test Default Type conversion (i.e. don't specify target type)
     */
    @Test
    public void testDefaultType() {
        final String pattern = "yyyy-MM-dd";

        // Create & Configure the Converter
        final DateTimeConverter<T> converter = makeConverter();
        converter.setPattern(pattern);

        // Valid String --> Type Conversion
        final String testString = "2006-10-29";
        final Calendar calendar = toCalendar(testString, pattern, null);
        final Object expected = toType(calendar);

        final Object result = converter.convert(null, testString);
        final Class<T> expectedType = getExpectedType();
        if (expectedType.equals(Calendar.class)) {
            assertTrue(expectedType.isAssignableFrom(result.getClass()), "TYPE ");
        } else {
            assertInstanceOf(expectedType, result, "TYPE ");
        }
        assertEquals(expected, result, "VALUE ");
    }

    /**
     * Test Converter with types it can't handle
     */
    @Test
    public void testInvalidType() {

        // Create & Configure the Converter
        @SuppressWarnings("unchecked") // we are creating a mismatch to assert a failure
        final DateTimeConverter<Character> converter = (DateTimeConverter<Character>) makeConverter();

        // Invalid Class Type
        assertThrows(ConversionException.class,
                     ()-> converter.convert(Character.class, new Date()),
                     "Requested Character.class conversion, expected ConversionException");
    }

    /**
     * Test Date Converter with no default value
     */
    @Test
    public void testLocale() {

        // Re-set the default Locale to Locale.US
        final Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        final String pattern = "M/d/yy"; // SHORT style date format for US Locale

        // Create & Configure the Converter
        final DateTimeConverter<T> converter = makeConverter();
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
        invalidConversion(converter, Integer.valueOf(2));

        // Restore the default Locale
        Locale.setDefault(defaultLocale);

    }

    /**
     * Test Converter with multiple patterns
     */
    @Test
    public void testMultiplePatterns() {
        String testString;
        Object expected;

        // Create & Configure the Converter
        final String[] patterns = { "yyyy-MM-dd", "yyyy/MM/dd" };
        final DateTimeConverter<T> converter = makeConverter();
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
     * Test Converter with no default value
     */
    @Test
    public void testPatternDefault() {

        final String pattern = "yyyy-MM-dd";

        // Create & Configure the Converter
        final T defaultValue = toType("2000-01-01", pattern, null);
        assertNotNull(defaultValue, "Check default date");
        final DateTimeConverter<T> converter = makeConverter(defaultValue);
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
        validConversion(converter, defaultValue, Integer.valueOf(2));

    }

    /**
     * Test Converter with no default value
     */
    @Test
    public void testPatternNoDefault() {

        final String pattern = "yyyy-MM-dd";

        // Create & Configure the Converter
        final DateTimeConverter<T> converter = makeConverter();
        converter.setPattern(pattern);

        // Valid String --> Type Conversion
        final String testString = "2006-10-29";
        final Calendar calendar = toCalendar(testString, pattern, null);
        final Object expected = toType(calendar);
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
        invalidConversion(converter, Integer.valueOf(2));

    }

    /**
     * Test Converter with no default value
     */
    @Test
    public void testPatternNullDefault() {

        final String pattern = "yyyy-MM-dd";

        // Create & Configure the Converter
        final T defaultValue = null;
        final DateTimeConverter<T> converter = makeConverter(defaultValue);
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
        validConversion(converter, defaultValue, Integer.valueOf(2));

    }

    /**
     * Test Conversion to String
     */
    @Test
    public void testStringConversion() {

        final String pattern = "yyyy-MM-dd";

        // Create & Configure the Converter
        final DateTimeConverter<T> converter = makeConverter();
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

        // java.time.LocalDateTime --> String Conversion
        stringConversion(converter, expected, toLocalDateTime(calendar));

        stringConversion(converter, null, null);
        stringConversion(converter, "", "");

    }

    /**
     * Parse a String value to a Calendar
     *
     * @param value   The String value to parse
     * @param pattern The date pattern
     * @param locale  The locale to use (or null)
     * @return parsed Calendar value
     */
    Calendar toCalendar(final String value, final String pattern, final Locale locale) {
        Calendar calendar = null;
        try {
            final DateFormat format = locale == null ? new SimpleDateFormat(pattern) : new SimpleDateFormat(pattern, locale);
            format.setLenient(false);
            format.parse(value);
            calendar = format.getCalendar();
        } catch (final Exception e) {
            fail("Error creating Calendar value ='" + value + ", pattern='" + pattern + "' " + e.toString());
        }
        return calendar;
    }

    /**
     * Convert a Calendar to a java.util.Date
     *
     * @param calendar The calendar object to convert
     * @return The converted java.util.Date
     */
    Date toDate(final Calendar calendar) {
        return calendar.getTime();
    }

    /**
     * Convert a Calendar to a java.time.LocalDateTime
     *
     * @param calendar The calendar object to convert
     * @return The converted java.time.LocalDate
     */
    LocalDateTime toLocalDateTime(final Calendar calendar) {
        return Instant.ofEpochMilli(calendar.getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Convert a Calendar to a java.sql.Date
     *
     * @param calendar The calendar object to convert
     * @return The converted java.sql.Date
     */
    java.sql.Date toSqlDate(final Calendar calendar) {
        return new java.sql.Date(getTimeInMillis(calendar));
    }

    /**
     * Convert a Calendar to a java.sql.Time
     *
     * @param calendar The calendar object to convert
     * @return The converted java.sql.Time
     */
    java.sql.Time toSqlTime(final Calendar calendar) {
        return new java.sql.Time(getTimeInMillis(calendar));
    }

    /**
     * Convert a Calendar to a java.sql.Timestamp
     *
     * @param calendar The calendar object to convert
     * @return The converted java.sql.Timestamp
     */
    java.sql.Timestamp toSqlTimestamp(final Calendar calendar) {
        return new java.sql.Timestamp(getTimeInMillis(calendar));
    }

    /**
     * Convert from a Calendar to the appropriate Date type
     *
     * @param value The Calendar value to convert
     * @return The converted value
     */
    protected abstract T toType(Calendar value);

    /**
     * Parse a String value to the required type
     *
     * @param value   The String value to parse
     * @param pattern The date pattern
     * @param locale  The locale to use (or null)
     * @return parsed Calendar value
     */
    protected T toType(final String value, final String pattern, final Locale locale) {
        return toType(toCalendar(value, pattern, locale));
    }

    /**
     * Test Conversion to the required type
     *
     * @param converter The converter to use
     * @param expected  The expected result
     * @param value     The value to convert
     */
    protected void validConversion(final Converter<T> converter, final Object expected, final Object value) {
        final String valueType = value == null ? "null" : value.getClass().getName();
        final String msg = "Converting '" + valueType + "' value '" + value + "'";
        final Object result = assertDoesNotThrow(() -> converter.convert(getExpectedType(), value));
        final Class<?> resultType = result == null ? null : result.getClass();
        final Class<?> expectType = expected == null ? null : expected.getClass();
        assertEquals(expectType, resultType, () -> "TYPE " + msg);
        assertEquals(expected, result, () -> "VALUE " + msg);
    }
}
