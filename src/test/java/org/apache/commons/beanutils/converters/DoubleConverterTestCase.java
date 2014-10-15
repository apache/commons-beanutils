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

import junit.framework.TestSuite;

import org.apache.commons.beanutils.Converter;


/**
 * Test Case for the DoubleConverter class.
 *
 * @version $Id$
 */

public class DoubleConverterTestCase extends NumberConverterTestBase {

    private Converter converter = null;

    // ------------------------------------------------------------------------

    public DoubleConverterTestCase(final String name) {
        super(name);
    }

    // ------------------------------------------------------------------------

    @Override
    public void setUp() throws Exception {
        converter = makeConverter();
        numbers[0] = new Double("-12");
        numbers[1] = new Double("13");
        numbers[2] = new Double("-22");
        numbers[3] = new Double("23");
    }

    public static TestSuite suite() {
        return new TestSuite(DoubleConverterTestCase.class);
    }

    @Override
    public void tearDown() throws Exception {
        converter = null;
    }

    // ------------------------------------------------------------------------

    @Override
    protected NumberConverter makeConverter() {
        return new DoubleConverter();
    }

    @Override
    protected NumberConverter makeConverter(final Object defaultValue) {
        return new DoubleConverter(defaultValue);
    }

    @Override
    protected Class<?> getExpectedType() {
        return Double.class;
    }

    // ------------------------------------------------------------------------

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
            String.valueOf(Double.MIN_VALUE),
            "-17.2",
            "-1.1",
            "0.0",
            "1.1",
            "17.2",
            String.valueOf(Double.MAX_VALUE),
            new Byte((byte)7),
            new Short((short)8),
            new Integer(9),
            new Long(10),
            new Float(11.1),
            new Double(12.2)
        };

        final Double[] expected = {
            new Double(Double.MIN_VALUE),
            new Double(-17.2),
            new Double(-1.1),
            new Double(0.0),
            new Double(1.1),
            new Double(17.2),
            new Double(Double.MAX_VALUE),
            new Double(7),
            new Double(8),
            new Double(9),
            new Double(10),
            new Double(11.1),
            new Double(12.2)
        };

        for(int i=0;i<expected.length;i++) {
            assertEquals(
                message[i] + " to Double",
                expected[i].doubleValue(),
                (converter.convert(Double.class,input[i])).doubleValue(),
                0.00001D);
            assertEquals(
                message[i] + " to double",
                expected[i].doubleValue(),
                (converter.convert(Double.TYPE,input[i])).doubleValue(),
                0.00001D);
            assertEquals(
                message[i] + " to null type",
                expected[i].doubleValue(),
                ((Double)(converter.convert(null,input[i]))).doubleValue(),
                0.00001D);
        }
    }

}

