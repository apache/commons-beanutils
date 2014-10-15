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
 * Test Case for the CharacterConverter class.
 *
 * @version $Id$
 */
public class CharacterConverterTestCase extends TestCase {

    /**
     * Construct a new Character Converter test case.
     * @param name Test Name
     */
    public CharacterConverterTestCase(final String name) {
        super(name);
    }

    // ------------------------------------------------------------------------

    /**
     * Create Test Suite
     * @return test suite
     */
    public static TestSuite suite() {
        return new TestSuite(CharacterConverterTestCase.class);
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

        final Converter converter = new CharacterConverter();

        assertEquals("Character Test", "N", converter.convert(String.class, new Character('N')));
        assertEquals("String Test",    "F", converter.convert(String.class, "FOO"));
        assertEquals("Integer Test",   "3", converter.convert(String.class, new Integer(321)));
        assertEquals("Null Test",     null, converter.convert(String.class, null));
    }

    /**
     * Test Conversion to Character
     */
    public void testConvertToCharacter() {
        final Converter converter = new CharacterConverter();
        assertEquals("Character Test", new Character('N'), converter.convert(Character.class, new Character('N')));
        assertEquals("String Test",    new Character('F'), converter.convert(Character.class, "FOO"));
        assertEquals("Integer Test",   new Character('3'), converter.convert(Character.class, new Integer(321)));
    }

    /**
     * Tests whether the primitive char class can be passed as target type.
     */
    public void testConvertToChar() {
        final Converter converter = new CharacterConverter();
        assertEquals("Wrong result", new Character('F'), converter.convert(Character.TYPE, "FOO"));
    }

    /**
     * Tests a conversion to character for null input if no default value is
     * provided.
     */
    public void testConvertToCharacterNullNoDefault() {
        final Converter converter = new CharacterConverter();
        try {
            converter.convert(Character.class, null);
            fail("Expected a ConversionException for null value");
        } catch (final Exception e) {
            // expected result
        }
    }

    /**
     * Test Conversion to Character (with default)
     */
    public void testDefault() {
        final Converter converter = new CharacterConverter("C");
        assertEquals("Default Test",   new Character('C'), converter.convert(Character.class, null));
    }

    /**
     * Tries a conversion to an unsupported type.
     */
    public void testConvertToUnsupportedType() {
        final Converter converter = new CharacterConverter();
        try {
            converter.convert(Integer.class, "Test");
            fail("Could convert to unsupported type!");
        } catch (final ConversionException cex) {
            // expected result
        }
    }
}
