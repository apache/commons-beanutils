/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.beanutils.locale.converters;

import java.util.Locale;

import org.apache.commons.beanutils.ConversionException;

/**
 * Tests that {@link DecimalLocaleConverter} fully consumes its input and rejects trailing, unparsed characters.
 */
public class DecimalLocaleConverterTest extends BaseLocaleConverterTest {

    public DecimalLocaleConverterTest(final String name) {
        super(name);
    }

    /**
     * A grouped number is still parsed in full.
     */
    public void testParseAcceptsGroupedNumber() {
        final IntegerLocaleConverter converter = new IntegerLocaleConverter(Locale.US);
        assertEquals(Integer.valueOf(1234), converter.convert("1,234"));
    }

    /**
     * Trailing characters left after a partial parse are rejected.
     */
    public void testParseRejectsTrailingCharacters() {
        final IntegerLocaleConverter converter = new IntegerLocaleConverter(Locale.US);
        try {
            converter.convert("123abc");
            fail("Expected ConversionException for trailing characters");
        } catch (final ConversionException expected) {
            // expected
        }
    }

    /**
     * A numeric prefix followed by an expression is rejected.
     */
    public void testParseRejectsTrailingExpression() {
        final IntegerLocaleConverter converter = new IntegerLocaleConverter(Locale.US);
        try {
            converter.convert("42 OR 1=1");
            fail("Expected ConversionException for trailing expression");
        } catch (final ConversionException expected) {
            // expected
        }
    }
}
