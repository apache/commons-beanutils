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

import java.sql.Date;
import java.util.Calendar;

import junit.framework.TestSuite;

/**
 * Test Case for the {@link SqlDateConverter} class.
 *
 * @version $Id$
 */

public class SqlDateConverterTestCase extends DateConverterTestBase {

    /**
     * Construct a new Date test case.
     * @param name Test Name
     */
    public SqlDateConverterTestCase(final String name) {
        super(name);
    }

    // ------------------------------------------------------------------------

    /**
     * Create Test Suite
     * @return test suite
     */
    public static TestSuite suite() {
        return new TestSuite(SqlDateConverterTestCase.class);
    }

    // ------------------------------------------------------------------------

    /**
     * Test default String to java.sql.Date conversion
     */
    @Override
    public void testDefaultStringToTypeConvert() {

        // Create & Configure the Converter
        final DateTimeConverter converter = makeConverter();
        converter.setUseLocaleFormat(false);

        // Valid String --> java.sql.Date Conversion
        final String testString = "2006-05-16";
        final Object expected = toType(testString, "yyyy-MM-dd", null);
        validConversion(converter, expected, testString);

        // Invalid String --> java.sql.Date Conversion
        invalidConversion(converter, "01/01/2006");
    }

    /**
     * Test default java.sql.Date to String conversion
     */
    public void testDefaultTypeToStringConvert() {

        // Create & Configure the Converter
        final DateTimeConverter converter = makeConverter();
        converter.setUseLocaleFormat(false);

        // Valid String --> java.sql.Date Conversion
        final String expected  = "2006-05-16";
        final Object testVal   = toType(expected, "yyyy-MM-dd", null);
        stringConversion(converter, expected, testVal);

        final Object result = converter.convert(String.class, new Integer(2));
        assertEquals("Default toString()", "2", result);
    }

    /**
     * Create the Converter with no default value.
     * @return A new Converter
     */
    @Override
    protected DateTimeConverter makeConverter() {
        return new SqlDateConverter();
    }

    /**
     * Create the Converter with a default value.
     * @param defaultValue The default value
     * @return A new Converter
     */
    @Override
    protected DateTimeConverter makeConverter(final Object defaultValue) {
        return new SqlDateConverter(defaultValue);
    }

    /**
     * Return the expected type
     * @return The expected type
     */
    @Override
    protected Class<?> getExpectedType() {
        return Date.class;
    }

    /**
     * Convert from a Calendar to the appropriate Date type
     *
     * @param value The Calendar value to convert
     * @return The converted value
     */
    @Override
    protected Object toType(final Calendar value) {
        return new java.sql.Date(getTimeInMillis(value));
    }

}
