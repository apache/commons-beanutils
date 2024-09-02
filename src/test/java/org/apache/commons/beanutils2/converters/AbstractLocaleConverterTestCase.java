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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Locale;

import org.apache.commons.beanutils2.locale.BaseLocaleConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base Test Case for the DecimalLocaleConverter classes. This class doesn't define any real tests; it just provides useful methods for the real test case
 * classes to inherit.
 *
 * @param <T> The converter type.
 */
public abstract class AbstractLocaleConverterTestCase<T> {

    // Original Default Locale
    protected Locale origLocale;

    // Converter
    protected BaseLocaleConverter<T> converter;
    protected Object result;
    protected T defaultValue;
    protected Object expectedValue;

    // Localized values
    protected Locale localizedLocale;
    protected String localizedDecimalPattern;
    protected String localizedIntegerPattern;
    protected String localizedDecimalValue;
    protected String localizedIntegerValue;

    // Locale values
    protected Locale defaultLocale;
    protected String defaultDecimalPattern;
    protected String defaultIntegerPattern;
    protected String defaultDecimalValue;
    protected String defaultIntegerValue;

    // Expected values
    protected String expectedDecimalValue;
    protected String expectedIntegerValue;

    /**
     * Test Converting an invalid value.
     */
    protected void convertInvalid(final BaseLocaleConverter<T> converter, final Object expectedValue) {
        convertInvalid(converter, "", expectedValue);
    }

    /**
     * Test Converting an invalid value.
     */
    protected void convertInvalid(final BaseLocaleConverter<T> converter, final String msgId, final Object expectedValue) {
        // Convert value with no pattern
        try {
            result = converter.convert("xyz");
            if (expectedValue == null) {
                fail("Expected ConversionException if no default value " + msgId + ", converter = " + converter);
            }
        } catch (final Exception e) {
            if (expectedValue != null) {
                fail("Expected default value '" + msgId + "' threw " + e + ", expectedValue = '" + expectedValue + "'");
            }
        }

        if (expectedValue != null) {
            assertEquals(expectedValue, result, () -> "Check invalid conversion is default " + msgId);
        }
    }

    /**
     * Test Converting Null value.
     */
    protected void convertNull(final BaseLocaleConverter<T> converter, final Object expectedValue) {
        convertNull(converter, "", expectedValue);
    }

    /**
     * Test Converting Null value.
     */
    protected void convertNull(final BaseLocaleConverter<T> converter, final String msgId, final Object expectedValue) {
        // Convert value with no pattern
        result = assertDoesNotThrow(() -> converter.convert(null), () -> "Null conversion threw '" + msgId + "'");

        if (expectedValue == null) {
            assertNull(result, () -> "Check null conversion is null '" + msgId + "' result=" + result);
        } else {
            assertEquals(expectedValue, result, () -> "Check null conversion is default " + msgId);
        }
    }

    /**
     * Test Converting Value WITHOUT a pattern
     */
    protected void convertValueNoPattern(final BaseLocaleConverter<T> converter, final Object value, final Object expectedValue) {
        convertValueNoPattern(converter, "", value, expectedValue);
    }

    /**
     * Test Converting Value WITHOUT a pattern
     */
    protected void convertValueNoPattern(final BaseLocaleConverter<T> converter, final String msgId, final Object value, final Object expectedValue) {
        // Convert value with no pattern
        result = assertDoesNotThrow(() -> converter.convert(value), () -> "No Pattern conversion threw '" + msgId + "'");
        assertEquals(expectedValue, result, () -> "Check conversion value without pattern " + msgId);

    }

    /**
     * Test Converting Value WITH a pattern
     */
    protected void convertValueWithPattern(final BaseLocaleConverter<T> converter, final Object value, final String pattern, final Object expectedValue) {
        convertValueWithPattern(converter, "", value, pattern, expectedValue);
    }

    /**
     * Test Converting Value WITH a pattern
     */
    protected void convertValueWithPattern(final BaseLocaleConverter<T> converter, final String msgId, final Object value, final String pattern,
            final Object expectedValue) {
        // Convert value with no pattern
        result = assertDoesNotThrow(() -> converter.convert(value, pattern), () -> "Pattern conversion threw '" + msgId + "'");
        assertEquals(expectedValue, result, () -> "Check conversion value with pattern " + msgId);
    }

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() throws Exception {

        // Default Locale (Use US)
        defaultLocale = Locale.US;
        defaultDecimalPattern = "#,###.00";
        defaultIntegerPattern = "#,###";
        defaultDecimalValue = "1,234.56";
        defaultIntegerValue = "1,234";

        // Use German Locale (uses different separators to US)
        localizedLocale = Locale.GERMAN;
        localizedDecimalPattern = "#.###,00";
        localizedIntegerPattern = "#.###";
        localizedDecimalValue = "1.234,56";
        localizedIntegerValue = "1.234";

        // Expected Values
        expectedDecimalValue = "1234.56";
        expectedIntegerValue = "1234";

        // Reset default to the one specified
        origLocale = Locale.getDefault();

        // Initialize
        converter = null;
        result = null;
        defaultValue = null;
        expectedValue = null;

        if (defaultLocale.equals(origLocale)) {
            origLocale = null;
        } else {
            // System.out.println("Changing default locale from " + origLocale + " to " + defaultLocale);
            Locale.setDefault(defaultLocale);
        }
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        converter = null;
        result = null;
        defaultValue = null;
        expectedValue = null;

        // Set the Default Locale back to the original value
        if (origLocale != null) {
            // System.out.println("Restoring default locale to " + origLocale);
            Locale.setDefault(origLocale);
        }
    }
}
