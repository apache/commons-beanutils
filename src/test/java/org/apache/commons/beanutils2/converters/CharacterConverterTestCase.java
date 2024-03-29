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
import org.apache.commons.beanutils2.Converter;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test Case for the CharacterConverter class.
 */
public class CharacterConverterTestCase extends TestCase {

    /**
     * Create Test Suite
     *
     * @return test suite
     */
    public static TestSuite suite() {
        return new TestSuite(CharacterConverterTestCase.class);
    }

    /**
     * Constructs a new Character Converter test case.
     *
     * @param name Test Name
     */
    public CharacterConverterTestCase(final String name) {
        super(name);
    }

    /** Sets Up */
    @Override
    public void setUp() throws Exception {
    }

    /** Tear Down */
    @Override
    public void tearDown() throws Exception {
    }

    /**
     * Tests whether the primitive char class can be passed as target type.
     */
    public void testConvertToChar() {
        final Converter<Character> converter = new CharacterConverter();
        assertEquals("Wrong result", Character.valueOf('F'), converter.convert(Character.TYPE, "FOO"));
    }

    /**
     * Test Conversion to Character
     */
    public void testConvertToCharacter() {
        final Converter<Character> converter = new CharacterConverter();
        assertEquals("Character Test", Character.valueOf('N'), converter.convert(Character.class, Character.valueOf('N')));
        assertEquals("String Test", Character.valueOf('F'), converter.convert(Character.class, "FOO"));
        assertEquals("Integer Test", Character.valueOf('3'), converter.convert(Character.class, Integer.valueOf(321)));
    }

    /**
     * Tests a conversion to character for null input if no default value is provided.
     */
    public void testConvertToCharacterNullNoDefault() {
        final Converter<Character> converter = new CharacterConverter();
        try {
            converter.convert(Character.class, null);
            fail("Expected a ConversionException for null value");
        } catch (final Exception e) {
            // expected result
        }
    }

    /**
     * Test Conversion to String
     */
    @SuppressWarnings("unchecked") // testing raw conversion
    public void testConvertToString() {

        final Converter<Character> converter = new CharacterConverter();
        @SuppressWarnings("rawtypes")
        final Converter raw = converter;

        assertEquals("Character Test", "N", raw.convert(String.class, Character.valueOf('N')));
        assertEquals("String Test", "F", raw.convert(String.class, "FOO"));
        assertEquals("Integer Test", "3", raw.convert(String.class, Integer.valueOf(321)));
        assertEquals("Null Test", null, raw.convert(String.class, null));
    }

    /**
     * Tries a conversion to an unsupported type.
     */
    @SuppressWarnings("unchecked") // tests failure so allow mismatch
    public void testConvertToUnsupportedType() {
        @SuppressWarnings("rawtypes") // tests failure so allow mismatch
        final Converter converter = new CharacterConverter();
        try {
            converter.convert(Integer.class, "Test");
            fail("Could convert to unsupported type!");
        } catch (final ConversionException cex) {
            // expected result
        }
    }

    /**
     * Test Conversion to Character (with default)
     */
    public void testDefault() {
        final CharacterConverter converter = new CharacterConverter('C');
        assertEquals("Default Test", Character.valueOf('C'), converter.convert(Character.class, null));
    }
}
