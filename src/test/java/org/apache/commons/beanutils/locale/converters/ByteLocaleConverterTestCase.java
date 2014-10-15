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

package org.apache.commons.beanutils.locale.converters;


/**
 * Test Case for the ByteLocaleConverter class.
 *
 * @version $Id$
 */

public class ByteLocaleConverterTestCase extends BaseLocaleConverterTestCase {

    // ---------------------------------------------------------- Constructors

    public ByteLocaleConverterTestCase(final String name) {
        super(name);
    }

    // -------------------------------------------------- Overall Test Methods

    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {

        super.setUp();

        defaultIntegerPattern   = "#,###";
        defaultIntegerValue     = ",123";

        localizedIntegerPattern = "#.###";
        localizedIntegerValue   = ".123";

        // Expected Values
        expectedDecimalValue    = "123.56";
        expectedIntegerValue    = "123";


        defaultValue  = new Byte("99");
        expectedValue = new Byte(expectedIntegerValue);

    }

    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {
        super.tearDown();
    }


    // ------------------------------------------------------------------------

    /**
     * Test Converter(defaultValue, locale, pattern, localizedPattern) constructor
     */
    public void testConstructorMain() {

        // ------------- Construct with localized pattern ------------
        converter = new ByteLocaleConverter(defaultValue,
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
        // quite happily turning ",123" into "0"
        // I guess this is one of the limitations of DecimalFormat
        // **************************************************************************
        convertValueNoPattern(converter, "(B)", defaultIntegerValue, new Byte("0"));


        // **************************************************************************
        // Convert with non-localized pattern
        // **************************************************************************
        convertValueWithPattern(converter, "(B)", "123", defaultIntegerPattern, new Byte("123"));
        convertValueWithPattern(converter, "(B-2)", localizedIntegerValue, defaultIntegerPattern, defaultValue);


        // **************************************************************************
        // Convert with specified type
        //
        // BaseLocaleConverter completely ignores the type - so even if we specify
        // Double.class here it still returns a Byte.
        //  **** This has been changed due to BEANUTILS-449 ****
        // **************************************************************************
        //convertValueToType(converter, "(B)", Double.class, localizedIntegerValue, localizedIntegerPattern, expectedValue);


        // ------------- Construct with non-localized pattern ------------
        converter = new ByteLocaleConverter(defaultValue,
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
        converter = new ByteLocaleConverter();

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
        converter = new ByteLocaleConverter(true);

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
        converter = new ByteLocaleConverter(localizedLocale);

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
        converter = new ByteLocaleConverter(localizedLocale, true);

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
        converter = new ByteLocaleConverter(localizedLocale, defaultIntegerPattern);

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
        converter = new ByteLocaleConverter(localizedLocale, localizedIntegerPattern, true);

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
        converter = new ByteLocaleConverter(defaultValue);

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
        converter = new ByteLocaleConverter(defaultValue, true);

        // Perform Tests
        convertValueNoPattern(converter, defaultIntegerValue, expectedValue);
        convertValueWithPattern(converter, defaultIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, defaultValue);
        convertNull(converter, defaultValue);

    }



}

