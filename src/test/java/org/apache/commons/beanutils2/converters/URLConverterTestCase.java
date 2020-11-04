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

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test Case for the URLConverter class.
 */
public class URLConverterTestCase {

    private URLConverter converter;

    @Before
    public void before() {
        converter = new URLConverter();
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
            "http://www.apache.org",
            "http://www.apache.org/",
            "ftp://cvs.apache.org",
            "file://project.xml",
            "http://208.185.179.12",
            "http://www.apache.org:9999/test/thing",
            "http://user:admin@www.apache.org:50/one/two.three",
            "http://notreal.apache.org",
        };

        final String[] expected = {
            "http://www.apache.org",
            "http://www.apache.org/",
            "ftp://cvs.apache.org",
            "file://project.xml",
            "http://208.185.179.12",
            "http://www.apache.org:9999/test/thing",
            "http://user:admin@www.apache.org:50/one/two.three",
            "http://notreal.apache.org"
        };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(message[i] + " to URL", expected[i], converter.convert(URL.class, input[i]).toString());
            assertEquals(message[i] + " to null type", expected[i], converter.convert(null, input[i]).toString());
        }

        for (int i = 0; i < expected.length; i++) {
            assertEquals(input[i] + " to String", input[i], converter.convert(String.class, expected[i]));
        }
    }

    /**
     * Tests a conversion to an unsupported type.
     */
    @Test
    public void testUnsupportedType() {
        try {
            converter.convert(Integer.class, "http://www.apache.org");
            fail("Unsupported type could be converted!");
        } catch (final ConversionException cex) {
            // expected result
        }
    }
}
