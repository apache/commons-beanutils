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

package org.apache.commons.beanutils2.locale.converters;

import java.math.BigInteger;

import org.apache.commons.beanutils2.ConversionException;

/**
 * Test Case for the BigIntegerLocaleConverter class.
 *
 */

public class BigIntegerLocaleConverterTestCase extends BaseLocaleConverterTestCase {



    

    public BigIntegerLocaleConverterTestCase(final String name) {
        super(name);
    }

    

    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {

        super.setUp();

        defaultValue  = new BigInteger("999");
        expectedValue = new BigInteger(expectedIntegerValue);

    }

    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {
        super.tearDown();
    }


    

    /**
     * Test Converter(defaultValue, locale, pattern, localizedPattern) constructor
     */
    public void testConstructorMain() {

        // ------------- Construct with localized pattern ------------
        converter = new BigIntegerLocaleConverter(defaultValue,
                                                  localizedLocale,
                                                  localizedIntegerPattern,
                                                  true);


        convertValueNoPattern(converter, "(A)", localizedIntegerValue, expectedValue);
        convertValueWithPattern(converter, "(A)", localizedIntegerValue, localizedIntegerPattern, expectedValue);
        convertInvalid(converter, "(A)", defaultValue);
        convertNull(converter, "(A)", defaultValue);


        // **************************************************************************
        // Convert value in the wrong format - maybe you would expect it to throw an
        // exception and return the default - it doesn't, DecimalFormat parses it
        // quite happily turning "1,234" into "1"
        // I guess this is one of the limitations of DecimalFormat
        // **************************************************************************
        convertValueNoPattern(converter, "(B)", defaultIntegerValue, new BigInteger("1"));


        // **************************************************************************
        // Convert with non-localized pattern - unlike the equivalent BigDecimal Test Case
        // it doesn't causes an exception in parse() - DecimalFormat parses it
        // quite happily turning "1,234" into "1"
        // Again this is one of the limitations of DecimalFormat
        // **************************************************************************
        convertValueWithPattern(converter, "(B)", localizedIntegerValue, defaultIntegerPattern, new BigInteger("1"));


        // **************************************************************************
        // Convert with specified type
        //
        // BaseLocaleConverter completely ignores the type - so even if we specify
        // Double.class here it still returns a BigInteger.
        //  **** This has been changed due to BEANUTILS-449 ****
        // **************************************************************************
        //convertValueToType(converter, "(B)", Double.class, localizedIntegerValue, localizedIntegerPattern, expectedValue);


        // ------------- Construct with non-localized pattern ------------
        converter = new BigIntegerLocaleConverter(defaultValue,
                                                  localizedLocale,
                                                  defaultIntegerPattern,
                                                  false);


        convertValueNoPattern(converter, "(C)", localizedIntegerValue, expectedValue);
        convertValueWithPattern(converter, "(C)", localizedIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, "(C)", defaultValue);
        convertNull(converter, "(C)", defaultValue);

    }

    /**
     * Test Converter() constructor
     *
     * Uses the default locale, no default value
     *
     */
    public void testConstructor_2() {

        // ------------- Construct using default locale ------------
        converter = new BigIntegerLocaleConverter();

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
     *
     */
    public void testConstructor_3() {

        // ------------- Construct using localized pattern (default locale) --------
        converter = new BigIntegerLocaleConverter(true);

        // Perform Tests
        convertValueNoPattern(converter, defaultIntegerValue, expectedValue);
        convertValueWithPattern(converter, defaultIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);


    }

    /**
     * Test Converter(Locale) constructor
     */
    public void testConstructor_4() {

        // ------------- Construct using specified Locale --------
        converter = new BigIntegerLocaleConverter(localizedLocale);

        // Perform Tests
        convertValueNoPattern(converter, localizedIntegerValue, expectedValue);
        convertValueWithPattern(converter, localizedIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);


    }


    /**
     * Test Converter(Locale, locPattern) constructor
     */
    public void testConstructor_5() {

        // ------------- Construct using specified Locale --------
        converter = new BigIntegerLocaleConverter(localizedLocale, true);

        // Perform Tests
        convertValueNoPattern(converter, localizedIntegerValue, expectedValue);
        convertValueWithPattern(converter, localizedIntegerValue, localizedIntegerPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);


    }

    /**
     * Test Converter(Locale, pattern) constructor
     */
    public void testConstructor_6() {

        // ------------- Construct using specified Locale --------
        converter = new BigIntegerLocaleConverter(localizedLocale, defaultIntegerPattern);

        // Perform Tests
        convertValueNoPattern(converter, localizedIntegerValue, expectedValue);
        convertValueWithPattern(converter, localizedIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);

    }

    /**
     * Test Converter(Locale, pattern, locPattern) constructor
     */
    public void testConstructor_7() {

        // ------------- Construct using specified Locale --------
        converter = new BigIntegerLocaleConverter(localizedLocale, localizedIntegerPattern, true);

        // Perform Tests
        convertValueNoPattern(converter, localizedIntegerValue, expectedValue);
        convertValueWithPattern(converter, localizedIntegerValue, localizedIntegerPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);

    }

    /**
     * Test Converter(defaultValue) constructor
     */
    public void testConstructor_8() {

        // ------------- Construct using specified Locale --------
        converter = new BigIntegerLocaleConverter(defaultValue);

        // Perform Tests
        convertValueNoPattern(converter, defaultIntegerValue, expectedValue);
        convertValueWithPattern(converter, defaultIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, defaultValue);
        convertNull(converter, defaultValue);

    }

    /**
     * Test Converter(defaultValue, locPattern) constructor
     */
    public void testConstructor_9() {

        // ------------- Construct using specified Locale --------
        converter = new BigIntegerLocaleConverter(defaultValue, true);

        // Perform Tests
        convertValueNoPattern(converter, defaultIntegerValue, expectedValue);
        convertValueWithPattern(converter, defaultIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, defaultValue);
        convertNull(converter, defaultValue);

    }

    /**
     * Tries to convert to an unsupported type. This tests behavior of the base
     * class. All locale converters should react in the same way.
     */
    public void testUnsupportedType() {
        converter = new BigIntegerLocaleConverter();
        try {
            converter.convert(getClass(), "test", null);
            fail("Unsupported type not detected!");
        } catch (final ConversionException cex) {
            // expected result
        }
    }

}

