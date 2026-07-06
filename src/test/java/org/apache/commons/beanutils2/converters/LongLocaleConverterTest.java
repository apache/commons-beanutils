/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.beanutils2.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.DecimalFormat;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.locale.converters.LongLocaleConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the LongLocaleConverter class.
 */
class LongLocaleConverterTest extends AbstractLocaleConverterTest<Long> {

    /**
     * Sets up instance variables required by this test case.
     */
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        defaultValue = Long.valueOf("999");
        expectedValue = Long.valueOf(expectedIntegerValue);
    }

    /**
     * Test Converter() constructor Uses the default locale, no default value
     */
    @Test
    void testConstructor_2() {
        // Construct using default locale
        converter = LongLocaleConverter.builder().get();
        // Perform Tests
        convertValueNoPattern(converter, defaultIntegerValue, expectedValue);
        convertValueWithPattern(converter, defaultIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);
    }

    /**
     * Test Converter(locPattern) constructor Uses the default locale, no default value
     */
    @Test
    void testConstructor_3() {
        // Construct using localized pattern (default locale)
        converter = LongLocaleConverter.builder().setLocalizedPattern(true).get();
        // Perform Tests
        convertValueNoPattern(converter, defaultIntegerValue, expectedValue);
        convertValueWithPattern(converter, defaultIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);
    }

    /**
     * Test Converter(Locale) constructor
     */
    @Test
    void testConstructor_4() {
        // Construct using specified Locale
        converter = LongLocaleConverter.builder().setLocale(localizedLocale).get();
        // Perform Tests
        convertValueNoPattern(converter, localizedIntegerValue, expectedValue);
        convertValueWithPattern(converter, localizedIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);
    }

    /**
     * Test Converter(Locale, locPattern) constructor
     */
    @Test
    void testConstructor_5() {
        // Construct using specified Locale
        converter = LongLocaleConverter.builder().setLocale(localizedLocale).setLocalizedPattern(true).get();
        // Perform Tests
        convertValueNoPattern(converter, localizedIntegerValue, expectedValue);
        convertValueWithPattern(converter, localizedIntegerValue, localizedIntegerPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);
    }

    /**
     * Test Converter(Locale, pattern) constructor
     */
    @Test
    void testConstructor_6() {
        // Construct using specified Locale
        converter = LongLocaleConverter.builder().setLocale(localizedLocale).setPattern(defaultIntegerPattern).get();
        // Perform Tests
        convertValueNoPattern(converter, localizedIntegerValue, expectedValue);
        convertValueWithPattern(converter, localizedIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);
    }

    /**
     * Test Converter(Locale, pattern, locPattern) constructor
     */
    @Test
    void testConstructor_7() {
        // Construct using specified Locale
        converter = LongLocaleConverter.builder().setLocale(localizedLocale).setPattern(localizedIntegerPattern).setLocalizedPattern(true).get();
        // Perform Tests
        convertValueNoPattern(converter, localizedIntegerValue, expectedValue);
        convertValueWithPattern(converter, localizedIntegerValue, localizedIntegerPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);
    }

    /**
     * Test Converter(defaultValue) constructor
     */
    @Test
    void testConstructor_8() {
        // Construct using specified Locale
        converter = LongLocaleConverter.builder().setDefault(defaultValue).get();
        // Perform Tests
        convertValueNoPattern(converter, defaultIntegerValue, expectedValue);
        convertValueWithPattern(converter, defaultIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, defaultValue);
        convertNull(converter, defaultValue);
    }

    /**
     * Test Converter(defaultValue, locPattern) constructor
     */
    @Test
    void testConstructor_9() {
        // Construct using specified Locale
        converter = LongLocaleConverter.builder().setDefault(defaultValue).setLocalizedPattern(true).get();
        // Perform Tests
        convertValueNoPattern(converter, defaultIntegerValue, expectedValue);
        convertValueWithPattern(converter, defaultIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, defaultValue);
        convertNull(converter, defaultValue);
    }

    /**
     * Test Converter(defaultValue, locale, pattern, localizedPattern) constructor
     */
    @Test
    void testConstructorMain() {
        // Construct with localized pattern
        converter = LongLocaleConverter.builder().setDefault(defaultValue).setLocale(localizedLocale).setPattern(localizedIntegerPattern)
                .setLocalizedPattern(true).get();
        convertValueNoPattern(converter, "(A)", localizedIntegerValue, expectedValue);
        convertValueWithPattern(converter, "(A)", localizedIntegerValue, localizedIntegerPattern, expectedValue);
        convertInvalid(converter, "(A)", defaultValue);
        convertNull(converter, "(A)", defaultValue);
        // **************************************************************************
        // Convert value in the wrong format - the localized (German) converter reads
        // ',' as the decimal separator, so "1,234" parses to the fractional value
        // 1.234; the integer converter now rejects a non-integer result and returns
        // the default.
        // **************************************************************************
        convertValueNoPattern(converter, "(B)", defaultIntegerValue, defaultValue);
        // **************************************************************************
        // Convert with non-localized pattern - the trailing characters left after the
        // partial parse are now rejected, so the converter returns the default.
        // **************************************************************************
        convertValueWithPattern(converter, "(B)", localizedIntegerValue, defaultIntegerPattern, defaultValue);
        // **************************************************************************
        // Convert with specified type
        //
        // BaseLocaleConverter completely ignores the type - so even if we specify
        // Double.class here it still returns a Long.
        // **** This has been changed due to BEANUTILS-449 ****
        // **************************************************************************
        // convertValueToType(converter, "(B)", Double.class, localizedIntegerValue, localizedIntegerPattern, expectedValue);
        // Construct with non-localized pattern
        converter = LongLocaleConverter.builder().setDefault(defaultValue).setLocale(localizedLocale).setPattern(defaultIntegerPattern)
                .setLocalizedPattern(false).get();
        convertValueNoPattern(converter, "(C)", localizedIntegerValue, expectedValue);
        convertValueWithPattern(converter, "(C)", localizedIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, "(C)", defaultValue);
        convertNull(converter, "(C)", defaultValue);
    }

    /**
     * Test Long limits
     */
    @Test
    void testLongLimits() {
        converter = LongLocaleConverter.builder().setLocale(defaultLocale).get();
        final DecimalFormat fmt = new DecimalFormat("#");
        assertEquals(Long.valueOf(Long.MAX_VALUE), converter.convert(fmt.format(Long.MAX_VALUE)), "Long.MAX_VALUE");
        assertEquals(Long.valueOf(Long.MIN_VALUE), converter.convert(fmt.format(Long.MIN_VALUE)), "Long.MIN_VALUE");
        assertThrows(ConversionException.class, () -> converter.convert("99999999999999999999"));
        assertThrows(ConversionException.class, () -> converter.convert("-99999999999999999999"));
    }

    /**
     * Tests that a non-integer value is rejected rather than silently truncated to an integer.
     */
    @Test
    void testNonIntegerRejected() {
        converter = LongLocaleConverter.builder().setDefault(defaultValue).setLocale(defaultLocale).get();
        convertValueNoPattern(converter, "non-integer", "5.5", defaultValue);
    }
}
