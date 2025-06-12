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

package org.apache.commons.beanutils.locale.converters;

/**
 * Test Case for the IntegerLocaleConverter class.
 *
 */

public class IntegerLocaleConverterTestCase extends BaseLocaleConverterTestCase {

    public IntegerLocaleConverterTestCase(final String name) {
        super(name);
    }

    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {

        super.setUp();

        defaultValue  = Integer.valueOf("999");
        expectedValue = Integer.valueOf(expectedIntegerValue);

    }

    /**
     * Test Converter() constructor
     *
     * Uses the default locale, no default value
     *
     */
    public void testConstructor_2() {

        converter = new IntegerLocaleConverter();

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

        converter = new IntegerLocaleConverter(true);

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

        converter = new IntegerLocaleConverter(localizedLocale);

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

        converter = new IntegerLocaleConverter(localizedLocale, true);

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

        converter = new IntegerLocaleConverter(localizedLocale, defaultIntegerPattern);

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

        converter = new IntegerLocaleConverter(localizedLocale, localizedIntegerPattern, true);

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

        converter = new IntegerLocaleConverter(defaultValue);

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

        converter = new IntegerLocaleConverter(defaultValue, true);

        // Perform Tests
        convertValueNoPattern(converter, defaultIntegerValue, expectedValue);
        convertValueWithPattern(converter, defaultIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, defaultValue);
        convertNull(converter, defaultValue);

    }

    /**
     * Test Converter(defaultValue, locale, pattern, localizedPattern) constructor
     */
    public void testConstructorMain() {

        converter = new IntegerLocaleConverter(defaultValue,
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
        convertValueNoPattern(converter, "(B)", defaultIntegerValue, Integer.valueOf("1"));

        // **************************************************************************
        // Convert with non-localized pattern - unlike the equivalent BigDecimal Test Case
        // it doesn't causes an exception in parse() - DecimalFormat parses it
        // quite happily turning "1,234" into "1"
        // Again this is one of the limitations of DecimalFormat
        // **************************************************************************
        convertValueWithPattern(converter, "(B)", localizedIntegerValue, defaultIntegerPattern, Integer.valueOf("1"));

        // **************************************************************************
        // Convert with specified type
        //
        // BaseLocaleConverter completely ignores the type - so even if we specify
        // Double.class here it still returns a Integer.
        //  **** This has been changed due to BEANUTILS-449 ****
        // **************************************************************************
        //convertValueToType(converter, "(B)", Double.class, localizedIntegerValue, localizedIntegerPattern, expectedValue);

        converter = new IntegerLocaleConverter(defaultValue,
                                                  localizedLocale,
                                                  defaultIntegerPattern,
                                                  false);

        convertValueNoPattern(converter, "(C)", localizedIntegerValue, expectedValue);
        convertValueWithPattern(converter, "(C)", localizedIntegerValue, defaultIntegerPattern, expectedValue);
        convertInvalid(converter, "(C)", defaultValue);
        convertNull(converter, "(C)", defaultValue);

    }

    /**
     * Test Converting a number
     */
    public void testNumber() {

        converter = new IntegerLocaleConverter(localizedLocale);

        final Integer value = Integer.valueOf(1234);
        assertEquals("Convert Integer", value, converter.convert(value));
        assertEquals("Convert Long",    value, converter.convert(Long.valueOf(value.intValue())));
    }

    /**
     * Tests whether a conversion to a primitive type can be performed.
     */
    public void testToPrimitiveType() {
        converter = new IntegerLocaleConverter();
        final int value = 20131028;
        final Class<Integer> target = Integer.TYPE;
        final int result = converter.convert(target, (Object) Integer.toString(value));
        assertEquals("Wrong result", value, result);
    }
}

