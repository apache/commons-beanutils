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
 * Test Case for the DoubleConverter class.
 */
public class DoubleConverterTestCase extends AbstractNumberConverterTest<Double> {

    public static TestSuite suite() {
        return new TestSuite(DoubleConverterTestCase.class);
    }

    private Converter<Double> converter = null;

    public DoubleConverterTestCase(final String name) {
        super(name);
    }

    @Override
    protected Class<Double> getExpectedType() {
        return Double.class;
    }

    @Override
    protected DoubleConverter makeConverter() {
        return new DoubleConverter();
    }

    @Override
    protected DoubleConverter makeConverter(final Double defaultValue) {
        return new DoubleConverter(defaultValue);
    }

    @Override
    public void setUp() throws Exception {
        converter = makeConverter();
        numbers[0] = Double.valueOf("-12");
        numbers[1] = Double.valueOf("13");
        numbers[2] = Double.valueOf("-22");
        numbers[3] = Double.valueOf("23");
    }

    @Override
    public void tearDown() throws Exception {
        converter = null;
    }

    public void testSimpleConversion() {
        final String[] message = { "from String", "from String", "from String", "from String", "from String", "from String", "from String", "from Byte",
                "from Short", "from Integer", "from Long", "from Float", "from Double" };

        final Object[] input = { String.valueOf(Double.MIN_VALUE), "-17.2", "-1.1", "0.0", "1.1", "17.2", String.valueOf(Double.MAX_VALUE),
                Byte.valueOf((byte) 7), Short.valueOf((short) 8), Integer.valueOf(9), Long.valueOf(10), Float.valueOf((float) 11.1), Double.valueOf(12.2) };

        final Double[] expected = { Double.valueOf(Double.MIN_VALUE), Double.valueOf(-17.2), Double.valueOf(-1.1), Double.valueOf(0.0), Double.valueOf(1.1),
                Double.valueOf(17.2), Double.valueOf(Double.MAX_VALUE), Double.valueOf(7), Double.valueOf(8), Double.valueOf(9), Double.valueOf(10),
                Double.valueOf(11.1), Double.valueOf(12.2) };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(message[i] + " to Double", expected[i].doubleValue(), converter.convert(Double.class, input[i]).doubleValue(), 0.00001D);
            assertEquals(message[i] + " to double", expected[i].doubleValue(), converter.convert(Double.TYPE, input[i]).doubleValue(), 0.00001D);
            assertEquals(message[i] + " to null type", expected[i].doubleValue(), converter.convert((Class<Double>) null, input[i]).doubleValue(), 0.00001D);
        }
    }

}
