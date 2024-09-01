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

package org.apache.commons.beanutils2.sql.converters;

import java.sql.Date;
import java.util.Calendar;

import org.apache.commons.beanutils2.converters.AbstractDateConverterTest;
import org.apache.commons.beanutils2.converters.DateTimeConverter;

/**
 * Test Case for the {@link SqlDateConverter} class.
 */
public class SqlDateConverterTestCase extends AbstractDateConverterTest<Date> {

    /**
     * Gets the expected type
     *
     * @return The expected type
     */
    @Override
    protected Class<Date> getExpectedType() {
        return Date.class;
    }

    /**
     * Create the Converter with no default value.
     *
     * @return A new Converter
     */
    @Override
    protected SqlDateConverter makeConverter() {
        return new SqlDateConverter();
    }

    /**
     * Create the Converter with a default value.
     *
     * @param defaultValue The default value
     * @return A new Converter
     */
    @Override
    protected SqlDateConverter makeConverter(final Date defaultValue) {
        return new SqlDateConverter(defaultValue);
    }

    /**
     * Test default String to java.sql.Date conversion
     */
    @Override
    public void testDefaultStringToTypeConvert() {

        // Create & Configure the Converter
        final SqlDateConverter converter = makeConverter();
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
        final DateTimeConverter<Date> converter = makeConverter();
        converter.setUseLocaleFormat(false);

        // Valid String --> java.sql.Date Conversion
        final String expected = "2006-05-16";
        final Object testVal = toType(expected, "yyyy-MM-dd", null);
        stringConversion(converter, expected, testVal);

        final Object result = converter.convert(String.class, Integer.valueOf(2));
        assertEquals("Default toString()", "2", result);
    }

    /**
     * Convert from a Calendar to the appropriate Date type
     *
     * @param value The Calendar value to convert
     * @return The converted value
     */
    @Override
    protected Date toType(final Calendar value) {
        return new Date(getTimeInMillis(value));
    }

}
