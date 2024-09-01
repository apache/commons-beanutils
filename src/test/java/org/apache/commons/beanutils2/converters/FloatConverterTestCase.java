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
 * Test Case for the FloatConverter class.
 */
public class FloatConverterTestCase extends AbstractNumberConverterTest<Float> {

    private Converter<Float> converter;

    @Override
    protected Class<Float> getExpectedType() {
        return Float.class;
    }

    @Override
    protected FloatConverter makeConverter() {
        return new FloatConverter();
    }

    @Override
    protected FloatConverter makeConverter(final Float defaultValue) {
        return new FloatConverter(defaultValue);
    }

    @BeforeEach
    public void setUp() throws Exception {
        converter = makeConverter();
        numbers[0] = Float.valueOf("-12");
        numbers[1] = Float.valueOf("13");
        numbers[2] = Float.valueOf("-22");
        numbers[3] = Float.valueOf("23");
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
        final Converter<Float> converter = makeConverter();
        final Class<?> clazz = Float.class;

        final Double max = Double.valueOf(Float.MAX_VALUE);
        final Double tooBig = Double.valueOf(Double.MAX_VALUE);

        // Maximum
        assertEquals(Float.valueOf(Float.MAX_VALUE), converter.convert(clazz, max), "Maximum");

        // Too Large
        assertThrows(ConversionException.class,
                     () -> converter.convert(clazz, tooBig),
                     "More than maximum, expected ConversionException");
    }

    @Test
    public void testSimpleConversion() {
        final String[] message = { "from String", "from String", "from String", "from String", "from String", "from String", "from String", "from Byte",
                "from Short", "from Integer", "from Long", "from Float", "from Double" };

        final Object[] input = { String.valueOf(Float.MIN_VALUE), "-17.2", "-1.1", "0.0", "1.1", "17.2", String.valueOf(Float.MAX_VALUE),
                Byte.valueOf((byte) 7), Short.valueOf((short) 8), Integer.valueOf(9), Long.valueOf(10), Float.valueOf((float) 11.1), Double.valueOf(12.2), };

        final Float[] expected = { Float.valueOf(Float.MIN_VALUE), Float.valueOf((float) -17.2), Float.valueOf((float) -1.1), Float.valueOf((float) 0.0),
                Float.valueOf((float) 1.1), Float.valueOf((float) 17.2), Float.valueOf(Float.MAX_VALUE), Float.valueOf(7), Float.valueOf(8), Float.valueOf(9),
                Float.valueOf(10), Float.valueOf((float) 11.1), Float.valueOf((float) 12.2) };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].floatValue(), converter.convert(Float.class, input[i]).floatValue(), 0.00001, message[i] + " to Float");
            assertEquals(expected[i].floatValue(), converter.convert(Float.TYPE, input[i]).floatValue(), 0.00001, message[i] + " to float");
            assertEquals(expected[i].floatValue(), converter.convert((Class<Float>) null, input[i]).floatValue(), 0.00001, message[i] + " to null type");
        }
    }
}
