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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.beanutils2.ConversionException;
import org.junit.jupiter.api.Test;

/**
 * Abstract base for &lt;Number&gt;Converter classes.
 * @param <T> Number type.
 */
abstract class AbstractNumberConverterTest<T extends Number> {

    /** Test Number values */
    protected Number[] numbers = new Number[4];

    protected abstract Class<T> getExpectedType();

    protected abstract NumberConverter<T> makeConverter();

    protected abstract NumberConverter<T> makeConverter(T defaultValue);

    /**
     * Convert Boolean --> Number (default conversion)
     */
    @Test
    public void testBooleanToNumberDefault() {
        final NumberConverter<T> converter = makeConverter();

        // Other type --> String conversion
        assertEquals(0, ((Number) converter.convert(getExpectedType(), Boolean.FALSE)).intValue(),
                                "Boolean.FALSE to Number ");
        assertEquals(1, ((Number) converter.convert(getExpectedType(), Boolean.TRUE)).intValue(),
                                "Boolean.TRUE to Number ");
    }

    /**
     * Convert Calendar --> Long
     */
    @Test
    public void testCalendarToNumber() {
        final NumberConverter<T> converter = makeConverter();

        final Calendar calendarValue = Calendar.getInstance();
        final long longValue = calendarValue.getTime().getTime();

        // Calendar --> Long conversion
        assertEquals(Long.valueOf(longValue), converter.convert(Long.class, calendarValue),
                                "Calendar to Long");

        // Calendar --> Integer
        assertThrows(ConversionException.class,
                     ()-> converter.convert(Integer.class, calendarValue),
                     "Calendar to Integer - expected a ConversionException");
    }

    /**
     * Assumes ConversionException in response to covert(getExpectedType(),null).
     */
    @Test
    public void testConvertNull() {
        assertThrows(ConversionException.class,
                     ()-> makeConverter().convert(getExpectedType(), null),
                     "Expected ConversionException");
    }

    /**
     * Assumes convert(getExpectedType(),Number) returns some non-null instance of getExpectedType().
     */
    @Test
    public void testConvertNumber() {
        final String[] message = { "from Byte", "from Short", "from Integer", "from Long", "from Float", "from Double", "from BigDecimal", "from BigInteger",
                "from Integer array", };

        final Object[] number = { Byte.valueOf((byte) 7), Short.valueOf((short) 8), Integer.valueOf(9), Long.valueOf(10), Float.valueOf((float) 11.1),
                Double.valueOf(12.2), new BigDecimal("17.2"), new BigInteger("33"),
                new Integer[] { Integer.valueOf(3), Integer.valueOf(2), Integer.valueOf(1) } };

        for (int i = 0; i < number.length; i++) {
            final Object val = makeConverter().convert(getExpectedType(), number[i]);
            assertNotNull(val, "Convert " + message[i] + " should not be null");
            assertTrue(getExpectedType().isInstance(val),
                                  "Convert " + message[i] + " should return a " + getExpectedType().getName());
        }
    }

    /**
     * Convert Date --> Long
     */
    @Test
    public void testDateToNumber() {
        final NumberConverter<T> converter = makeConverter();

        final Date dateValue = new Date();
        final long longValue = dateValue.getTime();

        // Date --> Long conversion
        assertEquals(Long.valueOf(longValue), converter.convert(Long.class, dateValue), "Date to Long");

        // Date --> Integer
        assertThrows(ConversionException.class,
                     () -> converter.convert(Integer.class, dateValue),
                     "Date to Integer - expected a ConversionException");
    }

    /**
     * Convert Number --> String (using default and specified Locales)
     */
    @Test
    public void testInvalidDefault() {
        final T defaultvalue = (T) numbers[0];
        final NumberConverter<T> converter = makeConverter(defaultvalue);

        // Default String --> Number conversion
        assertEquals(defaultvalue, converter.convert(getExpectedType(), null), "Invalid null ");
        assertEquals(defaultvalue, converter.convert(getExpectedType(), "XXXX"), "Default XXXX ");
    }

    /**
     * Convert Number --> String (using default and specified Locales)
     */
    @Test
    public void testInvalidException() {
        final NumberConverter<T> converter = makeConverter();

        assertThrows(ConversionException.class,
                     () -> converter.convert(getExpectedType(), null),
                     "Null test, expected ConversionException");

        assertThrows(ConversionException.class,
                     () -> converter.convert(getExpectedType(), "XXXX"),
                     "Invalid test, expected ConversionException");
    }

    /**
     * Test specifying an invalid type.
     */
    @Test
    public void testInvalidType() {
        final NumberConverter<T> converter = makeConverter();

        assertThrows(ConversionException.class,
                     () -> converter.convert(Object.class, numbers[0]),
                     "Invalid type test, expected ConversionException");
    }

    /**
     * Tests a conversion to an unsupported type if a default value is set.
     */
    @Test
    public void testInvalidTypeWithDefault() {
        final NumberConverter<T> converter = makeConverter((T) numbers[0]);

        assertThrows(ConversionException.class,
                     () -> converter.convert(Object.class, numbers[0]),
                     "Invalid type with default test, expected ConversionException");
    }

    /**
     * Convert Number --> String (default conversion)
     */
    @Test
    public void testNumberToStringDefault() {
        final NumberConverter<T> converter = makeConverter();

        // Default Number --> String conversion
        assertEquals(numbers[0].toString(), converter.convert(String.class, numbers[0]),
                     () -> "Default Convert " + numbers[0]);
        assertEquals(numbers[1].toString(), converter.convert(String.class, numbers[1]),
                     () -> "Default Convert " + numbers[1]);
    }

    /**
     * Convert Number --> String (using default and specified Locales)
     */
    @Test
    public void testNumberToStringLocale() {
        // Re-set the default Locale to Locale.US
        final Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        final NumberConverter<T> converter = makeConverter();
        converter.setUseLocaleFormat(true);

        // Default Locale
        assertEquals("-12", converter.convert(String.class, numbers[0]), () -> "Default Locale " + numbers[0]);
        assertEquals("13", converter.convert(String.class, numbers[1]), () -> "Default Locale " + numbers[1]);

        // Locale.GERMAN
        converter.setLocale(Locale.GERMAN);
        assertEquals("-22", converter.convert(String.class, numbers[2]), () -> "Locale.GERMAN " + numbers[2]);
        assertEquals("23", converter.convert(String.class, numbers[3]), () -> "Locale.GERMAN " + numbers[3]);

        // Restore the default Locale
        Locale.setDefault(defaultLocale);
    }

    /**
     * Convert Number --> String (using a Pattern, with default and specified Locales)
     */
    @Test
    public void testNumberToStringPattern() {
        // Re-set the default Locale to Locale.US
        final Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        final NumberConverter<T> converter = makeConverter();
        converter.setPattern("[0,0.0];(0,0.0)");

        // Default Locale
        assertEquals("(12.0)", converter.convert(String.class, numbers[0]), () -> "Default Locale " + numbers[0]);
        assertEquals("[13.0]", converter.convert(String.class, numbers[1]), () -> "Default Locale " + numbers[1]);

        // Locale.GERMAN
        converter.setLocale(Locale.GERMAN);
        assertEquals("(22,0)", converter.convert(String.class, numbers[2]), () -> "Locale.GERMAN " + numbers[2]);
        assertEquals("[23,0]", converter.convert(String.class, numbers[3]), () -> "Locale.GERMAN " + numbers[3]);

        // Restore the default Locale
        Locale.setDefault(defaultLocale);
    }

    /**
     * Convert Other --> String (default conversion)
     */
    @Test
    public void testOtherToStringDefault() {
        final NumberConverter<T> converter = makeConverter();

        // Other type --> String conversion
        assertEquals("ABC", converter.convert(String.class, new StringBuilder("ABC")), "Default Convert ");
    }

    /**
     * Convert Array --> Number
     */
    @Test
    public void testStringArrayToInteger() {
        final Integer defaultValue = Integer.valueOf(-1);
        final NumberConverter<Integer> converter = new IntegerConverterTestCase().makeConverter(defaultValue);

        // Default Locale
        assertEquals(Integer.valueOf(5), converter.convert(Integer.class, new String[] { "5", "4", "3" }),
                                "Valid First");
        assertEquals(defaultValue, converter.convert(Integer.class, new String[] { "FOO", "1", "2" }),
                                "Invalid First");
        assertEquals(defaultValue, converter.convert(Integer.class, new String[] { null, "1", "2" }),
                                "Null First");
        assertEquals(Integer.valueOf(9), converter.convert(Integer.class, new long[] { 9, 2, 6 }),
                                "Long Array");
    }

    /**
     * Convert String --> Number (default conversion)
     */
    @Test
    public void testStringToNumberDefault() {
        final NumberConverter<T> converter = makeConverter();
        converter.setUseLocaleFormat(false);

        // Default String --> Number conversion
        assertEquals(numbers[0], converter.convert(getExpectedType(), numbers[0].toString()),
                     () -> "Default Convert " + numbers[0]);

        // Invalid
        assertThrows(ConversionException.class,
                     () -> converter.convert(getExpectedType(), "12x"),
                     "Expected invalid value to cause ConversionException");
    }

    /**
     * Convert String --> Number if the target type is not defined. Then the default type should be used.
     */
    @Test
    public void testStringToNumberDefaultType() {
        final NumberConverter<T> converter = makeConverter();
        converter.setUseLocaleFormat(false);

        assertEquals(numbers[0], converter.convert(null, numbers[0].toString()),
                     () -> "Default Convert " + numbers[0]);
    }

    /**
     * Convert String --> Number (using default and specified Locales)
     */
    @Test
    public void testStringToNumberLocale() {
        // Re-set the default Locale to Locale.US
        final Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        final NumberConverter<T> converter = makeConverter();
        converter.setUseLocaleFormat(true);

        // Default Locale
        assertEquals(numbers[0], converter.convert(getExpectedType(), "-0,012"),
                     () -> "Default Locale " + numbers[0]);
        assertEquals(numbers[1], converter.convert(getExpectedType(), "0,013"),
                     () -> "Default Locale " + numbers[1]);

        // Invalid Value
        assertThrows(ConversionException.class,
                     () -> converter.convert(getExpectedType(), "0,02x"),
                     "Expected invalid value to cause ConversionException");

        // Locale.GERMAN
        converter.setLocale(Locale.GERMAN);
        assertEquals(numbers[2], converter.convert(getExpectedType(), "-0.022"),
                     () -> "Locale.GERMAN " + numbers[2]);
        assertEquals(numbers[3], converter.convert(getExpectedType(), "0.023"),
                     () -> "Locale.GERMAN " + numbers[3]);

        // Invalid Value
        assertThrows(ConversionException.class,
                     () -> converter.convert(getExpectedType(), "0.02x"),
                     "Expected invalid value to cause ConversionException");

        // Restore the default Locale
        Locale.setDefault(defaultLocale);
    }

    /**
     * Convert String --> Number (using a Pattern, with default and specified Locales)
     */
    @Test
    public void testStringToNumberPattern() {

        // Re-set the default Locale to Locale.US
        final Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        final NumberConverter<T> converter = makeConverter();
        converter.setPattern("[0,0];(0,0)");

        // Default Locale
        assertEquals(numbers[0], converter.convert(getExpectedType(), "(1,2)"),
                     () -> "Default Locale " + numbers[0]);
        assertEquals(numbers[1], converter.convert(getExpectedType(), "[1,3]"),
                     () -> "Default Locale " + numbers[1]);

        // Locale.GERMAN
        converter.setLocale(Locale.GERMAN);
        assertEquals(numbers[2], converter.convert(getExpectedType(), "(2.2)"),
                     () -> "Locale.GERMAN " + numbers[2]);
        assertEquals(numbers[3], converter.convert(getExpectedType(), "[2.3]"),
                     () -> "Locale.GERMAN " + numbers[3]);

        // Invalid Value
        assertThrows(ConversionException.class,
                     () -> converter.convert(getExpectedType(), "1,2"),
                     "Expected invalid value to cause ConversionException");

        // Invalid Type (will try via String)
        final Object obj = new Object() {
            @Override
            public String toString() {
                return "dsdgsdsdg";
            }
        };
        assertThrows(ConversionException.class,
                     () -> converter.convert(getExpectedType(), obj),
                     "Expected invalid value to cause ConversionException");

        // Restore the default Locale
        Locale.setDefault(defaultLocale);
    }

    @Test
    public void testToString() {
        assertNotNull(makeConverter().toString());
    }

}
