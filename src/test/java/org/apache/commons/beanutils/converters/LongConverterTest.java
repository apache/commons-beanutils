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

package org.apache.commons.beanutils.converters;

import java.math.BigInteger;
import java.util.Locale;

import org.apache.commons.beanutils.Converter;

/**
 * Test Case for the LongConverter class.
 *
 */

public class LongConverterTest extends NumberConverterTest {
    private Converter converter;

    public LongConverterTest(final String name) {
        super(name);
    }

    @Override
    protected Class<?> getExpectedType() {
        return Long.class;
    }

    @Override
    protected NumberConverter makeConverter() {
        return new LongConverter();
    }

    @Override
    protected NumberConverter makeConverter(final Object defaultValue) {
        return new LongConverter(defaultValue);
    }

    @Override
    public void setUp() throws Exception {
        converter = makeConverter();
        numbers[0] = Long.valueOf("-12");
        numbers[1] = Long.valueOf("13");
        numbers[2] = Long.valueOf("-22");
        numbers[3] = Long.valueOf("23");
    }

    @Override
    public void tearDown() throws Exception {
        converter = null;
    }

    /**
     * Test Invalid Amounts (too big/small)
     */
    public void testInvalidAmount() {
        final Converter converter = makeConverter();
        final Class<?> clazz = Long.class;

        // Boundaries still convert
        assertEquals("Minimum", Long.valueOf(Long.MIN_VALUE), converter.convert(clazz, Long.valueOf(Long.MIN_VALUE)));
        assertEquals("Maximum", Long.valueOf(Long.MAX_VALUE), converter.convert(clazz, Long.valueOf(Long.MAX_VALUE)));

        // Out of range values must be rejected, not silently truncated/clamped
        final BigInteger tooSmall = BigInteger.valueOf(Long.MIN_VALUE).multiply(BigInteger.TEN);
        final BigInteger tooBig   = BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.TEN);

        // Too Small
        try {
            converter.convert(clazz, tooSmall);
            fail("Less than minimum, expected ConversionException");
        } catch (final Exception e) {
            // expected result
        }

        // Too Large
        try {
            converter.convert(clazz, tooBig);
            fail("More than maximum, expected ConversionException");
        } catch (final Exception e) {
            // expected result
        }
    }

    /**
     * A locale-parsed String beyond long range comes back from DecimalFormat as a Double and must be
     * rejected rather than clamped to Long.MAX_VALUE.
     */
    public void testLocaleStringOutOfRange() {
        final NumberConverter converter = makeConverter();
        converter.setLocale(Locale.US);
        try {
            converter.convert(Long.class, "99999999999999999999");
            fail("More than maximum, expected ConversionException");
        } catch (final Exception e) {
            // expected result
        }
    }

    public void testSimpleConversion() throws Exception {
        final String[] message= {
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from Byte",
            "from Short",
            "from Integer",
            "from Long",
            "from Float",
            "from Double"
        };

        final Object[] input = {
            String.valueOf(Long.MIN_VALUE),
            "-17",
            "-1",
            "0",
            "1",
            "17",
            String.valueOf(Long.MAX_VALUE),
            Byte.valueOf((byte)7),
            Short.valueOf((short)8),
            Integer.valueOf(9),
            Long.valueOf(10),
            Float.valueOf((float) 11.1),
            Double.valueOf(12.2)
        };

        final Long[] expected = {
            Long.valueOf(Long.MIN_VALUE),
            Long.valueOf(-17),
            Long.valueOf(-1),
            Long.valueOf(0),
            Long.valueOf(1),
            Long.valueOf(17),
            Long.valueOf(Long.MAX_VALUE),
            Long.valueOf(7),
            Long.valueOf(8),
            Long.valueOf(9),
            Long.valueOf(10),
            Long.valueOf(11),
            Long.valueOf(12)
        };

        for(int i=0;i<expected.length;i++) {
            assertEquals(message[i] + " to Long",expected[i],converter.convert(Long.class,input[i]));
            assertEquals(message[i] + " to long",expected[i],converter.convert(Long.TYPE,input[i]));
            assertEquals(message[i] + " to null type",expected[i],converter.convert(null,input[i]));
        }
    }

}

