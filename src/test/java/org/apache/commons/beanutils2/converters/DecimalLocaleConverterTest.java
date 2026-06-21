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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.locale.converters.DecimalLocaleConverter;
import org.apache.commons.beanutils2.locale.converters.IntegerLocaleConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests that {@link DecimalLocaleConverter} fully consumes its input and rejects trailing, unparsed characters.
 */
class DecimalLocaleConverterTest {

    private Locale origLocale;

    @BeforeEach
    public void setUp() {
        origLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    @AfterEach
    public void tearDown() {
        Locale.setDefault(origLocale);
    }

    @Test
    void testParseAcceptsGroupedNumber() {
        final IntegerLocaleConverter converter = IntegerLocaleConverter.builder().setLocale(Locale.US).get();
        assertEquals(Integer.valueOf(1234), converter.convert("1,234"));
    }

    @Test
    void testParseRejectsTrailingCharacters() {
        final IntegerLocaleConverter converter = IntegerLocaleConverter.builder().setLocale(Locale.US).get();
        assertThrows(ConversionException.class, () -> converter.convert("123abc"));
    }

    @Test
    void testParseRejectsTrailingExpression() {
        final IntegerLocaleConverter converter = IntegerLocaleConverter.builder().setLocale(Locale.US).get();
        assertThrows(ConversionException.class, () -> converter.convert("42 OR 1=1"));
    }
}
