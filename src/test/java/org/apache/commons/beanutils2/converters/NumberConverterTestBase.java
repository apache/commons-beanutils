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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.beanutils2.ConversionException;

import junit.framework.TestCase;


/**
 * Abstract base for &lt;Number&gt;Converter classes.
 *
 */

public abstract class NumberConverterTestBase extends TestCase {

    /** Test Number values */
    protected Number[] numbers = new Number[4];

    

    public NumberConverterTestBase(final String name) {
        super(name);
    }

    

    protected abstract Class<?> getExpectedType();
    protected abstract NumberConverter makeConverter();
    protected abstract NumberConverter makeConverter(Object defaultValue);

    

    /**
     * Convert Boolean --> Number (default conversion)
     */
    public void testBooleanToNumberDefault() {

        final NumberConverter converter = makeConverter();

        // Other type --> String conversion
        assertEquals("Boolean.FALSE to Number ", 0, ((Number)converter.convert(getExpectedType(), Boolean.FALSE)).intValue());
        assertEquals("Boolean.TRUE to Number ",  1, ((Number)converter.convert(getExpectedType(), Boolean.TRUE)).intValue());

    }

    /**
     * Convert Calendar --> Long
     */
    public void testCalendarToNumber() {

        final NumberConverter converter = makeConverter();

        final Calendar calendarValue = Calendar.getInstance();
        final long longValue = calendarValue.getTime().getTime();

        // Calendar --> Long conversion
        assertEquals("Calendar to Long", new Long(longValue), converter.convert(Long.class, calendarValue));

        // Calendar --> Integer
        try {
            converter.convert(Integer.class, calendarValue);
            fail("Calendar to Integer - expected a ConversionException");
        } catch (final ConversionException e) {
            // expected result - too large for Integer
        }

    }
    /**
     * Assumes ConversionException in response to covert(getExpectedType(),null).
     */
    public void testConvertNull() {
        try {
            makeConverter().convert(getExpectedType(),null);
            fail("Expected ConversionException");
        } catch(final ConversionException e) {
            // expected
        }
    }

    /**
     * Assumes convert(getExpectedType(),Number) returns some non-null
     * instance of getExpectedType().
     */
    public void testConvertNumber() {
        final String[] message= {
            "from Byte",
            "from Short",
            "from Integer",
            "from Long",
            "from Float",
            "from Double",
            "from BigDecimal",
            "from BigInteger",
            "from Integer array",
        };

        final Object[] number = {
            new Byte((byte)7),
            new Short((short)8),
            new Integer(9),
            new Long(10),
            new Float(11.1),
            new Double(12.2),
            new BigDecimal("17.2"),
            new BigInteger("33"),
            new Integer[] {new Integer(3), new Integer(2), new Integer(1)}
        };

        for(int i=0;i<number.length;i++) {
            final Object val = makeConverter().convert(getExpectedType(),number[i]);
            assertNotNull("Convert " + message[i] + " should not be null",val);
            assertTrue(
                "Convert " + message[i] + " should return a " + getExpectedType().getName(),
                getExpectedType().isInstance(val));
        }
    }

    /**
     * Convert Date --> Long
     */
    public void testDateToNumber() {

        final NumberConverter converter = makeConverter();

        final Date dateValue = new Date();
        final long longValue = dateValue.getTime();

        // Date --> Long conversion
        assertEquals("Date to Long", new Long(longValue), converter.convert(Long.class, dateValue));

        // Date --> Integer
        try {
            converter.convert(Integer.class, dateValue);
            fail("Date to Integer - expected a ConversionException");
        } catch (final ConversionException e) {
            // expected result - too large for Integer
        }

    }

    /**
     * Convert Number --> String (using default and specified Locales)
     */
    public void testInvalidDefault() {

        final Object defaultvalue = numbers[0];
        final NumberConverter converter = makeConverter(defaultvalue);

        // Default String --> Number conversion
        assertEquals("Invalid null ", defaultvalue, converter.convert(getExpectedType(), null));
        assertEquals("Default XXXX ", defaultvalue, converter.convert(getExpectedType(), "XXXX"));
    }

    /**
     * Convert Number --> String (using default and specified Locales)
     */
    public void testInvalidException() {

        final NumberConverter converter = makeConverter();

        try {
            converter.convert(getExpectedType(), null);
            fail("Null test, expected ConversionException");
        } catch (final ConversionException e) {
            // expected result
        }
        try {
            converter.convert(getExpectedType(), "XXXX");
            fail("Invalid test, expected ConversionException");
        } catch (final ConversionException e) {
            // expected result
        }
    }

    /**
     * Test specifying an invalid type.
     */
    public void testInvalidType() {

        final NumberConverter converter = makeConverter();

        try {
            converter.convert(Object.class, numbers[0]);
            fail("Invalid type test, expected ConversionException");
        } catch (final ConversionException e) {
            // expected result
        }
    }

    /**
     * Tests a conversion to an unsupported type if a default value is set.
     */
    public void testInvalidTypeWithDefault() {

        final NumberConverter converter = makeConverter(42);

        try {
            converter.convert(Object.class, numbers[0]);
            fail("Invalid type with default test, expected ConversionException");
        } catch(final ConversionException e) {
            // expected result
        }
    }

    /**
     * Convert Number --> String (default conversion)
     */
    public void testNumberToStringDefault() {

        final NumberConverter converter = makeConverter();

        // Default Number --> String conversion
        assertEquals("Default Convert " + numbers[0], numbers[0].toString(), converter.convert(String.class, numbers[0]));
        assertEquals("Default Convert " + numbers[1], numbers[1].toString(), converter.convert(String.class, numbers[1]));

    }

    /**
     * Convert Number --> String (using default and specified Locales)
     */
    public void testNumberToStringLocale() {

        // Re-set the default Locale to Locale.US
        final Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        final NumberConverter converter = makeConverter();
        converter.setUseLocaleFormat(true);

        // Default Locale
        assertEquals("Default Locale " + numbers[0], "-12", converter.convert(String.class, numbers[0]));
        assertEquals("Default Locale " + numbers[1], "13",  converter.convert(String.class, numbers[1]));

        // Locale.GERMAN
        converter.setLocale(Locale.GERMAN);
        assertEquals("Locale.GERMAN " + numbers[2], "-22", converter.convert(String.class, numbers[2]));
        assertEquals("Locale.GERMAN " + numbers[3], "23",  converter.convert(String.class, numbers[3]));

        // Restore the default Locale
        Locale.setDefault(defaultLocale);
    }

    /**
     * Convert Number --> String (using a Pattern, with default and specified Locales)
     */
    public void testNumberToStringPattern() {

        // Re-set the default Locale to Locale.US
        final Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        final NumberConverter converter = makeConverter();
        converter.setPattern("[0,0.0];(0,0.0)");

        // Default Locale
        assertEquals("Default Locale " + numbers[0], "(12.0)", converter.convert(String.class, numbers[0]));
        assertEquals("Default Locale " + numbers[1], "[13.0]", converter.convert(String.class, numbers[1]));

        // Locale.GERMAN
        converter.setLocale(Locale.GERMAN);
        assertEquals("Locale.GERMAN " + numbers[2], "(22,0)", converter.convert(String.class, numbers[2]));
        assertEquals("Locale.GERMAN " + numbers[3], "[23,0]", converter.convert(String.class, numbers[3]));

        // Restore the default Locale
        Locale.setDefault(defaultLocale);
    }

    /**
     * Convert Other --> String (default conversion)
     */
    public void testOtherToStringDefault() {

        final NumberConverter converter = makeConverter();

        // Other type --> String conversion
        assertEquals("Default Convert ", "ABC", converter.convert(String.class, new StringBuilder("ABC")));

    }

    /**
     * Convert Array --> Number
     */
    public void testStringArrayToInteger() {

        final Integer defaultValue = new Integer(-1);
        final NumberConverter converter = makeConverter(defaultValue);

        // Default Locale
        assertEquals("Valid First",   new Integer(5), converter.convert(Integer.class, new String[] {"5", "4", "3"}));
        assertEquals("Invalid First", defaultValue,   converter.convert(Integer.class, new String[] {"FOO", "1", "2"}));
        assertEquals("Null First",    defaultValue,   converter.convert(Integer.class, new String[] {null, "1", "2"}));
        assertEquals("Long Array",    new Integer(9), converter.convert(Integer.class, new long[] {9, 2, 6}));
    }

    /**
     * Convert String --> Number (default conversion)
     */
    public void testStringToNumberDefault() {

        final NumberConverter converter = makeConverter();
        converter.setUseLocaleFormat(false);

        // Default String --> Number conversion
        assertEquals("Default Convert " + numbers[0], numbers[0], converter.convert(getExpectedType(), numbers[0].toString()));

        // Invalid
        try {
            converter.convert(getExpectedType(), "12x");
            fail("Expected invalid value to cause ConversionException");
        } catch (final Exception e) {
            // expected result
        }
    }

    /**
     * Convert String --> Number if the target type is not defined. Then the
     * default type should be used.
     */
    public void testStringToNumberDefaultType() {
        final NumberConverter converter = makeConverter();
        converter.setUseLocaleFormat(false);

        assertEquals("Default Convert " + numbers[0], numbers[0], converter.convert(null, numbers[0].toString()));
    }

    /**
     * Convert String --> Number (using default and specified Locales)
     */
    public void testStringToNumberLocale() {

        // Re-set the default Locale to Locale.US
        final Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        final NumberConverter converter = makeConverter();
        converter.setUseLocaleFormat(true);

        // Default Locale
        assertEquals("Default Locale " + numbers[0], numbers[0], converter.convert(getExpectedType(), "-0,012"));
        assertEquals("Default Locale " + numbers[1], numbers[1], converter.convert(getExpectedType(), "0,013"));

        // Invalid Value
        try {
            converter.convert(getExpectedType(), "0,02x");
            fail("Expected invalid value to cause ConversionException");
        } catch (final Exception e) {
            // expected result
        }

        // Locale.GERMAN
        converter.setLocale(Locale.GERMAN);
        assertEquals("Locale.GERMAN " + numbers[2], numbers[2], converter.convert(getExpectedType(), "-0.022"));
        assertEquals("Locale.GERMAN " + numbers[3], numbers[3], converter.convert(getExpectedType(), "0.023"));

        // Invalid Value
        try {
            converter.convert(getExpectedType(), "0.02x");
            fail("Expected invalid value to cause ConversionException");
        } catch (final Exception e) {
            // expected result
        }

        // Restore the default Locale
        Locale.setDefault(defaultLocale);
    }

    /**
     * Convert String --> Number (using a Pattern, with default and specified Locales)
     */
    public void testStringToNumberPattern() {

        // Re-set the default Locale to Locale.US
        final Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        final NumberConverter converter = makeConverter();
        converter.setPattern("[0,0];(0,0)");

        // Default Locale
        assertEquals("Default Locale " + numbers[0], numbers[0], converter.convert(getExpectedType(), "(1,2)"));
        assertEquals("Default Locale " + numbers[1], numbers[1], converter.convert(getExpectedType(), "[1,3]"));

        // Locale.GERMAN
        converter.setLocale(Locale.GERMAN);
        assertEquals("Locale.GERMAN " + numbers[2], numbers[2], converter.convert(getExpectedType(), "(2.2)"));
        assertEquals("Locale.GERMAN " + numbers[3], numbers[3], converter.convert(getExpectedType(), "[2.3]"));

        // Invalid Value
        try {
            converter.convert(getExpectedType(), "1,2");
            fail("Expected invalid value to cause ConversionException");
        } catch (final Exception e) {
            // expected result
        }

        // Invalid Type (will try via String)
        final Object obj =  new Object() {
            @Override
            public String toString() {
                return "dsdgsdsdg";
            }
        };
        try {
            converter.convert(getExpectedType(), obj);
            fail("Expected invalid value to cause ConversionException");
        } catch (final Exception e) {
            // expected result
        }

        // Restore the default Locale
        Locale.setDefault(defaultLocale);
    }

}

