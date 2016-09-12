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

package org.apache.commons.beanutils.converters;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestSuite;

/**
 * Test Case for the {@link SqlTimestampConverter} class.
 *
 * @version $Id$
 */

public class SqlTimestampConverterTestCase extends DateConverterTestBase {

    /**
     * Construct a new Date test case.
     * @param name Test Name
     */
    public SqlTimestampConverterTestCase(final String name) {
        super(name);
    }

    // ------------------------------------------------------------------------

    /**
     * Create Test Suite
     * @return test suite
     */
    public static TestSuite suite() {
        return new TestSuite(SqlTimestampConverterTestCase.class);
    }

    // ------------------------------------------------------------------------

    private boolean isUSFormatWithComma() {
        // BEANUTILS-495 workaround - sometimes Java 9 expects "," in date even if
        // the format is set to lenient
        DateFormat loc = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
        return loc.format(new Date()).contains(",");
    }

    /**
     * Test Date Converter with no default value
     */
    @Override
    public void testLocale() {

        // Re-set the default Locale to Locale.US
        final Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        isUSFormatWithComma();


        // Create & Configure the Converter
        final DateTimeConverter converter = makeConverter();
        converter.setUseLocaleFormat(true);

        String pattern; // SHORT style Date & Time format for US Locale
        String testString;
        if (isUSFormatWithComma()) {
            pattern = "M/d/yy, h:mm a";
            testString = "3/21/06, 3:06 PM";
        } else {
            // More regular pattern for Java 8 and earlier
            pattern = "M/d/yy h:mm a";
            testString = "3/21/06 3:06 PM";
        }


        // Valid String --> Type Conversion
        final Object expected = toType(testString, pattern, null);
        validConversion(converter, expected, testString);

        // Invalid Conversions
        invalidConversion(converter, null);
        invalidConversion(converter, "");
        invalidConversion(converter, "13:05 pm");
        invalidConversion(converter, "11:05 p");
        invalidConversion(converter, "11.05 pm");
        invalidConversion(converter, new Integer(2));

        // Restore the default Locale
        Locale.setDefault(defaultLocale);

    }

    /**
     * Test default String to java.sql.Timestamp conversion
     */
    @Override
    public void testDefaultStringToTypeConvert() {

        // Create & Configure the Converter
        final DateTimeConverter converter = makeConverter();
        converter.setUseLocaleFormat(false);

        // Valid String --> java.sql.Timestamp Conversion
        final String testString = "2006-10-23 15:36:01.0";
        final Object expected = toType(testString, "yyyy-MM-dd HH:mm:ss.S", null);
        validConversion(converter, expected, testString);

        // Invalid String --> java.sql.Timestamp Conversion
        invalidConversion(converter, "2006/09/21 15:36:01.0");
        invalidConversion(converter, "2006-10-22");
        invalidConversion(converter, "15:36:01");

    }

    /**
     * Create the Converter with no default value.
     * @return A new Converter
     */
    @Override
    protected DateTimeConverter makeConverter() {
        return new SqlTimestampConverter();
    }

    /**
     * Create the Converter with a default value.
     * @param defaultValue The default value
     * @return A new Converter
     */
    @Override
    protected DateTimeConverter makeConverter(final Object defaultValue) {
        return new SqlTimestampConverter(defaultValue);
    }

    /**
     * Return the expected type
     * @return The expected type
     */
    @Override
    protected Class<?> getExpectedType() {
        return Timestamp.class;
    }

    /**
     * Convert from a Calendar to the appropriate Date type
     *
     * @param value The Calendar value to convert
     * @return The converted value
     */
    @Override
    protected Object toType(final Calendar value) {
        return new Timestamp(getTimeInMillis(value));
    }

}
