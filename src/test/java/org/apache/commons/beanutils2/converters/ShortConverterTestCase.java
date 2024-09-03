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
 * Test Case for the ShortConverter class.
 */
public class ShortConverterTestCase extends AbstractNumberConverterTest<Short> {

    private ShortConverter converter;

    @Override
    protected Class<Short> getExpectedType() {
        return Short.class;
    }

    @Override
    protected ShortConverter makeConverter() {
        return new ShortConverter();
    }

    @Override
    protected ShortConverter makeConverter(final Short defaultValue) {
        return new ShortConverter(defaultValue);
    }

    @BeforeEach
    public void setUp() throws Exception {
        converter = makeConverter();
        numbers[0] = Short.valueOf("-12");
        numbers[1] = Short.valueOf("13");
        numbers[2] = Short.valueOf("-22");
        numbers[3] = Short.valueOf("23");
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
        final Converter<Short> converter = makeConverter();
        final Class<?> clazz = Short.class;

        final Long min = Long.valueOf(Short.MIN_VALUE);
        final Long max = Long.valueOf(Short.MAX_VALUE);
        final Long minMinusOne = Long.valueOf(min.longValue() - 1);
        final Long maxPlusOne = Long.valueOf(max.longValue() + 1);

        // Minimum
        assertEquals(Short.valueOf(Short.MIN_VALUE), converter.convert(clazz, min), "Minimum");

        // Maximum
        assertEquals(Short.valueOf(Short.MAX_VALUE), converter.convert(clazz, max), "Maximum");

        // Too Small
        assertThrows(ConversionException.class,
                     () -> converter.convert(clazz, minMinusOne),
                     "Less than minimum, expected ConversionException");

        // Too Large
        assertThrows(ConversionException.class,
                     () -> converter.convert(clazz, maxPlusOne),
                     "More than maximum, expected ConversionException");
    }

    @Test
    public void testSimpleConversion() throws Exception {
        final String[] message = { "from String", "from String", "from String", "from String", "from String", "from String", "from String", "from Byte",
                "from Short", "from Integer", "from Long", "from Float", "from Double" };

        final Object[] input = { String.valueOf(Short.MIN_VALUE), "-17", "-1", "0", "1", "17", String.valueOf(Short.MAX_VALUE), Byte.valueOf((byte) 7),
                Short.valueOf((short) 8), Integer.valueOf(9), Long.valueOf(10), Float.valueOf((float) 11.1), Double.valueOf(12.2) };

        final Short[] expected = { Short.valueOf(Short.MIN_VALUE), Short.valueOf((short) -17), Short.valueOf((short) -1), Short.valueOf((short) 0),
                Short.valueOf((short) 1), Short.valueOf((short) 17), Short.valueOf(Short.MAX_VALUE), Short.valueOf((short) 7), Short.valueOf((short) 8),
                Short.valueOf((short) 9), Short.valueOf((short) 10), Short.valueOf((short) 11), Short.valueOf((short) 12) };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], converter.convert(Short.class, input[i]), message[i] + " to Short");
            assertEquals(expected[i], converter.convert(Short.TYPE, input[i]), message[i] + " to short");
            assertEquals(expected[i], converter.convert(null, input[i]), message[i] + " to null type");
        }
    }
}
