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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test Case for the CharacterConverter class.
 */
public class CharacterConverterTestCase {

    private CharacterConverter converter;

    @Before
    public void before() {
        converter = new CharacterConverter();
    }

    /**
     * Tests whether the primitive char class can be passed as target type.
     */
    @Test
    public void testConvertToChar() {
        assertEquals("Wrong result", Character.valueOf('F'), converter.convert(Character.TYPE, "FOO"));
    }

    /**
     * Test Conversion to Character
     */
    @Test
    public void testConvertToCharacter() {
        assertEquals("Character Test", Character.valueOf('N'), converter.convert(Character.class, 'N'));
        assertEquals("String Test", Character.valueOf('F'), converter.convert(Character.class, "FOO"));
        assertEquals("Integer Test", Character.valueOf('3'), converter.convert(Character.class, 321));
    }

    /**
     * Tests a conversion to character for null input if no default value is provided.
     */
    @Test(expected = ConversionException.class)
    public void testConvertToCharacterNullNoDefault() {
        converter.convert(Character.class, null);
    }

    /**
     * Test Conversion to String
     */
    @Test
    public void testConvertToString() {
        assertEquals("Character Test", "N", converter.convert(String.class, 'N'));
        assertEquals("String Test", "F", converter.convert(String.class, "FOO"));
        assertEquals("Integer Test", "3", converter.convert(String.class, 321));
        assertNull("Null Test", converter.convert(String.class, null));
    }

    /**
     * Tries a conversion to an unsupported type.
     */
    @Test(expected = ConversionException.class)
    public void testConvertToUnsupportedType() {
        converter.convert(Integer.class, "Test");
    }

    /**
     * Test Conversion to Character (with default)
     */
    @Test
    public void testDefault() {
        final Converter converter = new CharacterConverter("C");
        assertEquals("Default Test", Character.valueOf('C'), converter.convert(Character.class, null));
    }

    /**
     * If a hexadecimal value is provided, we'll convert it to a character if possible.
     */
    @Test
    public void testConvertHexCharacter() {
        Assert.assertEquals('A', (char) converter.convert(Character.class, "0x41"));
    }

    @Test
    public void testConvertHexSymbol() {
        Assert.assertEquals('£', (char) converter.convert(Character.class, "0xA3"));
    }
}
