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

import java.math.BigDecimal;

import org.apache.commons.beanutils2.Converter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the DoubleConverter class.
 */
public class BigDecimalConverterTestCase extends AbstractNumberConverterTest<BigDecimal> {

    /**
     * A class derived from {@code BigDecimal} used for testing whether derived number classes are handled correctly.
     */
    private static class ExtendingBigDecimal extends BigDecimal {
        private static final long serialVersionUID = 1L;

        private ExtendingBigDecimal(final String val) {
            super(val);
        }
    }

    private Converter<BigDecimal> converter;

    @Override
    protected Class<BigDecimal> getExpectedType() {
        return BigDecimal.class;
    }

    @Override
    protected BigDecimalConverter makeConverter() {
        return new BigDecimalConverter();
    }

    @Override
    protected BigDecimalConverter makeConverter(final BigDecimal defaultValue) {
        return new BigDecimalConverter(defaultValue);
    }

    @BeforeEach
    public void setUp() throws Exception {
        converter = makeConverter();
        numbers[0] = new BigDecimal("-12");
        numbers[1] = new BigDecimal("13");
        numbers[2] = new BigDecimal("-22");
        numbers[3] = new BigDecimal("23");
    }

    @AfterEach
    public void tearDown() throws Exception {
        converter = null;
    }

    @Test
    public void testSimpleConversion() throws Exception {
        final String[] message = { "from String", "from String", "from String", "from String", "from String", "from Byte", "from Short", "from Integer",
                "from Long", "from Float", "from Double", "from BigDecimal", "from BigDecimal extension" };

        final Object[] input = { "-17.2", "-1.1", "0.0", "1.1", "17.2", Byte.valueOf((byte) 7), Short.valueOf((short) 8), Integer.valueOf(9), Long.valueOf(10),
                Float.valueOf("11.1"), Double.valueOf("12.2"), new BigDecimal("3200.11"), new ExtendingBigDecimal("3200.11") };

        final BigDecimal[] expected = { new BigDecimal("-17.2"), new BigDecimal("-1.1"), new BigDecimal("0.0"), new BigDecimal("1.1"), new BigDecimal("17.2"),
                new BigDecimal("7"), new BigDecimal("8"), new BigDecimal("9"), new BigDecimal("10"), new BigDecimal("11.1"), new BigDecimal("12.2"),
                new BigDecimal("3200.11"), new BigDecimal("3200.11") };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], converter.convert(BigDecimal.class, input[i]), message[i] + " to BigDecimal");
            assertEquals(expected[i], converter.convert(null, input[i]), message[i] + " to null type");
        }
    }
}
