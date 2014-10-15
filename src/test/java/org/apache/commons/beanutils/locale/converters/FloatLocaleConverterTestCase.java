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

import java.text.DecimalFormat;
import java.util.Locale;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.LocaleConvertUtils;

/**
 * Test Case for the FloatLocaleConverter class.
 *
 * @version $Id$
 */

public class FloatLocaleConverterTestCase extends BaseLocaleConverterTestCase {



    // ---------------------------------------------------------- Constructors

    public FloatLocaleConverterTestCase(final String name) {
        super(name);
    }

    // -------------------------------------------------- Overall Test Methods

    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {

        super.setUp();

        defaultValue  = new Float("9.99");
        expectedValue = new Float(expectedDecimalValue);

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
        converter = new FloatLocaleConverter(defaultValue,
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
        convertValueNoPattern(converter, "(B)", defaultDecimalValue, new Float("1.234"));


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
        // Float.class here it still returns a Float.
        //  **** This has been changed due to BEANUTILS-449 ****
        // **************************************************************************
        //convertValueToType(converter, "(B)", Integer.class, localizedDecimalValue, localizedDecimalPattern, expectedValue);


        // ------------- Construct with non-localized pattern ------------
        converter = new FloatLocaleConverter(defaultValue,
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
        converter = new FloatLocaleConverter();

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
        converter = new FloatLocaleConverter(true);

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
        converter = new FloatLocaleConverter(localizedLocale);

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
        converter = new FloatLocaleConverter(localizedLocale, true);

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
        converter = new FloatLocaleConverter(localizedLocale, defaultDecimalPattern);

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
        converter = new FloatLocaleConverter(localizedLocale, localizedDecimalPattern, true);

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
        converter = new FloatLocaleConverter(defaultValue);

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
        converter = new FloatLocaleConverter(defaultValue, true);

        // Perform Tests
        convertValueNoPattern(converter, defaultDecimalValue, expectedValue);
        convertValueWithPattern(converter, defaultDecimalValue, defaultDecimalPattern, expectedValue);
        convertInvalid(converter, defaultValue);
        convertNull(converter, defaultValue);

    }

    /**
     * Test Float limits
     */
    public void testFloatLimits() {

        converter = new FloatLocaleConverter(defaultLocale, defaultDecimalPattern);
        final DecimalFormat fmt = new DecimalFormat("#.#############################################################");

        assertEquals(new Float(-0.12), converter.convert("-0.12"));
        assertEquals("Positive Float.MAX_VALUE", new Float(Float.MAX_VALUE), converter.convert(fmt.format(Float.MAX_VALUE)));
        assertEquals("Positive Float.MIN_VALUE", new Float(Float.MIN_VALUE), converter.convert(fmt.format(Float.MIN_VALUE)));

        assertEquals("Negative Float.MAX_VALUE", new Float(Float.MAX_VALUE * -1), converter.convert(fmt.format(Float.MAX_VALUE * -1)));
        assertEquals("Negative Float.MIN_VALUE", new Float(Float.MIN_VALUE * -1), converter.convert(fmt.format(Float.MIN_VALUE * -1)));


        try {
            converter.convert(fmt.format((double)Float.MAX_VALUE * (double)10));
            fail("Positive Too Large should throw ConversionException");
        } catch (final ConversionException e) {
            // expected result
        }
        try {
            converter.convert(fmt.format((double)Float.MAX_VALUE * (double)-10));
            fail("Negative Too Large should throw ConversionException");
        } catch (final ConversionException e) {
            // expected result
        }

        try {
            converter.convert(fmt.format((double)Float.MIN_VALUE / (double)10));
            fail("Positive Too Small should throw ConversionException");
        } catch (final ConversionException e) {
            // expected result
        }
        try {
            converter.convert(fmt.format((double)Float.MIN_VALUE / (double)-10));
            fail("Negative Too Small should throw ConversionException");
        } catch (final ConversionException e) {
            // expected result
        }
    }

    /**
     * Test parsing zero - see BEANUTILS-351
     */
    public void testParseZero() {
        try {
            final Object result = LocaleConvertUtils.convert("0", Float.class, Locale.US, null);
            assertEquals(new Float(0), result);
        } catch (final ConversionException e) {
            fail("Zero threw ConversionException: " + e);
        }

    }


}

