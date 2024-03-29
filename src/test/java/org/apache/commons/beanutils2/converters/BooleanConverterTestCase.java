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

import org.apache.commons.beanutils2.ConversionException;

import junit.framework.TestCase;

/**
 */
public class BooleanConverterTestCase extends TestCase {

    public static final String[] STANDARD_TRUES = { "yes", "y", "true", "on", "1" };

    public static final String[] STANDARD_FALSES = { "no", "n", "false", "off", "0" };

    public BooleanConverterTestCase(final String name) {
        super(name);
    }

    public void testAdditionalStrings() {
        final String[] trueStrings = { "sure" };
        final String[] falseStrings = { "nope" };
        final AbstractConverter<Boolean> converter = new BooleanConverter(trueStrings, falseStrings);
        testConversionValues(converter, new String[] { "sure", "Sure" }, new String[] { "nope", "nOpE" });

        try {
            converter.convert(Boolean.class, "true");
            fail("Converting obsolete true value should have generated an exception");
        } catch (final ConversionException expected) {
            // Exception is successful test
        }
        try {
            converter.convert(Boolean.class, "bogus");
            fail("Converting invalid string should have generated an exception");
        } catch (final ConversionException expected) {
            // Exception is successful test
        }
    }

    public void testCaseInsensitivity() {
        final AbstractConverter<Boolean> converter = new BooleanConverter();
        testConversionValues(converter, new String[] { "Yes", "TRUE" }, new String[] { "NO", "fAlSe" });
    }

    /**
     * Tests a conversion to another target type. This should not be possible.
     */
    public void testConversionToOtherType() {
        final AbstractConverter<Boolean> converter = new BooleanConverter();
        try {
            converter.convert(Integer.class, STANDARD_TRUES[0]);
            fail("Could convert to unsupported type!");
        } catch (final ConversionException cex) {
            // Expected result
        }
    }

    protected void testConversionValues(final AbstractConverter<Boolean> converter, final String[] trueValues, final String[] falseValues) {

        for (final String trueValue : trueValues) {
            assertEquals(Boolean.TRUE, converter.convert(Boolean.class, trueValue));
        }
        for (final String falseValue : falseValues) {
            assertEquals(Boolean.FALSE, converter.convert(Boolean.class, falseValue));
        }
    }

    public void testDefaultValue() {
        final Boolean defaultValue = Boolean.TRUE;
        final AbstractConverter<Boolean> converter = new BooleanConverter(defaultValue);

        assertSame(defaultValue, converter.convert(Boolean.class, "bogus"));
        testConversionValues(converter, STANDARD_TRUES, STANDARD_FALSES);
    }

    public void testInvalidString() {
        final AbstractConverter<Boolean> converter = new BooleanConverter();
        try {
            converter.convert(Boolean.class, "bogus");
            fail("Converting invalid string should have generated an exception");
        } catch (final ConversionException expected) {
            // Exception is successful test
        }
    }

    /**
     * Tests whether a conversion to a primitive boolean is possible.
     */
    public void testPrimitiveTargetClass() {
        final AbstractConverter<Boolean> converter = new BooleanConverter();
        assertTrue("Wrong result", converter.convert(Boolean.TYPE, STANDARD_TRUES[0]));
    }

    public void testStandardValues() {
        final AbstractConverter<Boolean> converter = new BooleanConverter();
        testConversionValues(converter, STANDARD_TRUES, STANDARD_FALSES);
    }

}
