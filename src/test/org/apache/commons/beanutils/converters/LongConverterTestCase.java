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

import junit.framework.TestSuite;

import org.apache.commons.beanutils.Converter;


/**
 * Test Case for the LongConverter class.
 *
 * @author Rodney Waldhoff
 * @version $Revision$ $Date$
 */

public class LongConverterTestCase extends NumberConverterTestBase {

    private Converter converter = null;

    // ------------------------------------------------------------------------

    public LongConverterTestCase(String name) {
        super(name);
    }
    
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        converter = makeConverter();
    }

    public static TestSuite suite() {
        return new TestSuite(LongConverterTestCase.class);        
    }

    public void tearDown() throws Exception {
        converter = null;
    }

    // ------------------------------------------------------------------------
    
    protected Converter makeConverter() {
        return new LongConverter();
    }
    
    protected Class getExpectedType() {
        return Long.class;
    }

    // ------------------------------------------------------------------------

    public void testSimpleConversion() throws Exception {
        String[] message= { 
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from Byte",
            "from Short",
            "from Integer",
            "from Long",
            "from Float",
            "from Double"
        };
        
        Object[] input = { 
            String.valueOf(Long.MIN_VALUE),
            "-17",
            "-1",
            "0",
            "1",
            "17",
            String.valueOf(Long.MAX_VALUE),
            new Byte((byte)7),
            new Short((short)8),
            new Integer(9),
            new Long(10),
            new Float(11.1),
            new Double(12.2)
        };
        
        Long[] expected = { 
            new Long(Long.MIN_VALUE),
            new Long(-17),
            new Long(-1),
            new Long(0),
            new Long(1),
            new Long(17),
            new Long(Long.MAX_VALUE),
            new Long(7),
            new Long(8),
            new Long(9),
            new Long(10),
            new Long(11),
            new Long(12)
        };
        
        for(int i=0;i<expected.length;i++) {
            assertEquals(message[i] + " to Long",expected[i],converter.convert(Long.class,input[i]));
            assertEquals(message[i] + " to long",expected[i],converter.convert(Long.TYPE,input[i]));
            assertEquals(message[i] + " to null type",expected[i],converter.convert(null,input[i]));
        }
    }
    
}

