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

package org.apache.commons.beanutils.converters;

import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;


/**
 * Test Case for the FileConverter class.
 *
 * @version $Id$
 */

public class FileConverterTestCase extends TestCase {

    private Converter converter = null;

    // ------------------------------------------------------------------------

    public FileConverterTestCase(final String name) {
        super(name);
    }

    // ------------------------------------------------------------------------

    @Override
    public void setUp() throws Exception {
        converter = makeConverter();
    }

    public static TestSuite suite() {
        return new TestSuite(FileConverterTestCase.class);
    }

    @Override
    public void tearDown() throws Exception {
        converter = null;
    }

    // ------------------------------------------------------------------------

    protected Converter makeConverter() {
        return new FileConverter();
    }

    protected Class<?> getExpectedType() {
        return File.class;
    }

    // ------------------------------------------------------------------------

    public void testSimpleConversion() throws Exception {
        final String[] message= {
            "from String",
            "from String",
            "from String"
        };

        final Object[] input = {
            "/tmp",
            "/tmp/foo.txt",
            "/tmp/does/not/exist.foo"
        };

        final File[] expected = {
            new File("/tmp"),
            new File("/tmp/foo.txt"),
            new File("/tmp/does/not/exist.foo")
        };

        for(int i=0;i<expected.length;i++) {
            assertEquals(message[i] + " to File",expected[i],converter.convert(File.class,input[i]));
            assertEquals(message[i] + " to null type",expected[i],converter.convert(null,input[i]));
        }
    }

    /**
     * Tries a conversion to an unsupported target type.
     */
    public void testUnsupportedTargetType() {
        try {
            converter.convert(Integer.class, "/tmp");
            fail("Could convert to unsupported type!");
        } catch (final ConversionException cex) {
            // expected result
        }
    }
}

