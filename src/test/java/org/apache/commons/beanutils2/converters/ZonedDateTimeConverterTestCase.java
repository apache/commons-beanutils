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

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

import junit.framework.TestSuite;

/**
 * Test Case for the ZonedDateTimeConverter class.
 */
public class ZonedDateTimeConverterTestCase extends AbstractDateConverterTest<ZonedDateTime> {

    /**
     * Create Test Suite
     *
     * @return test suite
     */
    public static TestSuite suite() {
        return new TestSuite(ZonedDateTimeConverterTestCase.class);
    }

    /**
     * Constructs a new Date test case.
     *
     * @param name Test Name
     */
    public ZonedDateTimeConverterTestCase(final String name) {
        super(name);
    }

    /**
     * Gets the expected type
     *
     * @return The expected type
     */
    @Override
    protected Class<ZonedDateTime> getExpectedType() {
        return ZonedDateTime.class;
    }

    /**
     * Create the Converter with no default value.
     *
     * @return A new Converter
     */
    @Override
    protected ZonedDateTimeConverter makeConverter() {
        return new ZonedDateTimeConverter();
    }

    /**
     * Create the Converter with a default value.
     *
     * @param defaultValue The default value
     * @return A new Converter
     */
    @Override
    protected ZonedDateTimeConverter makeConverter(final ZonedDateTime defaultValue) {
        return new ZonedDateTimeConverter(defaultValue);
    }

    /**
     * Convert from a Calendar to the appropriate Date type
     *
     * @param value The Calendar value to convert
     * @return The converted value
     */
    @Override
    protected ZonedDateTime toType(final Calendar value) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(value.getTimeInMillis()), ZoneId.systemDefault());
    }
}