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

import org.apache.commons.beanutils2.locale.converters.ByteLocaleConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the ByteLocaleConverter class.
 */
public class ByteLocaleConverterTestCase extends BaseLocaleConverterTestCase<Byte> {

    /**
     * Sets up instance variables required by this test case.
     */
    @Override
    @BeforeEach
    public void setUp() throws Exception {

        super.setUp();

        defaultIntegerPattern = "#,###";
        defaultIntegerValue = ",123";

        localizedIntegerPattern = "#.###";
        localizedIntegerValue = ".123";

        // Expected Values
        expectedDecimalValue = "123.56";
        expectedIntegerValue = "123";

        defaultValue = Byte.valueOf("99");
        expectedValue = Byte.valueOf(expectedIntegerValue);

    }

    /**
     * Test Converter() constructor
     *
     * Uses the default locale, no default value
     */
    @Test
    public void testConstructor_2() {

        // Construct using default locale
        converter = ByteLocaleConverter.builder().get();

        // Perform Tests
        convertValueNoPattern(converter, defaultIntegerValue, expectedValue);
        convertValueWithPattern(converter, defaultIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);

    }

    /**
     * Test Converter(locPattern) constructor
     *
     * Uses the default locale, no default value
     */
    @Test
    public void testConstructor_3() {

        // Construct using localized pattern (default locale)
        converter = ByteLocaleConverter.builder().setLocalizedPattern(true).get();

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
    public void testConstructor_4() {

        // Construct using specified Locale
        converter = ByteLocaleConverter.builder().setLocale(localizedLocale).get();

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
    public void testConstructor_5() {

        // Construct using specified Locale
        converter = ByteLocaleConverter.builder().setLocale(localizedLocale).setLocalizedPattern(true).get();

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
    public void testConstructor_6() {

        // Construct using specified Locale
        converter = ByteLocaleConverter.builder().setLocale(localizedLocale).setPattern(defaultIntegerPattern).get();

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
    public void testConstructor_7() {

        // Construct using specified Locale
        converter = ByteLocaleConverter.builder().setLocale(localizedLocale).setPattern(localizedIntegerPattern).setLocalizedPattern(true).get();

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
    public void testConstructor_8() {

        // Construct using specified Locale
        converter = ByteLocaleConverter.builder().setDefault(defaultValue).get();

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
    public void testConstructor_9() {

        // Construct using specified Locale
        converter = ByteLocaleConverter.builder().setDefault(defaultValue).setLocalizedPattern(true).get();

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
    public void testConstructorMain() {

        // Construct with localized pattern
        converter = ByteLocaleConverter.builder().setDefault(defaultValue).setLocale(localizedLocale).setPattern(localizedIntegerPattern)
                .setLocalizedPattern(true).get();

        convertValueNoPattern(converter, "(A)", localizedIntegerValue, expectedValue);
        convertValueWithPattern(converter, "(A)", localizedIntegerValue, localizedIntegerPattern, expectedValue);
        convertInvalid(converter, "(A)", defaultValue);
        convertNull(converter, "(A)", defaultValue);

        // **************************************************************************
        // Convert value in the wrong format - maybe you would expect it to throw an
        // exception and return the default - it doesn't, DecimalFormat parses it
        // quite happily turning ",123" into "0"
        // I guess this is one of the limitations of DecimalFormat
        // **************************************************************************
        convertValueNoPattern(converter, "(B)", defaultIntegerValue, Byte.valueOf("0"));

        // **************************************************************************
        // Convert with non-localized pattern
        // **************************************************************************
        convertValueWithPattern(converter, "(B)", "123", defaultIntegerPattern, Byte.valueOf("123"));
        convertValueWithPattern(converter, "(B-2)", localizedIntegerValue, defaultIntegerPattern, defaultValue);

        // **************************************************************************
        // Convert with specified type
        //
        // BaseLocaleConverter completely ignores the type - so even if we specify
        // Double.class here it still returns a Byte.
        // **** This has been changed due to BEANUTILS-449 ****
        // **************************************************************************
        // convertValueToType(converter, "(B)", Double.class, localizedIntegerValue, localizedIntegerPattern, expectedValue);

        // Construct with non-localized pattern
        converter = ByteLocaleConverter.builder().setDefault(defaultValue).setLocale(localizedLocale).setPattern(defaultIntegerPattern)
                .setLocalizedPattern(false).get();

        convertValueNoPattern(converter, "(C)", localizedIntegerValue, expectedValue);
        convertValueWithPattern(converter, "(C)", localizedIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, "(C)", defaultValue);
        convertNull(converter, "(C)", defaultValue);

    }

}
