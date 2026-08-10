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

package org.apache.commons.beanutils2.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;

/**
 * Test Case for the DateConverter class.
 */
class DateConverterTest extends AbstractDateConverterTest<Date> {

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
    protected DateConverter makeConverter() {
        return new DateConverter();
    }

    /**
     * Create the Converter with a default value.
     *
     * @param defaultValue The default value
     * @return A new Converter
     */
    @Override
    protected DateConverter makeConverter(final Date defaultValue) {
        return new DateConverter(defaultValue);
    }

    /**
     * Convert from a Calendar to the appropriate Date type
     *
     * @param value The Calendar value to convert
     * @return The converted value
     */
    @Override
    protected Date toType(final Calendar value) {
        return value.getTime();
    }

    /**
     * A pre-epoch {@link Timestamp} carries a non-negative sub-second part in {@code getNanos()}, so decomposing
     * {@code getTime()} into whole seconds must floor: integer division truncates toward zero for negative values and
     * gains a whole second.
     */
    @Test
    void testConvertPreEpochSqlTimestamp() {
        // 1969-12-31T23:59:59.500Z: getTime() == -500, getNanos() == 500_000_000
        final Timestamp timestamp = new Timestamp(-500L);
        assertEquals(-500L, makeConverter().convert(getExpectedType(), timestamp).getTime());
    }

    /**
     * For {@code getTime()} in {@code [Long.MIN_VALUE, Long.MIN_VALUE + 807]} the whole-second term
     * {@code Math.floorDiv(getTime(), 1000) * 1000} wraps around {@link Long#MIN_VALUE}, but adding the non-negative
     * {@code getNanos() / 1_000_000} wraps it back: the two terms reconstruct {@code getTime()} exactly in
     * two's-complement arithmetic, so no overflow guard is needed.
     */
    @Test
    void testConvertExtremePreEpochSqlTimestamp() {
        assertEquals(Long.MIN_VALUE, makeConverter().convert(getExpectedType(), new Timestamp(Long.MIN_VALUE)).getTime());
        // last value whose whole-second term still wraps
        assertEquals(Long.MIN_VALUE + 807, makeConverter().convert(getExpectedType(), new Timestamp(Long.MIN_VALUE + 807)).getTime());
    }
}
