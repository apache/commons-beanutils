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
import org.junit.Before;
import org.junit.Test;

import java.time.Period;

import static org.junit.Assert.assertEquals;

/**
 * Test Case for the PeriodConverter class.
 *
 */
public class PeriodConverterTestCase {

    private PeriodConverter converter;

    @Before
    public void before() {
        converter = new PeriodConverter();
    }

    @Test
    public void testSimpleConversion() throws Exception {
        final String[] message= {
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
        };

        final Object[] input = {
            "P2Y",
            "P5D",
            "P1Y2M3D"
        };

        final Period[] expected = {
            Period.parse("P2Y"),
            Period.parse("P5D"),
            Period.parse("P1Y2M3D")
        };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(message[i] + " to URI", expected[i], converter.convert(Period.class, input[i]));
            assertEquals(message[i] + " to null type", expected[i], converter.convert(null, input[i]));
        }

        for (int i = 0; i < expected.length; i++) {
            assertEquals(input[i] + " to String", input[i], converter.convert(String.class, expected[i]));
        }
    }

    /**
     * Tests a conversion to an unsupported type.
     */
    @Test(expected = ConversionException.class)
    public void testUnsupportedType() {
        converter.convert(Integer.class, "http://www.apache.org");
    }
}

