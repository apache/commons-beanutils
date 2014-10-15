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

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test Case for the DateLocaleConverter class.
 *
 * @version $Id$
 */

public class DateLocaleConverterTestCase extends BaseLocaleConverterTestCase {

    /** All logging goes through this logger */
    private final Log log = LogFactory.getLog(DateLocaleConverterTestCase.class);

    protected String localizedDatePattern;
    protected String localizedDateValue;
    protected String localizedShortDateValue;
    protected String defaultDatePattern;
    protected String defaultDateValue;
    protected String defaultShortDateValue;

    protected boolean validLocalDateSymbols;

    // ------------------------------------------------------------------------

    public DateLocaleConverterTestCase(final String name) {
        super(name);
    }

    // -------------------------------------------------- Overall Test Methods

    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {

        super.setUp();

        final String version = System.getProperty("java.specification.version");
        log.debug("JDK Version "+version);


        try {
            final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            expectedValue      = format.parse("20041001");
            defaultValue       = format.parse("19670316");
        } catch (final Exception ex) {
            log.error("Error creating expected/default dates", ex);
        }

        // Default Locale (Use US)
        defaultLocale           = Locale.US;
        defaultDatePattern      = "d MMMM yyyy";
        defaultDateValue        = "1 October 2004";
        defaultShortDateValue   = "10/01/04";

        // Use German Locale
//        localizedLocale         = Locale.GERMAN;  // N.B. doesn't work for dates
//        localizedLocale         = Locale.GERMANY; // N.B. doesn't work for dates
        localizedLocale         = new Locale("de", "AT"); // Austria/German works
        localizedDatePattern    = "t MMMM uuuu";
        localizedDateValue      = "1 Oktober 2004";
        localizedShortDateValue = "01.10.04";

        // Test whether the "local pattern characters" are what we
        // are expecting - Locale.GERMAN and Locale.GERMANY, Locale.FRENCH all
        // returned the standard "English" pattern characters on my machine
        // for JDK 1.4 (JDK 1.3 was OK). The Austria/German locale was OK though
        final String expectedChars = "GuMtkHmsSEDFwWahKzZ";
        final DateFormatSymbols localizedSymbols = new DateFormatSymbols(localizedLocale);
        final String localChars    = localizedSymbols.getLocalPatternChars();

        // different JDK versions seem to have different numbers of pattern characters
        final int lth = localChars.length() > expectedChars.length() ? expectedChars.length() :
                     localChars.length() < expectedChars.length() ? localChars.length() : expectedChars.length();
        validLocalDateSymbols = expectedChars.substring(0, lth).equals(localChars.substring(0, lth));

    }

    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {
        super.tearDown();
    }


    // ------------------------------------------------------------------------

    public void testSetLenient() {
        // make sure that date format works as expected
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.UK);

        // test with no leniency
        dateFormat.setLenient(false);

        try {

            dateFormat.parse("Feb 10, 2001");

        } catch (final ParseException e) {
            fail("Could not parse date (1) - " + e.getMessage());
        }

        try {

            dateFormat.parse("Feb 31, 2001");
            fail("Parsed illegal date (1)");

        } catch (final ParseException e) {
            // that's what we expected
        }

        // test with leniency
        dateFormat.setLenient(true);

        try {

            dateFormat.parse("Feb 10, 2001");

        } catch (final ParseException e) {
            fail("Could not parse date (2) - " + e.getMessage());
        }

        try {

            dateFormat.parse("Feb 31, 2001");

        } catch (final ParseException e) {
            fail("Could not parse date (3) - " + e.getMessage());
        }

        // now repeat tests for converter
        final DateLocaleConverter converter = new DateLocaleConverter(Locale.UK, "MMM dd, yyyy");

        // test with no leniency
        converter.setLenient(false);
        assertEquals("Set lenient failed", converter.isLenient(), false);

        try {

            converter.convert("Feb 10, 2001");

        } catch (final ConversionException e) {
            fail("Could not parse date (4) - " + e.getMessage());
        }

        try {

            converter.convert("Feb 31, 2001");
            assertEquals("Set lenient failed", converter.isLenient(), false);
            fail("Parsed illegal date (2)");

        } catch (final ConversionException e) {
            // that's what we expected
        }

        // test with leniency
        converter.setLenient(true);
        assertEquals("Set lenient failed", converter.isLenient(), true);

        try {

            converter.convert("Feb 10, 2001");

        } catch (final ConversionException e) {
            fail("Could not parse date (5) - " + e.getMessage());
        }

        try {

            converter.convert("Feb 31, 2001");

        } catch (final ConversionException e) {
            fail("Could not parse date (6) - " + e.getMessage());
        }
    }

    /**
     * Test Converter(defaultValue, locale, pattern, localizedPattern) constructor
     */
    public void testConstructorMain() {

        // Skip this test if no valid symbols for the locale
        if (!validLocalDateSymbols) {
            log.error("Invalid locale symbols *** skipping testConstructorMain() **");
            return;
        }

        // ------------- Construct with localized pattern ------------
        converter = new DateLocaleConverter(defaultValue,
                                            localizedLocale,
                                            localizedDatePattern,
                                            true);


        convertValueNoPattern(converter, "(A)", localizedDateValue, expectedValue);
        convertValueWithPattern(converter, "(A)", localizedDateValue, localizedDatePattern, expectedValue);
        convertInvalid(converter, "(A)", defaultValue);
        convertNull(converter, "(A)", defaultValue);


        // Convert value in the wrong format - should return default value
        convertValueNoPattern(converter, "(B)", defaultDateValue, defaultValue);

        // Convert with non-localized pattern - should return default value
        convertValueWithPattern(converter, "(B)", localizedDateValue, defaultDatePattern, defaultValue);

        // **************************************************************************
        // Convert with specified type
        //
        // BaseLocaleConverter completely ignores the type - so even if we specify
        // Double.class here it still returns a Date.
        //  **** This has been changed due to BEANUTILS-449 ****
        // **************************************************************************
        //convertValueToType(converter, "(B)", String.class, localizedDateValue, localizedDatePattern, expectedValue);


        // ------------- Construct with non-localized pattern ------------
        converter = new DateLocaleConverter(defaultValue,
                                            localizedLocale,
                                            defaultDatePattern,
                                            false);


        convertValueNoPattern(converter, "(C)", localizedDateValue, expectedValue);
        convertValueWithPattern(converter, "(C)", localizedDateValue, defaultDatePattern, expectedValue);
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

        // ------------- Construct using default pattern & default locale ------------
        converter = new DateLocaleConverter();

        // Perform Tests
        convertValueNoPattern(converter, defaultShortDateValue, expectedValue);
        convertValueWithPattern(converter, defaultDateValue, defaultDatePattern, expectedValue);
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

        // ------------- Construct using default pattern & default locale --------
        converter = new DateLocaleConverter(true);

        // Perform Tests
        convertValueNoPattern(converter, defaultShortDateValue, expectedValue);
        convertValueWithPattern(converter, defaultDateValue, defaultDatePattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);


    }

    /**
     * Test Converter(Locale) constructor
     */
    public void testConstructor_4() {

        // ------------- Construct using specified Locale --------
        converter = new DateLocaleConverter(localizedLocale);

        // Perform Tests
        convertValueNoPattern(converter, localizedShortDateValue, expectedValue);
        convertValueWithPattern(converter, localizedDateValue, defaultDatePattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);


    }


    /**
     * Test Converter(Locale, locPattern) constructor
     */
    public void testConstructor_5() {

        // Skip this test if no valid symbols for the locale
        if (!validLocalDateSymbols) {
            log.error("Invalid locale symbols *** skipping testConstructor_5() **");
            return;
        }

        // ------------- Construct using specified Locale --------
        converter = new DateLocaleConverter(localizedLocale, true);

        // Perform Tests
        convertValueNoPattern(converter, localizedShortDateValue, expectedValue);
        convertValueWithPattern(converter, localizedDateValue, localizedDatePattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);


    }

    /**
     * Test Converter(Locale, pattern) constructor
     */
    public void testConstructor_6() {

        // ------------- Construct using specified Locale --------
        converter = new DateLocaleConverter(localizedLocale, defaultDatePattern);

        // Perform Tests
        convertValueNoPattern(converter, localizedDateValue, expectedValue);
        convertValueWithPattern(converter, localizedDateValue, defaultDatePattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);

    }

    /**
     * Test Converter(Locale, pattern, locPattern) constructor
     */
    public void testConstructor_7() {

        // Skip this test if no valid symbols for the locale
        if (!validLocalDateSymbols) {
            log.error("Invalid locale symbols *** skipping testConstructor_7() **");
            return;
        }

        // ------------- Construct using specified Locale --------
        converter = new DateLocaleConverter(localizedLocale, localizedDatePattern, true);

        // Perform Tests
        convertValueNoPattern(converter, localizedDateValue, expectedValue);
        convertValueWithPattern(converter, localizedDateValue, localizedDatePattern, expectedValue);
        convertInvalid(converter, null);
        convertNull(converter, null);

    }

    /**
     * Test Converter(defaultValue) constructor
     */
    public void testConstructor_8() {

        // ------------- Construct using specified Locale --------
        converter = new DateLocaleConverter(defaultValue);

        // Perform Tests
        convertValueNoPattern(converter, defaultShortDateValue, expectedValue);
        convertValueWithPattern(converter, defaultDateValue, defaultDatePattern, expectedValue);
        convertInvalid(converter, defaultValue);
        convertNull(converter, defaultValue);

    }

    /**
     * Test Converter(defaultValue, locPattern) constructor
     */
    public void testConstructor_9() {

        // ------------- Construct using specified Locale --------
        converter = new DateLocaleConverter(defaultValue, true);

        // Perform Tests
        convertValueNoPattern(converter, defaultShortDateValue, expectedValue);
        convertValueWithPattern(converter, defaultDateValue, defaultDatePattern, expectedValue);
        convertInvalid(converter, defaultValue);
        convertNull(converter, defaultValue);

    }

    /**
     * Test invalid date
     */
    public void testInvalidDate() {

        converter = new DateLocaleConverter(defaultLocale);

        try {
            converter.convert("01/10/2004", "dd-MM-yyyy");
        } catch (final ConversionException e) {
            assertEquals("Parse Error", "Error parsing date '01/10/2004' at position=2", e.getMessage());
        }

        try {
            converter.convert("01-10-2004X", "dd-MM-yyyy");
        } catch (final ConversionException e) {
            assertEquals("Parse Length", "Date '01-10-2004X' contains unparsed characters from position=10", e.getMessage());
        }

    }

    /**
     * Test java.util.Date
     */
    public void testDateObject() {
        converter = new DateLocaleConverter(defaultLocale);
        assertEquals("java.util.Date", expectedValue, converter.convert(expectedValue));
    }

    /**
     * Test Calendar
     */
    public void testCalendarObject() {
        converter = new DateLocaleConverter(defaultLocale);
        final java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime((java.util.Date)expectedValue);
        assertEquals("java.util.Calendar", expectedValue, converter.convert(calendar));
    }

}

