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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.MonthDay;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.Converter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the MonthDayConverter class.
 */
class MonthDayConverterTest {

    private Converter<MonthDay> converter;

    protected Class<?> getExpectedType() {
        return MonthDay.class;
    }

    protected Converter<MonthDay> makeConverter() {
        return new MonthDayConverter();
    }

    @BeforeEach
    public void setUp() throws Exception {
        converter = makeConverter();
    }

    @AfterEach
    public void tearDown() throws Exception {
        converter = null;
    }

    @Test
    void testSimpleConversion() throws Exception {
        final String[] message = { "from String", "from String", "from String", "from String", "from String", "from String", "from String", "from String", };

        final Object[] input = { "--12-03", "--05-30", "--01-01" };

        final MonthDay[] expected = { MonthDay.parse("--12-03"), MonthDay.parse("--05-30"), MonthDay.parse("--01-01") };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], converter.convert(MonthDay.class, input[i]), message[i] + " to URI");
            assertEquals(expected[i], converter.convert(null, input[i]), message[i] + " to null type");
        }

        for (int i = 0; i < expected.length; i++) {
            assertEquals(input[i], converter.convert(String.class, expected[i]), input[i] + " to String");
        }
    }

    /**
     * Tests a conversion to an unsupported type.
     */
    @Test
    void testUnsupportedType() {
        assertThrows(ConversionException.class, () -> converter.convert(Integer.class, "http://www.apache.org"));
    }
}
