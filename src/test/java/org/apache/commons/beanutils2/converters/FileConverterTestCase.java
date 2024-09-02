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

import java.io.File;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.Converter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the FileConverter class.
 */
public class FileConverterTestCase {

    private Converter<File> converter;

    protected Class<?> getExpectedType() {
        return File.class;
    }

    protected Converter<File> makeConverter() {
        return new FileConverter();
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
        final String[] message = { "from String", "from String", "from String" };

        final Object[] input = { "/tmp", "/tmp/foo.txt", "/tmp/does/not/exist.foo" };

        final File[] expected = { new File("/tmp"), new File("/tmp/foo.txt"), new File("/tmp/does/not/exist.foo") };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], converter.convert(File.class, input[i]), message[i] + " to File");
            assertEquals(expected[i], converter.convert(null, input[i]), message[i] + " to null type");
        }
    }

    /**
     * Tries a conversion to an unsupported target type.
     */
    @Test
    public void testUnsupportedTargetType() {
        assertThrows(ConversionException.class, () -> converter.convert(Integer.class, "/tmp"));
    }
}
