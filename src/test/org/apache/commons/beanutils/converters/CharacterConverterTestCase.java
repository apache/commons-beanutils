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

import org.apache.commons.beanutils.Converter;

/**
 * Test Case for the CharacterConverter class.
 *
 * @version $Revision$ $Date$
 */
public class CharacterConverterTestCase extends TestCase {

    /**
     * Construct a new Character Converter test case.
     * @param name Test Name
     */
    public CharacterConverterTestCase(String name) {
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
    public void setUp() throws Exception {
    }

    /** Tear Down */
    public void tearDown() throws Exception {
    }


    // ------------------------------------------------------------------------

    /**
     * Test Conversion to String
     */
    public void testConvertToString() {

        Converter converter = new CharacterConverter();

        assertEquals("Character Test", "N", converter.convert(String.class, new Character('N')));
        assertEquals("String Test",    "F", converter.convert(String.class, "FOO"));
        assertEquals("Integer Test",   "3", converter.convert(String.class, new Integer(321)));
        assertEquals("Null Test",     null, converter.convert(String.class, null));
    }

    /**
     * Test Conversion to Character
     */
    public void testConvertToCharacter() {
        Converter converter = new CharacterConverter();
        assertEquals("Character Test", new Character('N'), converter.convert(Character.class, new Character('N')));
        assertEquals("String Test",    new Character('F'), converter.convert(Character.class, "FOO"));
        assertEquals("Integer Test",   new Character('3'), converter.convert(Character.class, new Integer(321)));
        try {
            converter.convert(Character.class, null);
            fail("Expected a ConversionException for null value");
        } catch (Exception e) {
            // expected result
        }
    }

    /**
     * Test Conversion to Character (with default)
     */
    public void testDefault() {
        Converter converter = new CharacterConverter("C");
        assertEquals("Default Test",   new Character('C'), converter.convert(Character.class, null));
    }
}
