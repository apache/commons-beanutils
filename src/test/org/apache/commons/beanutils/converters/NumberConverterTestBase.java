/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;


/**
 * Abstract base for &lt;Number&gt;Converter classes.
 *
 * @author Rodney Waldhoff
 * @version $Revision$ $Date$
 */

public abstract class NumberConverterTestBase extends TestCase {

    // ------------------------------------------------------------------------

    public NumberConverterTestBase(String name) {
        super(name);
    }
    
    // ------------------------------------------------------------------------
    
    protected abstract Converter makeConverter();
    protected abstract Class getExpectedType();

    // ------------------------------------------------------------------------

    /**
     * Assumes ConversionException in response to covert(getExpectedType(),null).
     */
    public void testConvertNull() throws Exception {
        try {
            makeConverter().convert(getExpectedType(),null);
            fail("Expected ConversionException");
        } catch(ConversionException e) {
            // expected
        }
    }

    /**
     * Assumes convert(getExpectedType(),Number) returns some non-null
     * instance of getExpectedType().
     */
    public void testConvertNumber() throws Exception {
        String[] message= { 
            "from Byte",
            "from Short",
            "from Integer",
            "from Long",
            "from Float",
            "from Double"
        };

        Object[] number = {
            new Byte((byte)7),
            new Short((short)8),
            new Integer(9),
            new Long(10),
            new Float(11.1),
            new Double(12.2)
        };

        for(int i=0;i<number.length;i++) {
            Object val = makeConverter().convert(getExpectedType(),number[i]);
            assertNotNull("Convert " + message[i] + " should not be null",val);
            assertTrue(
                "Convert " + message[i] + " should return a " + getExpectedType().getName(), 
                getExpectedType().isInstance(val));
        }
    }
}

