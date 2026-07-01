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

package org.apache.commons.beanutils2.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import org.apache.commons.beanutils2.locale.converters.StringLocaleConverter;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the StringLocaleConverter class.
 */
class StringLocaleConverterTest extends AbstractLocaleConverterTest<String> {

    /**
     * A {@link BigDecimal} beyond {@code double} precision must keep its full magnitude instead of being narrowed via {@code doubleValue()}.
     */
    @Test
    void testConvertBigDecimalKeepsMagnitude() {
        converter = StringLocaleConverter.builder().setLocale(Locale.US).get();
        final BigDecimal huge = new BigDecimal("123456789012345678901234567890");
        assertEquals("123,456,789,012,345,678,901,234,567,890", converter.convert(huge));
    }

    /**
     * A {@link BigInteger} wider than a {@code long} must keep its full magnitude instead of being narrowed via {@code longValue()}.
     */
    @Test
    void testConvertBigIntegerKeepsMagnitude() {
        converter = StringLocaleConverter.builder().setLocale(Locale.US).get();
        final BigInteger huge = new BigInteger("99999999999999999999");
        assertEquals("99,999,999,999,999,999,999", converter.convert(huge));
    }
}
