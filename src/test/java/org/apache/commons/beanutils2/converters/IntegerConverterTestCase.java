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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.Converter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the IntegerConverter class.
 */
public class IntegerConverterTestCase extends AbstractNumberConverterTest<Integer> {

    private Converter<Integer> converter;

    @Override
    protected Class<Integer> getExpectedType() {
        return Integer.class;
    }

    @Override
    protected NumberConverter<Integer> makeConverter() {
        return new IntegerConverter();
    }

    @Override
    protected IntegerConverter makeConverter(final Integer defaultValue) {
        return new IntegerConverter(defaultValue);
    }

    @BeforeEach
    public void setUp() throws Exception {
        converter = makeConverter();
        numbers[0] = Integer.valueOf("-12");
        numbers[1] = Integer.valueOf("13");
        numbers[2] = Integer.valueOf("-22");
        numbers[3] = Integer.valueOf("23");
    }

    @AfterEach
    public void tearDown() throws Exception {
        converter = null;
    }

    /**
     * Test Invalid Amounts (too big/small)
     */
    @Test
    public void testInvalidAmount() {
        final Converter<Integer> converter = makeConverter();
        final Class<?> clazz = Integer.class;

        final Long min = Long.valueOf(Integer.MIN_VALUE);
        final Long max = Long.valueOf(Integer.MAX_VALUE);
        final Long minMinusOne = Long.valueOf(min.longValue() - 1);
        final Long maxPlusOne = Long.valueOf(max.longValue() + 1);

        // Minimum
        assertEquals(Integer.valueOf(Integer.MIN_VALUE), converter.convert(clazz, min), "Minimum");

        // Maximum
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), converter.convert(clazz, max), "Maximum");

        // Too Small
        assertThrows(ConversionException.class,
                     () -> converter.convert(clazz, minMinusOne),
                     "Less than minimum, expected ConversionException");

        // Too Large
        assertThrows(ConversionException.class,
                     () -> converter.convert(clazz, maxPlusOne),
                     "More than maximum, expected ConversionException");
    }

    /**
     * Tests whether an invalid default object causes an exception.
     */
    @Test
    @SuppressWarnings("unchecked") // raw to test throwing
    public void testInvalidDefaultObject() {
        @SuppressWarnings("rawtypes") // raw to test throwing
        final NumberConverter converter = makeConverter();
        assertThrows(ConversionException.class, () -> converter.setDefaultValue("notANumber"), "Invalid default value not detected!");
    }

    @Test
    public void testSimpleConversion() throws Exception {
        final String[] message = { "from String", "from String", "from String", "from String", "from String", "from String", "from String", "from Byte",
                "from Short", "from Integer", "from Long", "from Float", "from Double" };

        final Object[] input = { String.valueOf(Integer.MIN_VALUE), "-17", "-1", "0", "1", "17", String.valueOf(Integer.MAX_VALUE), Byte.valueOf((byte) 7),
                Short.valueOf((short) 8), Integer.valueOf(9), Long.valueOf(10), Float.valueOf((float) 11.1), Double.valueOf(12.2) };

        final Integer[] expected = { Integer.valueOf(Integer.MIN_VALUE), Integer.valueOf(-17), Integer.valueOf(-1), Integer.valueOf(0), Integer.valueOf(1),
                Integer.valueOf(17), Integer.valueOf(Integer.MAX_VALUE), Integer.valueOf(7), Integer.valueOf(8), Integer.valueOf(9), Integer.valueOf(10),
                Integer.valueOf(11), Integer.valueOf(12) };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], converter.convert(Integer.class, input[i]), message[i] + " to Integer");
            assertEquals(expected[i], converter.convert(Integer.TYPE, input[i]), message[i] + " to int");
            assertEquals(expected[i], converter.convert(null, input[i]), message[i] + " to null type");
        }
    }
}
