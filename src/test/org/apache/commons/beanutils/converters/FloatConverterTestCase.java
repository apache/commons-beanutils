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
 * Test Case for the FloatConverter class.
 *
 * @author Rodney Waldhoff
 * @version $Revision$ $Date$
 */

public class FloatConverterTestCase extends NumberConverterTestBase {

    private Converter converter = null;

    // ------------------------------------------------------------------------

    public FloatConverterTestCase(String name) {
        super(name);
    }
    
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        converter = makeConverter();
    }

    public static TestSuite suite() {
        return new TestSuite(FloatConverterTestCase.class);        
    }

    public void tearDown() throws Exception {
        converter = null;
    }

    // ------------------------------------------------------------------------
    
    protected Converter makeConverter() {
        return new FloatConverter();
    }
    
    protected Class getExpectedType() {
        return Float.class;
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
            String.valueOf(Float.MIN_VALUE),
            "-17.2",
            "-1.1",
            "0.0",
            "1.1",
            "17.2",
            String.valueOf(Float.MAX_VALUE),
            new Byte((byte)7),
            new Short((short)8),
            new Integer(9),
            new Long(10),
            new Float(11.1),
            new Double(12.2)
        };
        
        Float[] expected = { 
            new Float(Float.MIN_VALUE),
            new Float(-17.2),
            new Float(-1.1),
            new Float(0.0),
            new Float(1.1),
            new Float(17.2),
            new Float(Float.MAX_VALUE),
            new Float(7),
            new Float(8),
            new Float(9),
            new Float(10),
            new Float(11.1),
            new Float(12.2)
        };
        
        for(int i=0;i<expected.length;i++) {
            assertEquals(
                message[i] + " to Float",
                expected[i].floatValue(),
                ((Float)(converter.convert(Float.class,input[i]))).floatValue(),
                0.00001);
            assertEquals(
                message[i] + " to float",
                expected[i].floatValue(),
                ((Float)(converter.convert(Float.TYPE,input[i]))).floatValue(),
                0.00001);
            assertEquals(
                message[i] + " to null type",
                expected[i].floatValue(),
                ((Float)(converter.convert(null,input[i]))).floatValue(),
                0.00001);
        }
    }
    
}

