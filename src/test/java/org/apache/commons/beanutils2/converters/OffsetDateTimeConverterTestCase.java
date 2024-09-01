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
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Calendar;

/**
 * Test Case for the OffsetDateTimeConverter class.
 */
public class OffsetDateTimeConverterTestCase extends AbstractDateConverterTest<OffsetDateTime> {

    /**
     * Gets the expected type
     *
     * @return The expected type
     */
    @Override
    protected Class<OffsetDateTime> getExpectedType() {
        return OffsetDateTime.class;
    }

    /**
     * Create the Converter with no default value.
     *
     * @return A new Converter
     */
    @Override
    protected OffsetDateTimeConverter makeConverter() {
        return new OffsetDateTimeConverter();
    }

    /**
     * Create the Converter with a default value.
     *
     * @param defaultValue The default value
     * @return A new Converter
     */
    @Override
    protected OffsetDateTimeConverter makeConverter(final OffsetDateTime defaultValue) {
        return new OffsetDateTimeConverter(defaultValue);
    }

    /**
     * Convert from a Calendar to the appropriate Date type
     *
     * @param value The Calendar value to convert
     * @return The converted value
     */
    @Override
    protected OffsetDateTime toType(final Calendar value) {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(value.getTimeInMillis()), ZoneId.systemDefault());
    }
}