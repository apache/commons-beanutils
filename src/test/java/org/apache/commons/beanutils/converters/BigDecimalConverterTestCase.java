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

package org.apache.commons.beanutils.converters;

import java.math.BigDecimal;

import junit.framework.TestSuite;
import org.apache.commons.beanutils.Converter;


/**
 * Test Case for the DoubleConverter class.
 *
 * @version $Id$
 */

public class BigDecimalConverterTestCase extends NumberConverterTestBase {

    private Converter converter = null;

    // ------------------------------------------------------------------------

    public BigDecimalConverterTestCase(final String name) {
        super(name);
    }

    // ------------------------------------------------------------------------

    @Override
    public void setUp() throws Exception {
        converter = makeConverter();
        numbers[0] = new BigDecimal("-12");
        numbers[1] = new BigDecimal("13");
        numbers[2] = new BigDecimal("-22");
        numbers[3] = new BigDecimal("23");
    }

    public static TestSuite suite() {
        return new TestSuite(BigDecimalConverterTestCase.class);
    }

    @Override
    public void tearDown() throws Exception {
        converter = null;
    }

    // ------------------------------------------------------------------------

    @Override
    protected NumberConverter makeConverter() {
        return new BigDecimalConverter();
    }

    @Override
    protected NumberConverter makeConverter(final Object defaultValue) {
        return new BigDecimalConverter(defaultValue);
    }

    @Override
    protected Class<?> getExpectedType() {
        return BigDecimal.class;
    }

    // ------------------------------------------------------------------------

    public void testSimpleConversion() throws Exception {
        final String[] message= {
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
            "from Double",
            "from BigDecimal",
            "from BigDecimal extension"
        };

        final Object[] input = {
            "-17.2",
            "-1.1",
            "0.0",
            "1.1",
            "17.2",
            new Byte((byte)7),
            new Short((short)8),
            new Integer(9),
            new Long(10),
            new Float("11.1"),
            new Double("12.2"),
            new BigDecimal("3200.11"),
            new ExtendingBigDecimal("3200.11")
        };

        final BigDecimal[] expected = {
            new BigDecimal("-17.2"),
            new BigDecimal("-1.1"),
            new BigDecimal("0.0"),
            new BigDecimal("1.1"),
            new BigDecimal("17.2"),
            new BigDecimal("7"),
            new BigDecimal("8"),
            new BigDecimal("9"),
            new BigDecimal("10"),
            new BigDecimal("11.1"),
            new BigDecimal("12.2"),
            new BigDecimal("3200.11"),
            new BigDecimal("3200.11")
        };

        for(int i=0;i<expected.length;i++) {
            assertEquals(
                message[i] + " to BigDecimal",
                expected[i],
                converter.convert(BigDecimal.class,input[i]));
            assertEquals(
                message[i] + " to null type",
                expected[i],
                converter.convert(null,input[i]));
        }
    }

    /**
     * A class derived from {@code BigDecimal} used for testing whether
     * derived number classes are handled correctly.
     */
    private class ExtendingBigDecimal extends BigDecimal {
        private ExtendingBigDecimal(final String val) {
            super(val);
        }
    }
}

