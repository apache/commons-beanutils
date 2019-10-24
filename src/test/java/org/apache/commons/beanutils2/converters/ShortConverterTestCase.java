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
 * Test Case for the ShortConverter class.
 *
 */

public class ShortConverterTestCase extends NumberConverterTestBase {

    public static TestSuite suite() {
        return new TestSuite(ShortConverterTestCase.class);
    }

    

    private Converter converter = null;

    

    public ShortConverterTestCase(final String name) {
        super(name);
    }

    @Override
    protected Class<?> getExpectedType() {
        return Short.class;
    }

    @Override
    protected NumberConverter makeConverter() {
        return new ShortConverter();
    }

    

    @Override
    protected NumberConverter makeConverter(final Object defaultValue) {
        return new ShortConverter(defaultValue);
    }

    @Override
    public void setUp() throws Exception {
        converter = makeConverter();
        numbers[0] = new Short("-12");
        numbers[1] = new Short("13");
        numbers[2] = new Short("-22");
        numbers[3] = new Short("23");
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
        final Class<?> clazz = Short.class;

        final Long min         = new Long(Short.MIN_VALUE);
        final Long max         = new Long(Short.MAX_VALUE);
        final Long minMinusOne = new Long(min.longValue() - 1);
        final Long maxPlusOne  = new Long(max.longValue() + 1);

        // Minimum
        assertEquals("Minimum", new Short(Short.MIN_VALUE), converter.convert(clazz, min));

        // Maximum
        assertEquals("Maximum", new Short(Short.MAX_VALUE), converter.convert(clazz, max));

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
            String.valueOf(Short.MIN_VALUE),
            "-17",
            "-1",
            "0",
            "1",
            "17",
            String.valueOf(Short.MAX_VALUE),
            new Byte((byte)7),
            new Short((short)8),
            new Integer(9),
            new Long(10),
            new Float(11.1),
            new Double(12.2)
        };

        final Short[] expected = {
            new Short(Short.MIN_VALUE),
            new Short((short)-17),
            new Short((short)-1),
            new Short((short)0),
            new Short((short)1),
            new Short((short)17),
            new Short(Short.MAX_VALUE),
            new Short((short)7),
            new Short((short)8),
            new Short((short)9),
            new Short((short)10),
            new Short((short)11),
            new Short((short)12)
        };

        for(int i=0;i<expected.length;i++) {
            assertEquals(message[i] + " to Short",expected[i],converter.convert(Short.class,input[i]));
            assertEquals(message[i] + " to short",expected[i],converter.convert(Short.TYPE,input[i]));
            assertEquals(message[i] + " to null type",expected[i],converter.convert(null,input[i]));
        }
    }
}

