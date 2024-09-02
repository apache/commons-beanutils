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

import java.net.URI;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.Converter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the URIConverter class.
 */
public class URIConverterTestCase {

    private Converter<URI> converter;

    protected Class<?> getExpectedType() {
        return URI.class;
    }

    protected Converter<URI> makeConverter() {
        return new URIConverter();
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

        final Object[] input = { "http://www.apache.org", "http://www.apache.org/", "ftp://cvs.apache.org", "file://project.xml", "http://208.185.179.12",
                "http://www.apache.org:9999/test/thing", "http://user:admin@www.apache.org:50/one/two.three", "http://notreal.apache.org", };

        final URI[] expected = { new URI("http://www.apache.org"), new URI("http://www.apache.org/"), new URI("ftp://cvs.apache.org"),
                new URI("file://project.xml"), new URI("http://208.185.179.12"), new URI("http://www.apache.org:9999/test/thing"),
                new URI("http://user:admin@www.apache.org:50/one/two.three"), new URI("http://notreal.apache.org") };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], converter.convert(URI.class, input[i]), message[i] + " to URI");
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
