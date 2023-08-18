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
 * Test Case for the LongConverter class.
 */
public class LongConverterTestCase extends AbstractNumberConverterTest<Long> {

    public static TestSuite suite() {
        return new TestSuite(LongConverterTestCase.class);
    }

    private Converter<Long> converter = null;

    public LongConverterTestCase(final String name) {
        super(name);
    }

    @Override
    protected Class<Long> getExpectedType() {
        return Long.class;
    }

    @Override
    protected LongConverter makeConverter() {
        return new LongConverter();
    }

    @Override
    protected LongConverter makeConverter(final Long defaultValue) {
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

    public void testSimpleConversion() throws Exception {
        final String[] message = { "from String", "from String", "from String", "from String", "from String", "from String", "from String", "from Byte",
                "from Short", "from Integer", "from Long", "from Float", "from Double" };

        final Object[] input = { String.valueOf(Long.MIN_VALUE), "-17", "-1", "0", "1", "17", String.valueOf(Long.MAX_VALUE), Byte.valueOf((byte) 7),
                Short.valueOf((short) 8), Integer.valueOf(9), Long.valueOf(10), Float.valueOf((float) 11.1), Double.valueOf(12.2) };

        final Long[] expected = { Long.valueOf(Long.MIN_VALUE), Long.valueOf(-17), Long.valueOf(-1), Long.valueOf(0), Long.valueOf(1), Long.valueOf(17),
                Long.valueOf(Long.MAX_VALUE), Long.valueOf(7), Long.valueOf(8), Long.valueOf(9), Long.valueOf(10), Long.valueOf(11), Long.valueOf(12) };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(message[i] + " to Long", expected[i], converter.convert(Long.class, input[i]));
            assertEquals(message[i] + " to long", expected[i], converter.convert(Long.TYPE, input[i]));
            assertEquals(message[i] + " to null type", expected[i], converter.convert(null, input[i]));
        }
    }

}
