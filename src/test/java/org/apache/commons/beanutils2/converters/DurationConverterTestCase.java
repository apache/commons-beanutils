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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.Converter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the DurationConverter class.
 */
public class DurationConverterTestCase {

    private Converter<Duration> converter;

    protected Class<?> getExpectedType() {
        return Duration.class;
    }

    protected Converter<Duration> makeConverter() {
        return new DurationConverter();
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
    public void testSimpleConversion() throws Exception {
        final String[] message = { "from String", "from String", "from String", "from String", "from String", "from String", "from String", "from String", };

        final Object[] input = { "PT20.345S", "PT15M", "PT51H4M" };

        final Duration[] expected = { Duration.parse("PT20.345S"), Duration.parse("PT15M"), Duration.parse("P2DT3H4M") };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], converter.convert(Duration.class, input[i]), message[i] + " to URI");
            assertEquals(expected[i], converter.convert(null, input[i]), message[i] + " to null type");
        }

        for (int i = 0; i < expected.length; i++) {
            assertEquals(input[i], converter.convert(String.class, expected[i]), input[i] + " to String");
        }
    }

    /**
     * Tests a conversion to an unsupported type.
     */
    @Test
    public void testUnsupportedType() {
        assertThrows(ConversionException.class, () -> converter.convert(Integer.class, "http://www.apache.org"));
    }
}
