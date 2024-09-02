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
 * Test Case for the CharacterConverter class.
 */
public class CharacterConverterTestCase {

    /** Sets Up */
    @BeforeEach
    public void setUp() throws Exception {
    }

    /** Tear Down */
    @AfterEach
    public void tearDown() throws Exception {
    }

    /**
     * Tests whether the primitive char class can be passed as target type.
     */
    @Test
    public void testConvertToChar() {
        final Converter<Character> converter = new CharacterConverter();
        assertEquals(Character.valueOf('F'), converter.convert(Character.TYPE, "FOO"), "Wrong result");
    }

    /**
     * Test Conversion to Character
     */
    @Test
    public void testConvertToCharacter() {
        final Converter<Character> converter = new CharacterConverter();
        assertEquals(Character.valueOf('N'), converter.convert(Character.class, Character.valueOf('N')), "Character Test");
        assertEquals(Character.valueOf('F'), converter.convert(Character.class, "FOO"), "String Test");
        assertEquals(Character.valueOf('3'), converter.convert(Character.class, Integer.valueOf(321)), "Integer Test");
    }

    /**
     * Tests a conversion to character for null input if no default value is provided.
     */
    @Test
    public void testConvertToCharacterNullNoDefault() {
        final Converter<Character> converter = new CharacterConverter();
        assertThrows(ConversionException.class, () -> converter.convert(Character.class, null));
    }

    /**
     * Test Conversion to String
     */
    @Test
    @SuppressWarnings("unchecked") // testing raw conversion
    public void testConvertToString() {

        final Converter<Character> converter = new CharacterConverter();
        @SuppressWarnings("rawtypes")
        final Converter raw = converter;

        assertEquals("N", raw.convert(String.class, Character.valueOf('N')), "Character Test");
        assertEquals("F", raw.convert(String.class, "FOO"), "String Test");
        assertEquals("3", raw.convert(String.class, Integer.valueOf(321)), "Integer Test");
        assertEquals(null, raw.convert(String.class, null), "Null Test");
    }

    /**
     * Tries a conversion to an unsupported type.
     */
    @Test
    @SuppressWarnings("unchecked") // tests failure so allow mismatch
    public void testConvertToUnsupportedType() {
        @SuppressWarnings("rawtypes") // tests failure so allow mismatch
        final Converter converter = new CharacterConverter();
        assertThrows(ConversionException.class, () -> converter.convert(Integer.class, "Test"));
    }

    /**
     * Test Conversion to Character (with default)
     */
    @Test
    public void testDefault() {
        final CharacterConverter converter = new CharacterConverter('C');
        assertEquals(Character.valueOf('C'), converter.convert(Character.class, null), "Default Test");
    }
}
