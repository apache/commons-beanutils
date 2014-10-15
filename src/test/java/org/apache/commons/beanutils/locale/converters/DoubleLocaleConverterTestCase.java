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
 * Test Case for the DoubleLocaleConverter class.
 *
 * @version $Id$
 */

public class DoubleLocaleConverterTestCase extends BaseLocaleConverterTestCase {



    // ---------------------------------------------------------- Constructors

    public DoubleLocaleConverterTestCase(final String name) {
        super(name);
    }

    // -------------------------------------------------- Overall Test Methods

    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {

        super.setUp();

        defaultValue  = new Double("9.99");
        expectedValue = new Double(expectedDecimalValue);

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
        converter = new DoubleLocaleConverter(defaultValue,
                                                  localizedLocale,
                                                  localizedDecimalPattern,
                                                  true);


        convertValueNoPattern(converter, "(A)", localizedDecimalValue, expectedValue);
        convertValueWithPattern(converter, "(A)", localizedDecimalValue, localizedDecimalPattern, expectedValue);
        convertInvalid(converter, "(A)", defaultValue);
        convertNull(converter, "(A)", defaultValue);


        // **************************************************************************
        // Convert value in the wrong format - maybe you would expect it to throw an
        // exception and return the default - it doesn't, DecimalFormat parses it
        // quite happily turning "1,234.56" into "1.234"
        // I guess this is one of the limitations of DecimalFormat
        // **************************************************************************
        convertValueNoPattern(converter, "(B)", defaultDecimalValue, new Double("1.234"));


        // **************************************************************************
        // Convert with non-localized pattern - this causes an exception in parse()
        // but it gets swallowed in convert() method and returns default.
        //  **** IS THIS THE EXPECTED BEHAVIOUR? ****
        // Maybe if the pattern is no good, we should use a default pattern rather
        // than just returning the default value.
        // **************************************************************************
        convertValueWithPattern(converter, "(B)", localizedDecimalValue, defaultDecimalPattern, defaultValue);


        // **************************************************************************
        // Convert with specified type
        //
        // BaseLocaleConverter completely ignores the type - so even if we specify
        // Double.class here it still returns a Double.
        //  **** This has been changed due to BEANUTILS-449 ****
        // **************************************************************************
        //convertValueToType(converter, "(B)", Integer.class, localizedDecimalValue, localizedDecimalPattern, expectedValue);


        // ------------- Construct with non-localized pattern ------------
        converter = new DoubleLocaleConverter(defaultValue,
                                                  localizedLocale,
                                                  defaultDecimalPattern,
                                                  false);


        convertValueNoPattern(converter, "(C)", localizedDecimalValue, expectedValue);
        convertValueWithPattern(converter, "(C)", localizedDecimalValue, defaultDecimalPattern, expectedValue);
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
        converter = new DoubleLocaleConverter();

        // Perform Tests
        convertValueNoPattern(converter, defaultDecimalValue, expectedValue);
        convertValueWithPattern(converter, defaultDecimalValue, defaultDecimalPattern, expectedValue);
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
        converter = new DoubleLocaleConverter(true);

        // Perform Tests
        convertValueNoPattern(converter, defaultDecimalValue, expectedValue);
        convertValueWithPattern(converter, defaultDecimalValue, defaultDecimalPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);


    }

    /**
     * Test Converter(Locale) constructor
     */
    public void testConstructor_4() {

        // ------------- Construct using specified Locale --------
        converter = new DoubleLocaleConverter(localizedLocale);

        // Perform Tests
        convertValueNoPattern(converter, localizedDecimalValue, expectedValue);
        convertValueWithPattern(converter, localizedDecimalValue, defaultDecimalPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);


    }


    /**
     * Test Converter(Locale, locPattern) constructor
     */
    public void testConstructor_5() {

        // ------------- Construct using specified Locale --------
        converter = new DoubleLocaleConverter(localizedLocale, true);

        // Perform Tests
        convertValueNoPattern(converter, localizedDecimalValue, expectedValue);
        convertValueWithPattern(converter, localizedDecimalValue, localizedDecimalPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);


    }

    /**
     * Test Converter(Locale, pattern) constructor
     */
    public void testConstructor_6() {

        // ------------- Construct using specified Locale --------
        converter = new DoubleLocaleConverter(localizedLocale, defaultDecimalPattern);

        // Perform Tests
        convertValueNoPattern(converter, localizedDecimalValue, expectedValue);
        convertValueWithPattern(converter, localizedDecimalValue, defaultDecimalPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);

    }

    /**
     * Test Converter(Locale, pattern, locPattern) constructor
     */
    public void testConstructor_7() {

        // ------------- Construct using specified Locale --------
        converter = new DoubleLocaleConverter(localizedLocale, localizedDecimalPattern, true);

        // Perform Tests
        convertValueNoPattern(converter, localizedDecimalValue, expectedValue);
        convertValueWithPattern(converter, localizedDecimalValue, localizedDecimalPattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);

    }

    /**
     * Test Converter(defaultValue) constructor
     */
    public void testConstructor_8() {

        // ------------- Construct using specified Locale --------
        converter = new DoubleLocaleConverter(defaultValue);

        // Perform Tests
        convertValueNoPattern(converter, defaultDecimalValue, expectedValue);
        convertValueWithPattern(converter, defaultDecimalValue, defaultDecimalPattern, expectedValue);
        convertInvalid(converter, defaultValue);
        convertNull(converter, defaultValue);

    }

    /**
     * Test Converter(defaultValue, locPattern) constructor
     */
    public void testConstructor_9() {

        // ------------- Construct using specified Locale --------
        converter = new DoubleLocaleConverter(defaultValue, true);

        // Perform Tests
        convertValueNoPattern(converter, defaultDecimalValue, expectedValue);
        convertValueWithPattern(converter, defaultDecimalValue, defaultDecimalPattern, expectedValue);
        convertInvalid(converter, defaultValue);
        convertNull(converter, defaultValue);

    }



}

