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

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * Test Case for the ClassConverter class.
 *
 * @version $Id$
 */
public class ClassConverterTestCase extends TestCase {

    /**
     * Construct a new Class Converter test case.
     * @param name Test Name
     */
    public ClassConverterTestCase(final String name) {
        super(name);
    }

    // ------------------------------------------------------------------------

    /**
     * Create Test Suite
     * @return test suite
     */
    public static TestSuite suite() {
        return new TestSuite(ClassConverterTestCase.class);
    }

    /** Set Up */
    @Override
    public void setUp() throws Exception {
    }

    /** Tear Down */
    @Override
    public void tearDown() throws Exception {
    }


    // ------------------------------------------------------------------------

    /**
     * Test Conversion to String
     */
    public void testConvertToString() {
        final Converter converter = new ClassConverter();

        assertEquals("Class Test", "java.lang.Integer", converter.convert(String.class, Integer.class));
        assertEquals("Value Test", "foo", converter.convert(String.class, "foo"));
        assertEquals("Value Test", "bar", converter.convert(String.class, new StringBuilder("bar")));
        assertEquals("Null Test",   null, converter.convert(String.class, null));
    }

    /**
     * Test Conversion to Class
     */
    public void testConvertToClass() {
        final Converter converter = new ClassConverter();

        assertEquals("Class Test",        Integer.class, converter.convert(Class.class, Integer.class));
        assertEquals("String Test",       Integer.class, converter.convert(Class.class, "java.lang.Integer"));
        assertEquals("StringBuilder Test", Integer.class, converter.convert(Class.class, new StringBuilder("java.lang.Integer")));

        // Invalid Test
        try {
            converter.convert(Class.class, new Integer(6));
            fail("Expected invalid value to fail");
        } catch (final ConversionException e) {
            // expected result
        }

        // Test Null
        try {
            converter.convert(Class.class, null);
            fail("Expected null value to fail");
        } catch (final ConversionException e) {
            // expected result
        }
    }

    /**
     * Test Invalid Conversion with default
     */
    public void testConvertToClassDefault() {

        final Converter converter = new ClassConverter(Object.class);

        assertEquals("Invalid Test", Object.class, converter.convert(Class.class, new Integer(6)));
        assertEquals("Null Test",    Object.class, converter.convert(Class.class, null));
    }

    /**
     * Test Invalid Conversion with default "null"
     */
    public void testConvertToClassDefaultNull() {

        final Converter converter = new ClassConverter(null);

        assertEquals("Invalid Test", null, converter.convert(Class.class, new Integer(6)));
        assertEquals("Null Test",    null, converter.convert(Class.class, null));
    }

    /**
     * Test Array Conversion
     */
    public void testArray() {
        final Converter converter = new ClassConverter();

        // Test Array Class to String
        assertEquals("Array to String", "[Ljava.lang.Boolean;", converter.convert(String.class, Boolean[].class));

        // *** N.B. for some reason the following works on m1, but not m2
        // Test String to Array Class
        // assertEquals("String to Array", Boolean[].class, converter.convert(Class.class, "[Ljava.lang.Boolean;"));
    }

    /**
     * Test Invalid
     */
    public void testInvalid() {
        final Converter converter = new ClassConverter();

        // Test invalid class name
        try {
            converter.convert(Class.class, "foo.bar");
            fail("Invalid class name, expected ConversionException");
        } catch (final ConversionException e) {
            // expected result
        }
    }

    /**
     * Tries a conversion to an unsupported target type.
     */
    public void testUnsupportedTargetType() {
        final Converter converter = new ClassConverter();
        try {
            converter.convert(Integer.class, getClass().getName());
            fail("Invalid target class not detected!");
        } catch (final ConversionException cex) {
            // expected result
        }
    }
}
