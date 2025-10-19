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

import java.time.Instant;
import java.time.Period;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.Converter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the {@link InstantConverter} class.
*/
class InstantConverterTest {

    private Converter<Instant> converter;

    protected Class<?> getExpectedType() {
        return Period.class;
    }

    protected Converter<Instant> makeConverter() {
        return new InstantConverter();
    }

    @BeforeEach
    public void setUp() throws Exception {
        converter = makeConverter();
    }

    @AfterEach
    public void tearDown() throws Exception {
        converter = null;
    }

    @Test
    void testConvertingMilliseconds() {
        final Instant expected = Instant.ofEpochMilli(1596500083605L);
        final Instant actual = converter.convert(Instant.class, 1596500083605L);
        assertEquals(expected, actual);
    }

    @Test
    void testConvertingInstantString() {
        final Instant expected = Instant.ofEpochMilli(1196676930000L);
        final Instant actual = converter.convert(Instant.class, "2007-12-03T10:15:30.00Z");
        assertEquals(expected, actual);
    }

    @Test
    void testText() {
        assertThrows(ConversionException.class, () -> converter.convert(Instant.class, "Hello, world!"));
    }

    @Test
    void testLocalizedNumber() {
        assertThrows(ConversionException.class, () -> converter.convert(Instant.class, "200,000,000,000"));
    }
}
