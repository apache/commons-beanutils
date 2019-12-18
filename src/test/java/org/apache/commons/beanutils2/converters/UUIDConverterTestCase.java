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

import java.util.UUID;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.Converter;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test Case for the UUIDConverter class.
 *
 */
public class UUIDConverterTestCase extends TestCase {

    public static TestSuite suite() {
        return new TestSuite(UUIDConverterTestCase.class);
    }



    private Converter converter = null;



    public UUIDConverterTestCase(final String name) {
        super(name);
    }

    protected Class<?> getExpectedType() {
        return UUID.class;
    }

    protected Converter makeConverter() {
        return new UUIDConverter();
    }



    @Override
    public void setUp() throws Exception {
        converter = makeConverter();
    }

    @Override
    public void tearDown() throws Exception {
        converter = null;
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
            "from String",
        };

        final Object[] input = {
        	"123e4567-e89b-12d3-a456-556642440000",
        	"7dc53df5-703e-49b3-8670-b1c468f47f1f"
        };

        final UUID[] expected = {
            UUID.fromString("123e4567-e89b-12d3-a456-556642440000"),
            UUID.fromString("7dc53df5-703e-49b3-8670-b1c468f47f1f")
        };

        for(int i=0;i<expected.length;i++) {
            assertEquals(message[i] + " to URI",expected[i],converter.convert(UUID.class,input[i]));
            assertEquals(message[i] + " to null type",expected[i],converter.convert(null,input[i]));
        }

        for(int i=0;i<expected.length;i++) {
            assertEquals(input[i] + " to String", input[i], converter.convert(String.class, expected[i]));
        }
    }

    /**
     * Tests a conversion to an unsupported type.
     */
    public void testUnsupportedType() {
        try {
            converter.convert(Integer.class, "http://www.apache.org");
            fail("Unsupported type could be converted!");
        } catch (final ConversionException cex) {
            // expected result
        }
    }
}

