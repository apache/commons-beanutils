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

import org.apache.commons.beanutils2.Converter;

import junit.framework.TestSuite;

/**
 * Test Case for the ByteConverter class.
 */
public class ByteConverterTestCase extends AbstractNumberConverterTest<Byte> {

    public static TestSuite suite() {
        return new TestSuite(ByteConverterTestCase.class);
    }

    private Converter<Byte> converter = null;

    public ByteConverterTestCase(final String name) {
        super(name);
    }

    @Override
    protected Class<Byte> getExpectedType() {
        return Byte.class;
    }

    @Override
    protected ByteConverter makeConverter() {
        return new ByteConverter();
    }

    @Override
    protected ByteConverter makeConverter(final Byte defaultValue) {
        return new ByteConverter(defaultValue);
    }

    @Override
    public void setUp() throws Exception {
        converter = makeConverter();
        numbers[0] = Byte.valueOf("-12");
        numbers[1] = Byte.valueOf("13");
        numbers[2] = Byte.valueOf("-22");
        numbers[3] = Byte.valueOf("23");
    }

    @Override
    public void tearDown() throws Exception {
        converter = null;
    }

    /**
     * Test Invalid Amounts (too big/small)
     */
    public void testInvalidAmount() {
        final Converter<Byte> converter = makeConverter();
        final Class<Byte> clazz = Byte.class;

        final Long min = Long.valueOf(Byte.MIN_VALUE);
        final Long max = Long.valueOf(Byte.MAX_VALUE);
        final Long minMinusOne = Long.valueOf(min.longValue() - 1);
        final Long maxPlusOne = Long.valueOf(max.longValue() + 1);

        // Minimum
        assertEquals("Minimum", Byte.valueOf(Byte.MIN_VALUE), converter.convert(clazz, min));

        // Maximum
        assertEquals("Maximum", Byte.valueOf(Byte.MAX_VALUE), converter.convert(clazz, max));

        // Too Small
        try {
            assertEquals("Minimum - 1", null, converter.convert(clazz, minMinusOne));
            fail("Less than minimum, expected ConversionException");
        } catch (final Exception e) {
            // expected result
        }

        // Too Large
        try {
            assertEquals("Maximum + 1", null, converter.convert(clazz, maxPlusOne));
            fail("More than maximum, expected ConversionException");
        } catch (final Exception e) {
            // expected result
        }
    }

    public void testSimpleConversion() throws Exception {
        final String[] message = { "from String", "from String", "from String", "from String", "from String", "from String", "from String", "from Byte",
                "from Short", "from Integer", "from Long", "from Float", "from Double" };

        final Object[] input = { String.valueOf(Byte.MIN_VALUE), "-17", "-1", "0", "1", "17", String.valueOf(Byte.MAX_VALUE), Byte.valueOf((byte) 7),
                Short.valueOf((short) 8), Integer.valueOf(9), Long.valueOf(10), Float.valueOf((float) 11.1), Double.valueOf(12.2) };

        final Byte[] expected = { Byte.valueOf(Byte.MIN_VALUE), Byte.valueOf((byte) -17), Byte.valueOf((byte) -1), Byte.valueOf((byte) 0),
                Byte.valueOf((byte) 1), Byte.valueOf((byte) 17), Byte.valueOf(Byte.MAX_VALUE), Byte.valueOf((byte) 7), Byte.valueOf((byte) 8),
                Byte.valueOf((byte) 9), Byte.valueOf((byte) 10), Byte.valueOf((byte) 11), Byte.valueOf((byte) 12) };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(message[i] + " to Byte", expected[i], converter.convert(Byte.class, input[i]));
            assertEquals(message[i] + " to byte", expected[i], converter.convert(Byte.TYPE, input[i]));
            assertEquals(message[i] + " to null type", expected[i], converter.convert(null, input[i]));
        }
    }

}
