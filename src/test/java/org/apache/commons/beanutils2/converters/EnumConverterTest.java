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

import java.time.DayOfWeek;
import java.util.concurrent.TimeUnit;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.Converter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the EnumConverter class.
 */
class EnumConverterTest {

    public enum PizzaStatus {
        ORDERED, READY, DELIVERED;
    }

    private Converter<Enum<PizzaStatus>> converter;

    protected Class<?> getExpectedType() {
        return Enum.class;
    }

    protected Converter<Enum<PizzaStatus>> makeConverter() {
        return new EnumConverter<>();
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

        final Object[] input = { "DELIVERED", "ORDERED", "READY" };

        final PizzaStatus[] expected = { PizzaStatus.DELIVERED, PizzaStatus.ORDERED, PizzaStatus.READY };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], converter.convert(PizzaStatus.class, input[i]), message[i] + " to Enum");
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

    @Test
    void testConvertTimeUnit() {
        final TimeUnit expected = TimeUnit.NANOSECONDS;
        final Enum actual = converter.convert(Enum.class, "java.util.concurrent.TimeUnit.NANOSECONDS");
        assertEquals(expected, actual);
    }

    @Test
    void testConvertDayOfWeek() {
        final DayOfWeek expected = DayOfWeek.MONDAY;
        final DayOfWeek actual = converter.convert(DayOfWeek.class, "java.time.DayOfWeek#MONDAY");
        assertEquals(expected, actual);
    }

    @Test
    void testConvertMismatchingEnumType() {
        assertThrows(ConversionException.class, () -> converter.convert(TimeUnit.class, "java.time.DayOfWeek#MONDAY"));
    }

    @Test
    void testBrokenNamingConvention() {
        assertThrows(ConversionException.class, () -> converter.convert(Enum.class, "JAVA-TIME-DAYOFWEEK#MONDAY"));
    }

    @Test
    void testNonEnumClasses() {
        assertThrows(ConversionException.class, () -> converter.convert(Enum.class, "java.lang.String#MONDAY"));
    }

    @Test
    void testNonExistingClasses() {
        assertThrows(ConversionException.class, () -> converter.convert(Enum.class, "java.lang.does.not.exist#MONDAY"));
    }
}
