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

import java.math.BigInteger;

import org.apache.commons.beanutils2.Converter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the BigInteger class.
 */
public class BigIntegerConverterTestCase extends AbstractNumberConverterTest<BigInteger> {

    private Converter<BigInteger> converter;

    @Override
    protected Class<BigInteger> getExpectedType() {
        return BigInteger.class;
    }

    @Override
    protected BigIntegerConverter makeConverter() {
        return new BigIntegerConverter();
    }

    @Override
    protected BigIntegerConverter makeConverter(final BigInteger defaultValue) {
        return new BigIntegerConverter(defaultValue);
    }

    @BeforeEach
    public void setUp() throws Exception {
        converter = makeConverter();
        numbers[0] = new BigInteger("-12");
        numbers[1] = new BigInteger("13");
        numbers[2] = new BigInteger("-22");
        numbers[3] = new BigInteger("23");
    }

    @AfterEach
    public void tearDown() throws Exception {
        converter = null;
    }

    @Test
    public void testSimpleConversion() throws Exception {
        final String[] message = { "from String", "from String", "from String", "from String", "from String", "from String", "from String", "from Byte",
                "from Short", "from Integer", "from Long", "from Float", "from Double" };

        final Object[] input = { String.valueOf(Long.MIN_VALUE), "-17", "-1", "0", "1", "17", String.valueOf(Long.MAX_VALUE), Byte.valueOf((byte) 7),
                Short.valueOf((short) 8), Integer.valueOf(9), Long.valueOf(10), Float.valueOf((float) 11.1), Double.valueOf(12.2) };

        final BigInteger[] expected = { BigInteger.valueOf(Long.MIN_VALUE), BigInteger.valueOf(-17), BigInteger.valueOf(-1), BigInteger.valueOf(0),
                BigInteger.valueOf(1), BigInteger.valueOf(17), BigInteger.valueOf(Long.MAX_VALUE), BigInteger.valueOf(7), BigInteger.valueOf(8),
                BigInteger.valueOf(9), BigInteger.valueOf(10), BigInteger.valueOf(11), BigInteger.valueOf(12) };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], converter.convert(BigInteger.class, input[i]), message[i] + " to BigInteger");
            assertEquals(expected[i], converter.convert(null, input[i]), message[i] + " to null type");
        }
    }

}
