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

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.Converter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the ClassConverter class.
 */
public class ClassConverterTestCase {

    /** Sets Up */
    @BeforeEach
    public void setUp() throws Exception {
    }

    /** Tear Down */
    @AfterEach
    public void tearDown() throws Exception {
    }

    /**
     * Test Array Conversion
     */
    @Test
    public void testArray() {
        final Converter<Class<?>> converter = new ClassConverter();
        // Test Array Class to String
        assertEquals("[Ljava.lang.Boolean;", converter.convert(String.class, Boolean[].class), "Array to String");
        // *** N.B. for some reason the following works on m1, but not m2
        // Test String to Array Class
        // assertEquals("String to Array", Boolean[].class, converter.convert(Class.class, "[Ljava.lang.Boolean;"));
    }

    /**
     * Test Conversion to Class
     */
    @Test
    public void testConvertToClass() {
        final Converter<Class<?>> converter = new ClassConverter();
        assertEquals(Integer.class, converter.convert(Class.class, Integer.class), "Class Test");
        assertEquals(Integer.class, converter.convert(Class.class, "java.lang.Integer"), "String Test");
        assertEquals(Integer.class, converter.convert(Class.class, new StringBuilder("java.lang.Integer")), "StringBuilder Test");
        // Invalid Test
        assertThrows(ConversionException.class, () -> converter.convert(Class.class, Integer.valueOf(6)));
        // Test Null
        assertThrows(ConversionException.class, () -> converter.convert(Class.class, null));
    }

    /**
     * Test Invalid Conversion with default
     */
    @Test
    public void testConvertToClassDefault() {
        final Converter<Class<?>> converter = new ClassConverter(Object.class);
        assertEquals(Object.class, converter.convert(Class.class, Integer.valueOf(6)), "Invalid Test");
        assertEquals(Object.class, converter.convert(Class.class, null), "Null Test");
    }

    /**
     * Test Invalid Conversion with default "null"
     */
    @Test
    public void testConvertToClassDefaultNull() {
        final Converter<Class<?>> converter = new ClassConverter(null);
        assertEquals(null, converter.convert(Class.class, Integer.valueOf(6)), "Invalid Test");
        assertEquals(null, converter.convert(Class.class, null), "Null Test");
    }

    /**
     * Test Conversion to String
     */
    @Test
    public void testConvertToString() {
        final Converter<Class<?>> converter = new ClassConverter();
        assertEquals("java.lang.Integer", converter.convert(String.class, Integer.class), "Class Test");
        assertEquals("foo", converter.convert(String.class, "foo"), "Value Test");
        assertEquals("bar", converter.convert(String.class, new StringBuilder("bar")), "Value Test");
        assertEquals(null, converter.convert(String.class, null), "Null Test");
    }

    /**
     * Test Invalid
     */
    @Test
    public void testInvalid() {
        final Converter<Class<?>> converter = new ClassConverter();
        // Test invalid class name
        assertThrows(ConversionException.class, () -> converter.convert(Class.class, "foo.bar"));
    }

    /**
     * Tries a conversion to an unsupported target type.
     */
    @Test
    public void testUnsupportedTargetType() {
        final Converter<Class<?>> converter = new ClassConverter();
        assertThrows(ConversionException.class, () -> converter.convert(Integer.class, getClass().getName()));
    }
}
